package com.devanmejia.appmanager.configuration.security;

import org.springframework.security.core.AuthenticationException;

public class JwtAuthenticationException extends AuthenticationException {
    public JwtAuthenticationException() {
        super("Access token is invalid");
    }
}
