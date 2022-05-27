package com.devanmejia.appmanager.controller;

import com.devanmejia.appmanager.configuration.TestUserDetailsService;
import com.devanmejia.appmanager.configuration.security.JwtAuthenticationEntryPoint;
import com.devanmejia.appmanager.configuration.security.JwtAuthenticationManager;
import com.devanmejia.appmanager.configuration.security.providers.JwtProvider;
import com.devanmejia.appmanager.configuration.security.token.JwtService;
import com.devanmejia.appmanager.service.event.EventService;
import com.devanmejia.appmanager.transfer.event.EventRequestDTO;
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
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@WebMvcTest(EventController.class)
public class EventControllerInTest {
    @MockBean
    private EventService eventService;
    private final MockMvc mvc;
    private final ObjectMapper objectMapper;

    @Autowired
    public EventControllerInTest(MockMvc mvc, ObjectMapper objectMapper) {
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
    }

    @Test
    @WithUserDetails(
            value = "lyah.artem10@mail.ru",
            userDetailsServiceBeanName = "testUserDetailsService"
    )
    public void addAppEvent_Test() throws Exception {
        var requestBody = new EventRequestDTO("Log in", "User was successfully logged in");
        var request = MockMvcRequestBuilders
                .post("/api/v1/app/1/event")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(requestBody));
        mvc.perform(request)
                .andExpect(status().isOk());
        verify(eventService, times(1))
                .addEvent(
                        eq(1L),
                        argThat(eventDTO -> eventDTO.name().equals(requestBody.name())
                                && eventDTO.extraInformation().equals(requestBody.extraInformation())),
                        eq(1L));
    }

    @Test
    @WithUserDetails(
            value = "lyah.artem10@gmail.com",
            userDetailsServiceBeanName = "testUserDetailsService"
    )
    public void return_403_When_addAppEvent_If_User_Does_Not_Have_Permission() throws Exception {
        var requestBody = new EventRequestDTO("Log in", "User was successfully logged in");
        var request = MockMvcRequestBuilders
                .post("/api/v1/app/1/event")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(requestBody));
        mvc.perform(request)
                .andExpect(status().isForbidden());
        verify(eventService, times(0))
                .addEvent(anyLong(), any(EventRequestDTO.class), anyLong());
    }

    @Test
    public void return_401_When_addAppEvent_If_User_Is_Not_Authenticated() throws Exception {
        var requestBody = new EventRequestDTO("Log in", "User was successfully logged in");
        var request = MockMvcRequestBuilders
                .post("/api/v1/app/1/event")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(requestBody));
        mvc.perform(request)
                .andExpect(status().isUnauthorized());
        verify(eventService, times(0))
                .addEvent(anyLong(), any(EventRequestDTO.class), anyLong());
    }

    @Test
    @WithUserDetails(
            value = "lyah.artem10@mail.ru",
            userDetailsServiceBeanName = "testUserDetailsService"
    )
    public void return_422_When_addAppEvent_If_Request_Body_Is_Incorrect() throws Exception {
        var requestBody = new EventRequestDTO("", "User was successfully logged in");
        var request = MockMvcRequestBuilders
                .post("/api/v1/app/1/event")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(requestBody));
        mvc.perform(request)
                .andExpect(status().isUnprocessableEntity());
        verify(eventService, times(0))
                .addEvent(anyLong(), any(EventRequestDTO.class), anyLong());
    }
}
