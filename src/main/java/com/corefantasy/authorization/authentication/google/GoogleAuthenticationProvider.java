package com.corefantasy.authorization.authentication.google;

import com.corefantasy.authorization.user.UserInterface;
import com.corefantasy.authorization.user.RegisteredUser;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import io.micronaut.context.annotation.Value;
import io.micronaut.security.authentication.*;
import io.reactivex.Flowable;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Singleton
public class GoogleAuthenticationProvider implements AuthenticationProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(GoogleAuthenticationProvider.class);

    @Value("${com.corefantasy.google-id}")
    private String CLIENT_ID;

    @Inject
    private UserInterface userInterface;

    @Override
    public Publisher<AuthenticationResponse> authenticate(AuthenticationRequest authenticationRequest) {
        String token = (String) authenticationRequest.getSecret();
        return Flowable.just(validateToken(token));
    }

    private AuthenticationResponse validateToken(String token) {
        try {
            LOGGER.info("Logging in using Google, with token: {}", token);
            NetHttpTransport transport = new NetHttpTransport.Builder().build();

            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, JacksonFactory.getDefaultInstance())
                    .setAudience(Collections.singletonList(CLIENT_ID))
                    .build();

            GoogleIdToken idToken = verifier.verify(token);
            if (idToken != null) {
                Payload payload = idToken.getPayload();

                String userId = "google/" + payload.getSubject();
                String email = payload.getEmail();
                String name = (String) payload.get("name");

                RegisteredUser registeredUser = userInterface.registerUser(userId, name, email);

                return new UserDetails(registeredUser.getId(), registeredUser.getRoles());
            }
            LOGGER.warn("Invalid Google login ID token was provide, '{}'.", token);
            return new AuthenticationFailed(AuthenticationFailureReason.CREDENTIALS_DO_NOT_MATCH);
        }
        catch (GeneralSecurityException | IOException e) {
            LOGGER.error("Error verifying Google login ID token,'{}'. ", token, e);
            return new AuthenticationFailed(AuthenticationFailureReason.UNKNOWN);
        }
    }

    private AuthenticationResponse testing(String token) {
        LOGGER.info("Logging in using test, with token: {}", token);

        RegisteredUser registeredUser = userInterface.registerUser("user-id-123", "my name", "email@mail.org");
        LOGGER.info("Response: {}", registeredUser);
        return new UserDetails(registeredUser.getId(), registeredUser.getRoles());
    }
}