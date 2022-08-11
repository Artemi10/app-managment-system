package com.devanmejia.appmanager.security;

import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class JwtAuthenticationManager implements AuthenticationManager {
    private final List<AuthenticationProvider> providers;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        return providers.stream()
                .filter(provider -> provider.supports(authentication.getClass()))
                .findFirst()
                .map(provider -> provider.authenticate(authentication))
                .orElseThrow(() -> new ProviderNotFoundException("Credentials are invalid"));
    }
}
