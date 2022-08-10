package com.devanmejia.appmanager.controller;

import com.devanmejia.appmanager.configuration.TestUserDetailsService;
import com.devanmejia.appmanager.configuration.security.JwtAuthenticationEntryPoint;
import com.devanmejia.appmanager.configuration.security.JwtAuthenticationManager;
import com.devanmejia.appmanager.configuration.security.SecurityConfig;
import com.devanmejia.appmanager.configuration.security.oauth.OAuth2AuthenticationFailureHandler;
import com.devanmejia.appmanager.configuration.security.oauth.OAuth2AuthenticationSuccessHandler;
import com.devanmejia.appmanager.configuration.security.oauth.OAuth2RequestRepository;
import com.devanmejia.appmanager.configuration.security.oauth.cookie.CookieService;
import com.devanmejia.appmanager.configuration.security.providers.JwtProvider;
import com.devanmejia.appmanager.configuration.security.token.JwtService;
import com.devanmejia.appmanager.service.auth.AuthService;
import com.devanmejia.appmanager.service.time.TimeService;
import com.devanmejia.appmanager.service.time.TimeServiceImpl;
import com.devanmejia.appmanager.transfer.auth.LogInDTO;
import com.devanmejia.appmanager.transfer.auth.SignUpDTO;
import com.devanmejia.appmanager.transfer.auth.token.EnterToken;
import com.devanmejia.appmanager.transfer.auth.token.Token;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.filter.OncePerRequestFilter;

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@WebMvcTest(AuthController.class)
public class AuthControllerInTest {
    private static final ZonedDateTime NOW = ZonedDateTime.of(
            2022,
            8,
            10,
            14,
            22,
            54,
            6,
            ZoneId.of("UTC")
    );
    @MockBean
    private AuthService authService;
    private final MockMvc mvc;
    private final ObjectMapper objectMapper;

    @Autowired
    public AuthControllerInTest(MockMvc mvc, ObjectMapper objectMapper) {
        this.mvc = mvc;
        this.objectMapper = objectMapper;
    }

    @TestConfiguration
    static class SecurityTestConfig {
        @Bean("testUserDetailsService")
        public UserDetailsService testUserDetailsService(){
            return new TestUserDetailsService();
        }

        @Bean("testAuthenticationManager")
        public AuthenticationManager testAuthenticationManager(){
            return new JwtAuthenticationManager(
                    List.of(new JwtProvider(testUserDetailsService(), new JwtService(testTimeService()))));
        }

        @Bean("testAuthenticationEntryPoint")
        public AuthenticationEntryPoint testAuthenticationEntryPoint(){
            return new JwtAuthenticationEntryPoint(objectMapper());
        }

        @Bean
        public ObjectMapper objectMapper() {
            return new ObjectMapper();
        }

        @Bean("testAuthenticationSuccessHandler")
        public AuthenticationSuccessHandler testAuthenticationSuccessHandler(){
            return new OAuth2AuthenticationSuccessHandler(spy(CookieService.class), spy(AuthService.class));
        }

        @Bean("testAuthenticationFailureHandler")
        public AuthenticationFailureHandler testAuthenticationFailureHandler(){
            return new OAuth2AuthenticationFailureHandler(spy(CookieService.class));
        }

        @Bean("testOAuth2RequestRepository")
        public OAuth2RequestRepository testOAuth2RequestRepository(){
            return new OAuth2RequestRepository(spy(CookieService.class));
        }

        @Bean("testTimeService")
        public TimeService testTimeService() {
            var clock = Clock.fixed(NOW.toInstant(), NOW.getZone());
            return new TimeServiceImpl(clock);
        }
    }

    @Test
    public void logIn_Test() throws Exception {
        var requestBody = new LogInDTO("lyah.artem10@mail.ru", "qwerty");
        var request = MockMvcRequestBuilders
                .post("/api/v1/auth/log-in")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(requestBody));
        mvc.perform(request)
                .andExpect(status().isOk());
        verify(authService, times(1))
                .logIn(argThat(logInDTO -> logInDTO.email().equals(requestBody.email())
                                && logInDTO.password().equals(requestBody.password())));
    }

    @Test
    public void return_422_When_logIn_If_Request_Body_Is_Incorrect() throws Exception {
        var requestBody = new LogInDTO("", "qwerty");
        var request = MockMvcRequestBuilders
                .post("/api/v1/auth/log-in")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(requestBody));
        mvc.perform(request)
                .andExpect(status().isUnprocessableEntity());
        verify(authService, times(0))
                .logIn(any(LogInDTO.class));
    }

    @Test
    public void signUp_Test() throws Exception {
        var requestBody = new SignUpDTO("lyah.artem10@mail.ru", "qwerty", "qwerty");
        var request = MockMvcRequestBuilders
                .post("/api/v1/auth/sign-up")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(requestBody));
        mvc.perform(request)
                .andExpect(status().isOk());
        verify(authService, times(1))
                .signUp(argThat(signUp -> signUp.email().equals(requestBody.email())
                        && signUp.password().equals(requestBody.password())
                        && signUp.rePassword().equals(requestBody.rePassword())));
    }

    @Test
    public void return_422_When_signUp_If_Request_Body_Is_Incorrect() throws Exception {
        var requestBody = new SignUpDTO("lyah.artem10@mail.ru", "qwerty", "qwerty1");
        var request = MockMvcRequestBuilders
                .post("/api/v1/auth/sign-up")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(requestBody));
        mvc.perform(request)
                .andExpect(status().isUnprocessableEntity());
        verify(authService, times(0))
                .logIn(any(LogInDTO.class));
    }

    @Test
    public void refresh_Test() throws Exception {
        var requestBody = new Token(
                "eyJhbGciOiJIUzI1NiJ.eyJzdWIiOiJseWFoLmFydGVtMTBAbWFpbC5ydSIsImF1dGhvcml0eSI6IkFDVElWRSIsImlhdCI6MTY1MTA2MzQyOCwiZXhwIjoxNjUxMDY0MTQ4fQ.p3gILyvyQ7AsAXH0t97mcqv3hLlgT1q4F9AIX3mfPD4",
                "f49a1380-4e9b-4a81-b5ee-735c59f35fed");
        var request = MockMvcRequestBuilders
                .post("/api/v1/auth/refresh")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(requestBody));
        mvc.perform(request)
                .andExpect(status().isOk());
        verify(authService, times(1))
                .refresh(argThat(token -> token.accessToken().equals(requestBody.accessToken())
                        && token.refreshToken().equals(requestBody.refreshToken())));
    }

    @Test
    public void logInViaEnterToken_Test() throws Exception {
        var requestBody = new EnterToken(
                "eyJhbGciOiJIUzI1NiJ.eyJzdWIiOiJseWFoLmFydGVtMTBAbWFpbC5ydSIsImF1dGhvcml0eSI6IkFDVElWRSIsImlhdCI6MTY1MTA2MzQyOCwiZXhwIjoxNjUxMDY0MTQ4fQ.p3gILyvyQ7AsAXH0t97mcqv3hLlgT1q4F9AIX3mfPD4");
        var request = MockMvcRequestBuilders
                .post("/api/v1/auth/log-in/token")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(requestBody));
        mvc.perform(request)
                .andExpect(status().isOk());
        verify(authService, times(1))
                .logInViaEnterToken(argThat(token -> token.enterToken().equals(requestBody.enterToken())));
    }
}
