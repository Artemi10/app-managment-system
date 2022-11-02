package com.devanmejia.appmanager.service.auth;

import com.devanmejia.appmanager.security.token.AccessTokenService;
import com.devanmejia.appmanager.entity.user.Authority;
import com.devanmejia.appmanager.entity.user.User;
import com.devanmejia.appmanager.repository.UserRepository;
import com.devanmejia.appmanager.service.token.SecureTokenGenerator;
import com.devanmejia.appmanager.transfer.auth.LogInDTO;
import com.devanmejia.appmanager.transfer.auth.SignUpDTO;
import com.devanmejia.appmanager.transfer.auth.token.EnterToken;
import com.devanmejia.appmanager.transfer.auth.token.Token;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
        this.authService = new AuthServiceImpl(
                accessTokenService,
                new BCryptPasswordEncoder(),
                userRepository,
                new SecureTokenGenerator()
        );
    }

    @BeforeEach
    public void initMocks() {
        var user = User.builder()
                .id(1)
                .email("lyah.artem10@mail.ru")
                .password("$2a$10$3kVWnJcACqfBKzhiA//1MeJ/ex1PylaWC7esjmVwSzePHGW6AQmhu")
                .refreshToken("$2a$10$nELieoQz2WeESnhcSm1RAumtrjowOwX6jZArwnDu9vpHv00CKhLUW")
                .authority(Authority.ACTIVE)
                .build();
        var newUser = User.builder()
                .id(2)
                .email("lyah.artem10@gmail.com")
                .password("$2a$10$3kVWnJcACqfBKzhiA//1MeJ/ex1PylaWC7esjmVwSzePHGW6AQmhu")
                .refreshToken("$2a$10$nELieoQz2WeESnhcSm1RAumtrjowOwX6jZArwnDu9vpHv00CKhLUW")
                .authority(Authority.ACTIVE)
                .build();
        var oAuthUser = User.builder()
                .id(3)
                .email("lyah.artem03@gmail.com")
                .password("$2a$10$3kVWnJcACqfBKzhiA//1MeJ/ex1PylaWC7esjmVwSzePHGW6AQmhu")
                .refreshToken("$2a$10$nELieoQz2WeESnhcSm1RAumtrjowOwX6jZArwnDu9vpHv00CKhLUW")
                .oauthEnterToken("7b69ab6d-4767-48d8-a20f-25a2340e424")
                .authority(Authority.ACTIVE)
                .build();
        when(userRepository.findByEmail("lyah.artem10@mail.ru"))
                .thenReturn(Optional.of(user));
        when(userRepository.save(argThat(userToCreate -> userToCreate.getEmail().equals("lyah.artem10@gmail.com"))))
                .thenReturn(newUser);
        when(userRepository.findByEmail("lyah.artem10@gmail.com"))
                .thenReturn(Optional.empty());
        when(userRepository.findUserByOauthEnterToken("7b69ab6d-4767-48d8-a20f-25a2340e424"))
                .thenReturn(Optional.of(oAuthUser));
        when(userRepository.findUserByOauthEnterToken(argThat(token -> !token.equals("7b69ab6d-4767-48d8-a20f-25a2340e424"))))
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
        verify(userRepository, times(1))
                .save(argThat(newUser -> newUser.getEmail().equals("lyah.artem10@gmail.com")));
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

    @Test
    public void logInViaEnterToken_Test() {
        var enterToken = new EnterToken("7b69ab6d-4767-48d8-a20f-25a2340e424");
        assertDoesNotThrow(() -> authService.logInViaEnterToken(enterToken));
        verify(userRepository, times(1))
                .save(argThat(user -> user.getId() == 3));
        verify(accessTokenService, times(1))
                .createAccessToken("lyah.artem03@gmail.com", Authority.ACTIVE);
    }

    @Test
    public void throw_Exception_When_logInViaEnterToken_If_User_Not_Found_Test() {
        var enterToken = new EnterToken("7b69ab6d-4767-48d8-a20f-25a2340e422");
        var exception = assertThrows(BadCredentialsException.class, () -> authService.logInViaEnterToken(enterToken));
        assertEquals("Enter token is incorrect", exception.getMessage());
        verify(userRepository, times(0))
                .save(any());
        verify(accessTokenService, times(0))
                .createAccessToken(any(), any());
    }

    @Test
    public void logInWithOAuth_When_User_Has_Already_Been_Registered_Test() {
        authService.logInWithOAuth("lyah.artem10@gmail.com");
        verify(userRepository, times(1))
                .findByEmail("lyah.artem10@gmail.com");
        verify(userRepository, times(2))
                .save(argThat(user -> user.getEmail().equals("lyah.artem10@gmail.com")));
    }

    @Test
    public void logInWithOAuth_When_User_Is_New_Test() {
        authService.logInWithOAuth("lyah.artem10@mail.ru");
        verify(userRepository, times(1))
                .findByEmail("lyah.artem10@mail.ru");
        verify(userRepository, times(1))
                .save(argThat(user -> user.getEmail().equals("lyah.artem10@mail.ru")));
    }
}
