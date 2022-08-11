package com.devanmejia.appmanager.security.details;

import com.devanmejia.appmanager.entity.user.Authority;
import com.devanmejia.appmanager.entity.user.User;
import com.devanmejia.appmanager.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
class UserPrincipalDetailsServiceTest {
    private final UserPrincipalDetailsService userDetailsService;
    private final UserRepository userRepository;

    public UserPrincipalDetailsServiceTest() {
        this.userRepository = spy(UserRepository.class);
        this.userDetailsService = new UserPrincipalDetailsService(this.userRepository);
    }

    @BeforeEach
    public void initMocks() {
        when(userRepository.findByEmail("lyah.artem10@mail.ru"))
                .thenReturn(Optional.of(User.builder()
                        .id(1)
                        .email("lyah.artem10@mail.ru")
                        .password("qwerty")
                        .authority(Authority.ACTIVE)
                        .build()));
        when(userRepository.findByEmail("lyah.artem11@mail.ru"))
                .thenReturn(Optional.empty());
    }

    @Test
    public void loadUserByUsername_Test() {
        var actualPrincipals = assertDoesNotThrow(() -> userDetailsService.loadUserByUsername("lyah.artem10@mail.ru"));
        assertEquals("lyah.artem10@mail.ru", actualPrincipals.getUsername());
        assertEquals("qwerty", actualPrincipals.getPassword());
        assertEquals(1, actualPrincipals.getAuthorities().size());
        assertTrue(actualPrincipals.getAuthorities().contains(new SimpleGrantedAuthority(Authority.ACTIVE.name())));
        assertTrue(actualPrincipals.isAccountNonExpired());
        assertTrue(actualPrincipals.isCredentialsNonExpired());
        assertTrue(actualPrincipals.isAccountNonLocked());
        assertTrue(actualPrincipals.isEnabled());
    }

    @Test
    public void throw_Exception_When_loadUserByUsername_If_User_Not_Found_Test() {
        var exception = assertThrows(
                BadCredentialsException.class,
                () -> userDetailsService.loadUserByUsername("lyah.artem11@mail.ru"));
        assertEquals("Credentials are invalid", exception.getMessage());
    }
}
