package com.devanmejia.appmanager.controller;

import com.devanmejia.appmanager.configuration.TestUserDetailsService;
import com.devanmejia.appmanager.security.JwtAuthenticationEntryPoint;
import com.devanmejia.appmanager.security.JwtAuthenticationManager;
import com.devanmejia.appmanager.security.oauth.OAuth2AuthenticationFailureHandler;
import com.devanmejia.appmanager.security.oauth.OAuth2AuthenticationSuccessHandler;
import com.devanmejia.appmanager.security.oauth.OAuth2RequestRepository;
import com.devanmejia.appmanager.security.oauth.cookie.CookieService;
import com.devanmejia.appmanager.security.providers.JwtProvider;
import com.devanmejia.appmanager.security.token.JwtService;
import com.devanmejia.appmanager.entity.Event;
import com.devanmejia.appmanager.service.auth.AuthService;
import com.devanmejia.appmanager.service.event.EventService;
import com.devanmejia.appmanager.service.time.TimeService;
import com.devanmejia.appmanager.service.time.TimeServiceImpl;
import com.devanmejia.appmanager.transfer.criteria.PageCriteria;
import com.devanmejia.appmanager.transfer.event.EventRequestDTO;
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

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@WebMvcTest(EventController.class)
public class EventControllerInTest {
    private static final DateTimeFormatter DATE_FORMATTER
            = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:s");
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
    private EventService eventService;
    private final MockMvc mvc;
    private final ObjectMapper objectMapper;

    @Autowired
    public EventControllerInTest(MockMvc mvc, ObjectMapper objectMapper) {
        this.mvc = mvc;
        this.objectMapper = objectMapper;
    }

    @BeforeEach
    public void initMocks() {
        var body = new EventRequestDTO("Log in", "User was successfully logged in");
        var offsetTime = NOW.withZoneSameInstant(ZoneOffset.ofTotalSeconds(14400)).toOffsetDateTime();
        when(eventService.addAppEvent(
                eq(1L),
                argThat(requestBody -> requestBody.name().equals(body.name())
                        && requestBody.extraInformation().equals(body.extraInformation())),
                eq(1L),
                eq(offsetTime)))
                .thenReturn(Event.builder()
                        .id(1)
                        .name(body.name())
                        .creationTime(offsetTime)
                        .build());
        when(eventService.addAppEvent(
                eq(1L),
                argThat(requestBody -> requestBody.name().equals(body.name())
                        && requestBody.extraInformation().equals(body.extraInformation())),
                eq(1L),
                eq(NOW.toOffsetDateTime())))
                .thenReturn(Event.builder()
                        .id(1)
                        .name(body.name())
                        .creationTime(NOW.toOffsetDateTime())
                        .build());
        var bodyToUpdate = new EventRequestDTO("Log in failed", "User could not log in");
        when(eventService.updateAppEvent(
                eq(1L),
                eq(1L),
                argThat(requestBody -> requestBody.name().equals(bodyToUpdate.name())
                        && requestBody.extraInformation().equals(bodyToUpdate.extraInformation())),
                eq(1L)))
                .thenReturn(Event.builder()
                        .id(1)
                        .name(body.name())
                        .creationTime(NOW.toOffsetDateTime())
                        .build());
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
    @WithUserDetails(
            value = "lyah.artem10@mail.ru",
            userDetailsServiceBeanName = "testUserDetailsService"
    )
    public void addAppEvent_Test() throws Exception {
        var requestBody = new EventRequestDTO("Log in", "User was successfully logged in");
        var request = MockMvcRequestBuilders
                .post("/api/v1/app/1/event")
                .contentType("application/json")
                .header("Time-Zone-Offset", 14400)
                .content(objectMapper.writeValueAsString(requestBody));
        mvc.perform(request)
                .andExpect(status().isOk());
        verify(eventService, times(1))
                .addAppEvent(
                        eq(1L),
                        argThat(eventDTO -> eventDTO.name().equals(requestBody.name())
                                && eventDTO.extraInformation().equals(requestBody.extraInformation())),
                        eq(1L),
                        argThat(time -> time.format(DATE_FORMATTER).equals("10.08.2022 18:22:54")));
    }

    @Test
    @WithUserDetails(
            value = "lyah.artem10@mail.ru",
            userDetailsServiceBeanName = "testUserDetailsService"
    )
    public void addAppEvent_With_Default_TimeZone_Test() throws Exception {
        var requestBody = new EventRequestDTO("Log in", "User was successfully logged in");
        var request = MockMvcRequestBuilders
                .post("/api/v1/app/1/event")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(requestBody));
        mvc.perform(request)
                .andExpect(status().isOk());
        verify(eventService, times(1))
                .addAppEvent(
                        eq(1L),
                        argThat(eventDTO -> eventDTO.name().equals(requestBody.name())
                                && eventDTO.extraInformation().equals(requestBody.extraInformation())),
                        eq(1L),
                        argThat(time -> time.format(DATE_FORMATTER).equals("10.08.2022 14:22:54")));
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
                .addAppEvent(anyLong(), any(EventRequestDTO.class), anyLong(), any(OffsetDateTime.class));
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
                .addAppEvent(anyLong(), any(EventRequestDTO.class), anyLong(), any(OffsetDateTime.class));
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
                .addAppEvent(anyLong(), any(EventRequestDTO.class), anyLong(), any(OffsetDateTime.class));
    }

