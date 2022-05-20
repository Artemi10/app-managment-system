package com.devanmejia.appmanager.configuration.security.providers;

import com.devanmejia.appmanager.configuration.security.JwtAuthenticationException;
import com.devanmejia.appmanager.configuration.security.details.UserPrincipalDetailsService;
import com.devanmejia.appmanager.configuration.security.token.AccessTokenService;
import io.jsonwebtoken.JwtException;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class JwtProvider implements AuthenticationProvider {
    private final UserDetailsService userDetailsService;
    private final AccessTokenService accessTokenService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        try {
            if (authentication == null) {
                throw new JwtAuthenticationException();
            }
            var accessToken = authentication.getCredentials().toString();
            if (!accessTokenService.isValid(accessToken)) {
                throw new JwtAuthenticationException();
            }
            var email = accessTokenService.getEmail(accessToken);
            var tokenAuthority = accessTokenService.getAuthorityName(accessToken);
            var principal = userDetailsService.loadUserByUsername(email);
            var hasAuthority = principal.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .anyMatch(authority -> authority.equals(tokenAuthority));
            if (hasAuthority) {
                return new UsernamePasswordAuthenticationToken(principal, email, principal.getAuthorities());
            }
            throw new JwtAuthenticationException();
        } catch (JwtException exception) {
            throw new JwtAuthenticationException();
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
