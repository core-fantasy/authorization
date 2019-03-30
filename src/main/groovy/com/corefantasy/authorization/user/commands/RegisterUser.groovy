package com.corefantasy.authorization.user.commands

import groovy.transform.ToString

@ToString(includeNames = true)
class RegisterUser {
    String id
    String name
    String email
}
