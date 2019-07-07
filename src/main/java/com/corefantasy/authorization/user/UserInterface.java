package com.corefantasy.authorization.user;

import io.micronaut.context.annotation.Value;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.http.cookie.Cookie;
import io.micronaut.security.authentication.UserDetails;
import io.micronaut.security.token.generator.TokenGenerator;
import io.reactivex.Flowable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collections;
import java.util.Optional;

import static io.micronaut.http.HttpRequest.POST;

@Singleton
public class UserInterface {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserInterface.class);

    @Client("http://user:8080")
    @Inject
    private RxHttpClient userClient;

    @Value("${micronaut.security.token.jwt.cookie.cookie-name}")
    private String jwtCookieName;

    @Inject
    private TokenGenerator tokenGenerator;

    public RegisteredUser registerUser(String provider, String providerId, String name, String email)
            throws HttpClientResponseException {
        RegisterUser registerUser = new RegisterUser();
        registerUser.setProvider(provider);
        registerUser.setProviderId(providerId);
        registerUser.setName(name);
        registerUser.setEmail(email);

        String token = generateToken();

        var request = POST("/v1/register", registerUser)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .cookie(Cookie.of(jwtCookieName, token));

        Flowable<HttpResponse<RegisteredUser>> call = userClient.exchange(request, RegisteredUser.class);
        HttpResponse<RegisteredUser> response = call.blockingFirst();
        return response.body();
    }

    private String generateToken() {
        // This is a hack. I want the /register endpoint to not be available to any users or
        // really anything outside of the k8s cluster. I couldn't think of any way to do this
        // with Micronaut's security. A self-made token with a special user was the best I
        // could come up with.
        Optional<String> token = tokenGenerator.generateToken(
                new UserDetails("internal user", Collections.singletonList("INTERNAL_USER")),
                5 * 1000);
        return token.get();
    }
}
