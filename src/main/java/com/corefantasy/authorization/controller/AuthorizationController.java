package com.corefantasy.authorization.controller;

import io.micronaut.http.annotation.*;
import io.micronaut.security.annotation.Secured;
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
}