    @Test
    @WithUserDetails(
            value = "lyah.artem10@mail.ru",
            userDetailsServiceBeanName = "testUserDetailsService"
    )
    public void getAppEvents_Test() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/api/v1/app/2/events?page=1&pageSize=4")
                .contentType("application/json");
        mvc.perform(request)
                .andExpect(status().isOk());
        verify(eventService, times(1))
                .findAppEvents(eq(2L), eq(1L), eq(new PageCriteria(1, 4)));
    }

    @Test
    @WithUserDetails(
            value = "lyah.artem10@mail.ru",
            userDetailsServiceBeanName = "testUserDetailsService"
    )
    public void getAppEvents_With_Default_Page_Criteria_Test() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/api/v1/app/2/events")
                .contentType("application/json");
        mvc.perform(request)
                .andExpect(status().isOk());
        verify(eventService, times(1))
                .findAppEvents(eq(2L), eq(1L), eq(new PageCriteria(1, 3)));
    }

    @Test
    @WithUserDetails(
            value = "lyah.artem10@gmail.com",
            userDetailsServiceBeanName = "testUserDetailsService"
    )
    public void return_403_When_getAppEvents_If_User_Does_Not_Have_Permission() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/api/v1/app/2/events")
                .contentType("application/json");
        mvc.perform(request)
                .andExpect(status().isForbidden());
    }

    @Test
    public void return_401_When_getAppEvents_If_User_Is_Not_Authenticated() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/api/v1/app/2/events")
                .contentType("application/json");
        mvc.perform(request)
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails(
            value = "lyah.artem10@mail.ru",
            userDetailsServiceBeanName = "testUserDetailsService"
    )
    public void getAppEventsPageAmount_Test() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/api/v1/app/1/events/count")
                .param("pageSize", "3");
        mvc.perform(request)
                .andExpect(status().isOk());
        verify(eventService, times(1))
                .getPageAmount(eq(1L), eq(3), eq(1L));
    }

    @Test
    @WithUserDetails(
            value = "lyah.artem10@mail.ru",
            userDetailsServiceBeanName = "testUserDetailsService"
    )
    public void getAppEventsPageAmount_With_Default_Page_Size_Test() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/api/v1/app/1/events/count");
        mvc.perform(request)
                .andExpect(status().isOk());
        verify(eventService, times(1))
                .getPageAmount(eq(1L), eq(1), eq(1L));
    }

    @Test
    @WithUserDetails(
            value = "lyah.artem10@gmail.com",
            userDetailsServiceBeanName = "testUserDetailsService"
    )
    public void return_403_When_getAppEventsPageAmount_If_User_Does_Not_Have_Permission() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/api/v1/app/1/events/count");
        mvc.perform(request)
                .andExpect(status().isForbidden());
        verify(eventService, times(0))
                .getPageAmount(anyLong(), anyInt(), anyLong());
    }

    @Test
    public void return_401_When_getAppEventsPageAmount_If_User_Is_Not_Authenticated() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/api/v1/app/1/events/count");
        mvc.perform(request)
                .andExpect(status().isUnauthorized());
        verify(eventService, times(0))
                .getPageAmount(anyLong(), anyInt(), anyLong());
    }

    @Test
    @WithUserDetails(
            value = "lyah.artem10@mail.ru",
            userDetailsServiceBeanName = "testUserDetailsService"
    )
    public void deleteAppEvent_Test() throws Exception {
        var request = MockMvcRequestBuilders
                .delete("/api/v1/app/1/event/1")
                .contentType("application/json");
        mvc.perform(request)
                .andExpect(status().isOk());
        verify(eventService, times(1))
                .deleteAppEvent(eq(1L), eq(1L), eq(1L));
    }

    @Test
    @WithUserDetails(
            value = "lyah.artem10@mail.ru",
            userDetailsServiceBeanName = "testUserDetailsService"
    )
    public void updateAppEvent_Test() throws Exception {
        var requestBody = new EventRequestDTO("Log in failed", "User could not log in");
        var request = MockMvcRequestBuilders
                .put("/api/v1/app/1/event/1")
                .content(objectMapper.writeValueAsString(requestBody))
                .contentType("application/json");
        mvc.perform(request)
                .andExpect(status().isOk());
        verify(eventService, times(1))
                .updateAppEvent(
                        eq(1L),
                        eq(1L),
                        argThat(event -> event.name().equals(requestBody.name()) && event.extraInformation().equals(requestBody.extraInformation()) ),
                        eq(1L));
    }
}
