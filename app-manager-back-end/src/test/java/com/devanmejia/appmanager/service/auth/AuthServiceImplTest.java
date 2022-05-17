package com.devanmejia.appmanager.service.auth;

import com.devanmejia.appmanager.configuration.security.token.AccessTokenService;
import com.devanmejia.appmanager.configuration.security.token.JwtService;
import com.devanmejia.appmanager.entity.user.Authority;
import com.devanmejia.appmanager.entity.user.User;
import com.devanmejia.appmanager.repository.UserRepository;
import com.devanmejia.appmanager.transfer.auth.LogInDTO;
import com.devanmejia.appmanager.transfer.auth.SignUpDTO;
import com.devanmejia.appmanager.transfer.auth.token.Token;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
class AuthServiceImplTest {
    private final AuthService authService;
    private final AccessTokenService accessTokenService;
    private final UserRepository userRepository;

    @Autowired
    public AuthServiceImplTest() {
        this.accessTokenService = spy(AccessTokenService.class);
        this.userRepository = spy(UserRepository.class);
        this.authService = new AuthServiceImpl(accessTokenService, new BCryptPasswordEncoder(), userRepository);
    }

    @BeforeEach
    public void initMocks() {
        var user = User.builder()
                .id(1)
                .email("lyah.artem10@mail.ru")
                .password("$2a$10$3kVWnJcACqfBKzhiA//1MeJ/ex1PylaWC7esjmVwSzePHGW6AQmhu")
                .refreshToken("7b69ab6d-4767-48d8-a20f-25a2340e1405")
                .authority(Authority.ACTIVE)
                .build();
        when(userRepository.findByEmail("lyah.artem10@mail.ru"))
                .thenReturn(Optional.of(user));
        when(userRepository.findByEmail("lyah.artem10@gmail.com"))
                .thenReturn(Optional.empty());
        when(accessTokenService.createAccessToken("lyah.artem10@mail.ru", Authority.ACTIVE))
                .thenReturn("accessToken");
        when(accessTokenService.getEmail("accessToken"))
                .thenReturn("lyah.artem10@mail.ru");
        when(accessTokenService.getEmail("incorrectAccessToken"))
                .thenReturn("lyah.artem10@gmail.com");
    }

    @Test
    public void logIn_If_User_Exists(){
        var logInDTO = new LogInDTO("lyah.artem10@mail.ru", "2424285");
        assertDoesNotThrow(() -> authService.logIn(logInDTO));
        verify(userRepository, times(1))
                .findByEmail("lyah.artem10@mail.ru");
    }

    @Test
    public void throw_Exception_When_logIn_If_User_Not_Exists(){
        var logInDTO = new LogInDTO("lyah.artem10@gmail.com", "2424285");
        var exception = assertThrows(
                BadCredentialsException.class, () -> authService.logIn(logInDTO));
        assertEquals("Email and password combination is incorrect", exception.getMessage());
        verify(userRepository, times(1))
                .findByEmail("lyah.artem10@gmail.com");
    }

    @Test
    public void throw_Exception_When_logIn_If_Password_Is_Incorrect(){
        var logInDTO = new LogInDTO("lyah.artem10@mail.ru", "2424284");
        var exception = assertThrows(
                BadCredentialsException.class, () -> authService.logIn(logInDTO));
        assertEquals("Email and password combination is incorrect", exception.getMessage());
        verify(userRepository, times(1))
                .findByEmail("lyah.artem10@mail.ru");
    }

    @Test
    public void signUp_If_User_Not_Exists(){
        var signUpDTO = new SignUpDTO("lyah.artem10@gmail.com", "2424284", "2424284");
        assertDoesNotThrow(() -> authService.signUp(signUpDTO));
        verify(userRepository, times(1))
                .findByEmail("lyah.artem10@gmail.com");
    }

    @Test
    public void throw_Exception_When_signUp_If_User_Exists(){
        var signUpDTO = new SignUpDTO("lyah.artem10@mail.ru", "2424284", "2424284");
        var exception = assertThrows(
                BadCredentialsException.class, () -> authService.signUp(signUpDTO));
        assertEquals("User has already been registered", exception.getMessage());
        verify(userRepository, times(1))
                .findByEmail("lyah.artem10@mail.ru");
    }

    @Test
    public void refresh_If_Token_Combination_Is_Correct(){
        var token = new Token("accessToken", "7b69ab6d-4767-48d8-a20f-25a2340e1405");
        assertDoesNotThrow(() -> authService.refresh(token));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void throws_Exception_When_refresh_If_Refresh_Token_Is_Incorrect(){
        var token = new Token("accessToken", "7b69ab6d-4767-48d8-a20f-25a2340e1805");
        var exception = assertThrows(
                BadCredentialsException.class, () -> authService.refresh(token));
        assertEquals("Tokens combination is invalid", exception.getMessage());
        verify(userRepository, times(0)).save(any(User.class));
    }

    @Test
    public void throws_Exception_When_refresh_If_Access_Token_Is_Incorrect(){
        var token = new Token("invalidAccessToken", "7b69ab6d-4767-48d8-a20f-25a2340e1805");
        var exception = assertThrows(
                BadCredentialsException.class, () -> authService.refresh(token));
        assertEquals("Tokens combination is invalid", exception.getMessage());
        verify(userRepository, times(0)).save(any(User.class));
    }
}
