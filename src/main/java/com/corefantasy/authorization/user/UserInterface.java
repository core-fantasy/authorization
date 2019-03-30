package com.corefantasy.authorization.user;

import com.corefantasy.authorization.user.commands.RegisterUser;
import io.micronaut.context.annotation.Value;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
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

    @Client("http://user")
    @Inject
    private RxHttpClient userClient;

    @Value("${micronaut.security.token.jwt.cookie.cookie-name}")
    private String jwtCookieName;

    @Inject
    private TokenGenerator tokenGenerator;

    public RegisteredUser registerUser(String userId, String name, String email) {
        RegisterUser registerUser = new RegisterUser();
        registerUser.setId(userId);
        registerUser.setName(name);
        registerUser.setEmail(email);

        // TODO: add in error handling, especially 409 for already registered user

        // This is a hack. I want the /register endpoint to not be available to any users or
        // really anything outside of the k8s cluster. I couldn't think of any way to do this
        // with Micronaut's security. A self-made token with a special user was the best I
        // could come up with.
        Optional<String> token = tokenGenerator.generateToken(
                new UserDetails("internal user", Collections.singletonList("INTERNAL_USER")),
                5 * 1000);

        Flowable<HttpResponse<RegisteredUser>> call = userClient.exchange(
                POST("/v1/register", registerUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON_TYPE)
                        .cookie(Cookie.of(jwtCookieName, token.get())),
                RegisteredUser.class
        );
        HttpResponse<RegisteredUser> response = call.blockingFirst();

        LOGGER.info("Response: {}", response.code());
        return response.body();
    }
}
