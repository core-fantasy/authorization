package com.corefantasy.authorization.authentication;

import io.micronaut.security.authentication.UserDetails;

import java.util.Collection;

public class CoreFantasyUserDetails extends UserDetails {

    private final String provider;
    private final String providerId;

    public CoreFantasyUserDetails(String provider, String providerId, Collection<String> roles) {
        super(provider + providerId, roles);
        this.provider = provider;
        this.providerId = providerId;
    }

    public String getProvider() {
        return provider;
    }

    public String getProviderId() {
        return providerId;
    }
}
