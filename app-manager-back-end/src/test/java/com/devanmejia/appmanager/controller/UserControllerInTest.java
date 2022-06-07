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
import com.devanmejia.appmanager.configuration.security.token.AccessTokenService;
import com.devanmejia.appmanager.configuration.security.token.JwtService;
import com.devanmejia.appmanager.entity.user.Authority;
import com.devanmejia.appmanager.exception.EmailException;
import com.devanmejia.appmanager.exception.EntityException;
import com.devanmejia.appmanager.service.auth.AuthService;
import com.devanmejia.appmanager.service.email.MessageService;
import com.devanmejia.appmanager.service.user.UserService;
import com.devanmejia.appmanager.transfer.auth.UpdateDTO;
import com.devanmejia.appmanager.transfer.auth.token.ResetToken;
import com.devanmejia.appmanager.transfer.email.EmailRequestDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
public class UserControllerInTest {
    @MockBean
    private AccessTokenService accessTokenService;
    @MockBean
    private MessageService messageService;
    @MockBean
    private UserService userService;
    private final MockMvc mvc;
    private final ObjectMapper objectMapper;

    @Autowired
    public UserControllerInTest(MockMvc mvc, ObjectMapper objectMapper) {
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
                    List.of(new JwtProvider(testUserDetailsService(), new JwtService())));
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
    }

    @BeforeEach
    public void initMock(){
        doThrow(new EntityException("User not found"))
                .when(userService)
                .updateUser(eq("lyah.artem10@gmail.ru"), any(UpdateDTO.class));
        doThrow(EmailException.class)
                .when(messageService).sendMessage("lyah.artem10@gmail.ru", null);
        when(accessTokenService.createAccessToken("lyah.artem10@gmail.ru", Authority.UPDATE_NOT_CONFIRMED))
                .thenReturn("jwt token");
        when(accessTokenService.createAccessToken("lyah.artem10@gmail.ru", Authority.UPDATE_CONFIRMED))
                .thenReturn("jwt token");
    }

    @Test
    @WithUserDetails(
            value = "lyah.artem10@gmail.com",
            userDetailsServiceBeanName = "testUserDetailsService"
    )
    public void updateUser_When_User_Has_Update_Confirmed_Authority_Test() throws Exception {
        var requestBody = new UpdateDTO("qwerty", "qwerty");
        var request = MockMvcRequestBuilders
                .patch("/api/v1/user")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(requestBody));
        mvc.perform(request)
                .andExpect(status().isOk());
    }

    @Test
    @WithUserDetails(
            value = "lyah.artem10@gmail.com",
            userDetailsServiceBeanName = "testUserDetailsService"
    )
    public void return422_When_UpdateUser_If_Request_Body_Is_Invalid() throws Exception {
        var requestBody = new UpdateDTO("qwerty", "qwerty5");
        var request = MockMvcRequestBuilders
                .patch("/api/v1/user")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(requestBody));
        mvc.perform(request)
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @WithUserDetails(
            value = "lyah.artem10@mail.ru",
            userDetailsServiceBeanName = "testUserDetailsService"
    )
    public void return403_When_UpdateUser_If_User_Has_No_Permission() throws Exception {
        var requestBody = new UpdateDTO("qwerty", "qwerty");
        var request = MockMvcRequestBuilders
                .patch("/api/v1/user")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(requestBody));
        mvc.perform(request)
                .andExpect(status().isForbidden());
    }


    @Test
    public void reset_User_Test() throws Exception {
        var requestBody = new EmailRequestDTO("lyah.artem10@mail.ru");
        var request = MockMvcRequestBuilders
                .post("/api/v1/user/reset")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(requestBody));
        mvc.perform(request)
                .andExpect(status().isOk());
        verify(messageService, times(1))
                .sendMessage("lyah.artem10@mail.ru", null);
        verify(userService, times(0))
                .activateUser("lyah.artem10@mail.ru");
    }

    @Test
    public void return_404_When_Reset_User_If_Can_Not_sendEmail_Test() throws Exception {
        var requestBody = new EmailRequestDTO("lyah.artem10@gmail.ru");
        var request = MockMvcRequestBuilders
                .post("/api/v1/user/reset")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(requestBody));
        mvc.perform(request)
                .andExpect(status().isNotFound());
        verify(messageService, times(1))
                .sendMessage("lyah.artem10@gmail.ru", null);
        verify(userService, times(1))
                .activateUser("lyah.artem10@gmail.ru");
    }

    @Test
    public void return_422_When_Reset_User_If_Email_Is_Invalid_Test() throws Exception {
        var requestBody = new EmailRequestDTO("");
        var request = MockMvcRequestBuilders
                .post("/api/v1/user/reset")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(requestBody));
        mvc.perform(request)
                .andExpect(status().isUnprocessableEntity());
        verify(messageService, times(0)).sendMessage(any(), any());
        verify(userService, times(0)).activateUser(any());
    }

    @Test
    @WithUserDetails(
            value = "lyah.artem11@gmail.com",
            userDetailsServiceBeanName = "testUserDetailsService"
    )
    public void confirmResetUser_If_User_Has_Update_Not_Confirmed_Authority() throws Exception {
        var requestBody = new ResetToken("asdfrRT");
        var request = MockMvcRequestBuilders
                .post("/api/v1/user/reset/confirm")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(requestBody));
        mvc.perform(request)
                .andExpect(status().isOk());
    }

    @Test
    @WithUserDetails(
            value = "lyah.artem10@gmail.com",
            userDetailsServiceBeanName = "testUserDetailsService"
    )
    public void return403_When_confirmResetUser_If_User_Has_Update_Confirmed_Authority() throws Exception {
        var requestBody = new ResetToken("asdfrRT");
        var request = MockMvcRequestBuilders
                .post("/api/v1/user/reset/confirm")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(requestBody));
        mvc.perform(request)
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails(
            value = "lyah.artem10@mail.ru",
            userDetailsServiceBeanName = "testUserDetailsService"
    )
    public void return403_When_confirmResetUser_If_User_Has_Actice_Authority() throws Exception {
        var requestBody = new ResetToken("asdfrRT");
        var request = MockMvcRequestBuilders
                .post("/api/v1/user/reset/confirm")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(requestBody));
        mvc.perform(request)
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails(
            value = "lyah.artem11@gmail.com",
            userDetailsServiceBeanName = "testUserDetailsService"
    )
    public void reset_User_Again_If_User_Has_Update_Not_Confirmed_Authority_Test() throws Exception {
        var request = MockMvcRequestBuilders
                .post("/api/v1/user/reset/again")
                .contentType("application/json");
        mvc.perform(request)
                .andExpect(status().isOk());
        verify(messageService, times(1))
                .sendMessage("lyah.artem11@gmail.com", null);
    }

    @Test
    public void return_401_When_reset_User_Again_If_User_UnAuthorized_Test() throws Exception {
        var request = MockMvcRequestBuilders
                .post("/api/v1/user/reset/again")
                .contentType("application/json");
        mvc.perform(request)
                .andExpect(status().isUnauthorized());
        verify(messageService, times(0))
                .sendMessage(any(), any());
    }

    @Test
    @WithUserDetails(
            value = "lyah.artem10@gmail.com",
            userDetailsServiceBeanName = "testUserDetailsService"
    )
    public void return_403_When_reset_User_Again_If_User_Has_Update_Confirmed_Authority_Test() throws Exception {
        var request = MockMvcRequestBuilders
                .post("/api/v1/user/reset/again")
                .contentType("application/json");
        mvc.perform(request)
                .andExpect(status().isForbidden());
        verify(messageService, times(0))
                .sendMessage(any(), any());
    }

    @Test
    @WithUserDetails(
            value = "lyah.artem10@mail.ru",
            userDetailsServiceBeanName = "testUserDetailsService"
    )
    public void return_403_When_reset_User_Again_If_User_Has_Active_Authority_Test() throws Exception {
        var request = MockMvcRequestBuilders
                .post("/api/v1/user/reset/again")
                .contentType("application/json");
        mvc.perform(request)
                .andExpect(status().isForbidden());
        verify(messageService, times(0))
                .sendMessage(any(), any());
    }
}
