package com.devanmejia.appmanager.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
class JwtAuthenticationManagerTest {
    private final AuthenticationManager authenticationManager;
    private final AuthenticationProvider jwtProvider;
    private final AuthenticationProvider oauthProvider;

    @Autowired
    public JwtAuthenticationManagerTest() {
        this.jwtProvider = spy(AuthenticationProvider.class);
        this.oauthProvider = spy(AuthenticationProvider.class);
        this.authenticationManager = new JwtAuthenticationManager(
                List.of(jwtProvider, oauthProvider)
        );
    }

    @BeforeEach
    public void initMocks() {
        when(jwtProvider.supports(UsernamePasswordAuthenticationToken.class))
                .thenReturn(true);
        when(jwtProvider.supports(argThat(token -> !token.equals(UsernamePasswordAuthenticationToken.class))))
                .thenReturn(false);
        when(jwtProvider.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken("principal", "credentials"));
        when(oauthProvider.supports(OAuth2AuthenticationToken.class))
                .thenReturn(true);
        when(oauthProvider.supports(argThat(token -> !token.equals(OAuth2AuthenticationToken.class))))
                .thenReturn(false);
        when(oauthProvider.authenticate(any(OAuth2AuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken("principal", "credentials"));
    }

    @Test
    public void jwt_authenticate_Test() {
        var jwtAuthentication = new UsernamePasswordAuthenticationToken("principal", "credentials");
        assertDoesNotThrow(() -> authenticationManager.authenticate(jwtAuthentication));
        verify(jwtProvider, times(1)).authenticate(jwtAuthentication);
        verify(oauthProvider, times(0)).authenticate(jwtAuthentication);
    }

    @Test
    public void oauth_authenticate_Test() {
        var emptyUser = new DefaultOAuth2User(
                Collections.emptyList(),
                Map.of("attributeKey", "attributeValue"),
                "attributeKey");
        var oauthAuthentication = new OAuth2AuthenticationToken(
                emptyUser,
                Collections.emptyList(),
                "authorizedClientRegistrationId");
        assertDoesNotThrow(() -> authenticationManager.authenticate(oauthAuthentication));
        verify(jwtProvider, times(0)).authenticate(oauthAuthentication);
        verify(oauthProvider, times(1)).authenticate(oauthAuthentication);
    }

    @Test
    public void throw_Exception_When_authenticate_If_Authentication_Is_Not_Supported_Test() {
        var authentication = new AnonymousAuthenticationToken(
                "key",
                Collections.emptyMap(),
                List.of(new SimpleGrantedAuthority("Authority")));
        var exception = assertThrows(
                ProviderNotFoundException.class,
                () -> authenticationManager.authenticate(authentication));
        assertEquals("Credentials are invalid", exception.getMessage());
        verify(jwtProvider, times(0)).authenticate(authentication);
        verify(oauthProvider, times(0)).authenticate(authentication);
    }
}
