package com.devanmejia.appmanager.security.providers;

import com.devanmejia.appmanager.entity.user.Authority;
import com.devanmejia.appmanager.security.JwtAuthenticationException;
import com.devanmejia.appmanager.security.details.UserPrincipal;
import com.devanmejia.appmanager.security.token.AccessTokenService;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
class JwtProviderTest {
    private final JwtProvider jwtProvider;
    private final UserDetailsService userDetailsService;
    private final AccessTokenService accessTokenService;

    @Autowired
    public JwtProviderTest() {
        this.userDetailsService = spy(UserDetailsService.class);
        this.accessTokenService = spy(AccessTokenService.class);
        this.jwtProvider = new JwtProvider(userDetailsService, accessTokenService);
    }

    @BeforeEach
    public void initMocks() {
        var exceptionToken = "9c8d265b-4c41-41f0-82fd-f11a7d9702cc";
        when(accessTokenService.isValid(exceptionToken)).thenThrow(JwtException.class);
        var invalidToken = "a7aa1b9e-55ac-4fec-a00f-46d8cf8364fb";
        when(accessTokenService.isValid(invalidToken)).thenReturn(false);
        var validToken = "73912343-05d6-495a-90c4-e7ca0678127a";
        when(accessTokenService.isValid(validToken)).thenReturn(true);
        when(accessTokenService.getEmail(validToken)).thenReturn("lyah.artem10@mail.ru");
        when(accessTokenService.getAuthorityName(validToken)).thenReturn(Authority.ACTIVE.name());
        var tokenWithInvalidAuthority = "5301c3c9-2862-4401-b63c-949131659cef";
        when(accessTokenService.isValid(tokenWithInvalidAuthority)).thenReturn(true);
        when(accessTokenService.getEmail(tokenWithInvalidAuthority)).thenReturn("lyah.artem11@mail.ru");
        when(accessTokenService.getAuthorityName(tokenWithInvalidAuthority)).thenReturn(Authority.ACTIVE.name());

        when(userDetailsService.loadUserByUsername("lyah.artem10@mail.ru"))
                .thenReturn(
                        new UserPrincipal(
                                1,
                                "lyah.artem10@mail.ru",
                                "qwerty",
                                List.of(new SimpleGrantedAuthority(Authority.ACTIVE.name()))));
        when(userDetailsService.loadUserByUsername("lyah.artem11@mail.ru"))
                .thenReturn(
                        new UserPrincipal(
                                2,
                                "lyah.artem11@mail.ru",
                                "qwerty",
                                List.of(new SimpleGrantedAuthority(Authority.UPDATE_CONFIRMED.name()))));
    }

    @Test
    public void supports_Test() {
        assertTrue(jwtProvider.supports(UsernamePasswordAuthenticationToken.class));
        assertFalse(jwtProvider.supports(AnonymousAuthenticationToken.class));
        assertFalse(jwtProvider.supports(OAuth2AuthenticationToken.class));
    }

    @Test
    public void authenticate_Test() {
        var validAuthToken = new UsernamePasswordAuthenticationToken(
                "73912343-05d6-495a-90c4-e7ca0678127a",
                "73912343-05d6-495a-90c4-e7ca0678127a"
        );
        var authentication = assertDoesNotThrow(() -> jwtProvider.authenticate(validAuthToken));
        var authority = new SimpleGrantedAuthority(Authority.ACTIVE.name());
        assertTrue(authentication.isAuthenticated());
        assertEquals(1, ((UserPrincipal) authentication.getPrincipal()).id());
        assertEquals("lyah.artem10@mail.ru", ((UserPrincipal) authentication.getPrincipal()).email());
        assertEquals("qwerty", ((UserPrincipal) authentication.getPrincipal()).password());
        assertEquals(1, ((UserPrincipal) authentication.getPrincipal()).authorities().size());
        assertTrue(((UserPrincipal) authentication.getPrincipal()).authorities().contains(authority));
        assertEquals("lyah.artem10@mail.ru", authentication.getCredentials());
        assertEquals(1, authentication.getAuthorities().size());
        assertTrue(authentication.getAuthorities().contains(authority));
        verify(accessTokenService, times(1))
                .isValid("73912343-05d6-495a-90c4-e7ca0678127a");
        verify(accessTokenService, times(1))
                .getEmail("73912343-05d6-495a-90c4-e7ca0678127a");
        verify(accessTokenService, times(1))
                .getAuthorityName("73912343-05d6-495a-90c4-e7ca0678127a");
        verify(userDetailsService, times(1))
                .loadUserByUsername("lyah.artem10@mail.ru");
    }

