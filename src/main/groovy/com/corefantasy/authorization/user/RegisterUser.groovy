package com.corefantasy.authorization.user

import groovy.transform.ToString

@ToString(includeNames = true)
class RegisterUser {
    String provider
    String providerId
    String name
    String email
}
