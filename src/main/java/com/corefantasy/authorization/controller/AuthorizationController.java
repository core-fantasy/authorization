package com.corefantasy.authorization.controller;

import io.micronaut.http.annotation.*;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller("/v1")
public class AuthorizationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizationController.class);

    @Get(uri = "/authorized")
    @Secured({"ROLE_USER"})
    public boolean authorized() {
        return true;
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