    @Test
    public void throws_Exception_When_authenticate_If_Authentication_Is_Null_Test() {
        var exception = assertThrows(
                JwtAuthenticationException.class,
                () -> jwtProvider.authenticate(null));
        assertEquals("Access token is invalid", exception.getMessage());
        verify(accessTokenService, times(0)).isValid(anyString());
        verify(accessTokenService, times(0)).getEmail(anyString());
        verify(accessTokenService, times(0)).getAuthorityName(anyString());
        verify(userDetailsService, times(0)).loadUserByUsername(anyString());
    }

    @Test
    public void throws_Exception_When_authenticate_If_Authentication_Token_Is_Not_Valid_Test() {
        var invalidAuthToken = new UsernamePasswordAuthenticationToken(
                "a7aa1b9e-55ac-4fec-a00f-46d8cf8364fb",
                "a7aa1b9e-55ac-4fec-a00f-46d8cf8364fb"
        );
        var exception = assertThrows(
                JwtAuthenticationException.class,
                () -> jwtProvider.authenticate(invalidAuthToken));
        assertEquals("Access token is invalid", exception.getMessage());
        verify(accessTokenService, times(1))
                .isValid("a7aa1b9e-55ac-4fec-a00f-46d8cf8364fb");
        verify(accessTokenService, times(0))
                .getEmail("a7aa1b9e-55ac-4fec-a00f-46d8cf8364fb");
        verify(accessTokenService, times(0))
                .getAuthorityName("a7aa1b9e-55ac-4fec-a00f-46d8cf8364fb");
        verify(userDetailsService, times(0))
                .loadUserByUsername(anyString());
    }

    @Test
    public void throws_Exception_When_authenticate_If_Authentication_Token_Has_Invalid_Authority_Test() {
        var tokenWithInvalidAuthority = new UsernamePasswordAuthenticationToken(
                "5301c3c9-2862-4401-b63c-949131659cef",
                "5301c3c9-2862-4401-b63c-949131659cef"
        );
        var exception = assertThrows(
                JwtAuthenticationException.class,
                () -> jwtProvider.authenticate(tokenWithInvalidAuthority));
        assertEquals("Access token is invalid", exception.getMessage());
        verify(accessTokenService, times(1))
                .isValid("5301c3c9-2862-4401-b63c-949131659cef");
        verify(accessTokenService, times(1))
                .getEmail("5301c3c9-2862-4401-b63c-949131659cef");
        verify(accessTokenService, times(1))
                .getAuthorityName("5301c3c9-2862-4401-b63c-949131659cef");
        verify(userDetailsService, times(1))
                .loadUserByUsername("lyah.artem11@mail.ru");
    }

    @Test
    public void throws_Exception_When_authenticate_If_Authentication_Token_Produces_Exception_Test() {
        var tokenWithInvalidAuthority = new UsernamePasswordAuthenticationToken(
                "9c8d265b-4c41-41f0-82fd-f11a7d9702cc",
                "9c8d265b-4c41-41f0-82fd-f11a7d9702cc"
        );
        var exception = assertThrows(
                JwtAuthenticationException.class,
                () -> jwtProvider.authenticate(tokenWithInvalidAuthority));
        assertEquals("Access token is invalid", exception.getMessage());
        verify(accessTokenService, times(1))
                .isValid("9c8d265b-4c41-41f0-82fd-f11a7d9702cc");
        verify(accessTokenService, times(0))
                .getEmail("9c8d265b-4c41-41f0-82fd-f11a7d9702cc");
        verify(accessTokenService, times(0))
                .getAuthorityName("9c8d265b-4c41-41f0-82fd-f11a7d9702cc");
        verify(userDetailsService, times(0))
                .loadUserByUsername(anyString());
    }
}
