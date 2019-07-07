package com.corefantasy.authorization.controller;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import io.micronaut.http.annotation.*;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

@Controller("/v1")
public class AuthorizationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizationController.class);

    /**
     * Endpoint used to check if a user is authorized. This is also used by the re-direct URLs
     * in application.yaml. A 200 means login was successful, anything else indicates a failure.
     * It is assumed that ALL users will have the ROLE_USER role.
     *
     * @return true if user is authorized, 4xx if not.
     */
    @Get(uri = "/authorized")
    @Secured({"ROLE_USER"})
    public Map<String, String> authorized(@CookieValue(value = "JWT") String jwtCookie) {
        Map<String, String> responseBody = new HashMap<>();
        try {
            JWT jwt = JWTParser.parse(jwtCookie);
            String provider = (String) jwt.getJWTClaimsSet().getClaim("core-fantasy.com/provider");
            responseBody.put("provider", provider);
        }
        catch (ParseException pe) {
            LOGGER.error("Unexpected error parsing JWT token '{}'.", jwtCookie, pe);
        }
        return responseBody;
    }

    /**
     * Dummy endpoint to test access. Can be deleted.
     * @return
     */
    @Get("/test")
    @Secured(SecurityRule.IS_ANONYMOUS)
    public String test() {
        LOGGER.error("logging some data");
        return "test endpoint\n";
    }
}
