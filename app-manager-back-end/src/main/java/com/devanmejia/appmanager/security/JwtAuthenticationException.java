package com.devanmejia.appmanager.security;

import org.springframework.security.core.AuthenticationException;

public class JwtAuthenticationException extends AuthenticationException {
    public JwtAuthenticationException() {
        super("Access token is invalid");
    }
}
