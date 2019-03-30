package com.corefantasy.authorization.user

import groovy.transform.ToString

@ToString(includeNames = true)
class RegisteredUser {
    String id
    List<String> roles = []
}
