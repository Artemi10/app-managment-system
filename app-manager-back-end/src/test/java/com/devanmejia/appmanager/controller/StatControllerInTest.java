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
import com.devanmejia.appmanager.exception.EntityException;
import com.devanmejia.appmanager.service.auth.AuthService;
import com.devanmejia.appmanager.service.stat.StatService;
import com.devanmejia.appmanager.service.time.TimeService;
import com.devanmejia.appmanager.service.time.TimeServiceImpl;
import com.devanmejia.appmanager.transfer.stat.StatRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
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

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@WebMvcTest(StatController.class)
public class StatControllerInTest {
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
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:00 Z");
    @MockBean(name = "dayStatService")
    private StatService dayStatService;
    @MockBean(name = "hourStatService")
    private StatService hourStatService;
    @MockBean(name = "monthStatService")
    private StatService monthStatService;
    private final MockMvc mvc;

    @Autowired
    public StatControllerInTest(MockMvc mvc) {
        this.mvc = mvc;
    }

    @BeforeEach
    public void initMocks(){
        doThrow(EntityException.class)
                .when(monthStatService)
                .createStats(eq(7L), any(StatRequestDTO.class), eq(1L));
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
            var mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            return mapper;
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
    public void return_400_When_createDaysStat_If_Request_param_Is_Invalid() throws Exception {
        var request = MockMvcRequestBuilders
                .post("/api/v1/app/1/stat")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"from\" : \"null\", \"to\" : \"null\", \"timeZone\" : \"+0300\", \"type\" : \"DAY\"}");
        mvc.perform(request)
                .andExpect(status().isBadRequest());
        verify(dayStatService, times(0))
                .createStats(anyLong(),any(), anyLong());
        verify(monthStatService, times(0))
                .createStats(anyLong(),any(), anyLong());
        verify(hourStatService, times(0))
                .createStats(anyLong(),any(), anyLong());
    }

    @Test
    @WithUserDetails(
            value = "lyah.artem10@mail.ru",
            userDetailsServiceBeanName = "testUserDetailsService"
    )
    public void createStatByDatePeriod_If_User_Is_Authenticated() throws Exception {
        var request = MockMvcRequestBuilders
                .post("/api/v1/app/1/stat")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"from\" : \"2022-03-22 00:00\", \"to\" : \"2022-03-24 00:00\", \"timeZone\" : \"+0300\", \"type\" : \"DAY\"}");
        mvc.perform(request)
                .andExpect(status().isOk());
        verify(dayStatService, times(1))
                .createStats(
                        eq(1L),
                        argThat(stats ->
                                FORMATTER.format(stats.getFrom()).equals("2022-03-22 00:00 +0300")
                                && FORMATTER.format(stats.getTo()).equals("2022-03-24 00:00 +0300")),
                        eq(1L)
                );
        verify(monthStatService, times(0))
                .createStats(anyLong(),any(), anyLong());
        verify(hourStatService, times(0))
                .createStats(anyLong(),any(), anyLong());
    }

    @Test
    public void return_401_When_createStat_If_User_Is_Not_Authenticated() throws Exception {
        var request = MockMvcRequestBuilders
                .post("/api/v1/app/1/stat")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"from\" : \"null\", \"to\" : \"null\", \"timeZone\" : \"+3000\", \"type\" : \"MONTH\"}");
        mvc.perform(request)
                .andExpect(status().isUnauthorized());
        verify(dayStatService, times(0))
                .createStats(anyLong(),any(), anyLong());
        verify(monthStatService, times(0))
                .createStats(anyLong(),any(), anyLong());
        verify(hourStatService, times(0))
                .createStats(anyLong(),any(), anyLong());
    }

    @Test
    @WithUserDetails(
            value = "lyah.artem10@gmail.com",
            userDetailsServiceBeanName = "testUserDetailsService"
    )
    public void return_403_When_createStat_If_User_Does_Not_Have_Permission() throws Exception {
        var request = MockMvcRequestBuilders
                .post("/api/v1/app/1/stat")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"from\" : \"null\", \"to\" : \"null\", \"timeZone\" : \"+3000\", \"type\" : \"MONTH\"}");
        mvc.perform(request)
                .andExpect(status().isForbidden());
        verify(dayStatService, times(0))
                .createStats(anyLong(),any(), anyLong());
        verify(monthStatService, times(0))
                .createStats(anyLong(),any(), anyLong());
        verify(hourStatService, times(0))
                .createStats(anyLong(),any(), anyLong());
    }

    @Test
    @WithUserDetails(
            value = "lyah.artem10@mail.ru",
            userDetailsServiceBeanName = "testUserDetailsService"
    )
    public void return_400_When_createStat_If_RequestBody_Is_Invalid() throws Exception {
        var request = MockMvcRequestBuilders
                .post("/api/v1/app/1/stat")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"from\" : \"12 July 2022\", \"to\" : \"16 October 2022\", \"timeZone\" : \"+3000\",  \"type\" : \"MONTH\"}");
        mvc.perform(request)
                .andExpect(status().isBadRequest());
        verify(dayStatService, times(0))
                .createStats(anyLong(),any(), anyLong());
        verify(monthStatService, times(0))
                .createStats(anyLong(),any(), anyLong());
        verify(hourStatService, times(0))
                .createStats(anyLong(),any(), anyLong());
    }

    @Test
    @WithUserDetails(
            value = "lyah.artem10@mail.ru",
            userDetailsServiceBeanName = "testUserDetailsService"
    )
    public void return_404_When_createEmptyStat_If_Application_Not_Found() throws Exception {
        var request = MockMvcRequestBuilders
                .post("/api/v1/app/7/stat")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"from\" : \"2022-03-22 00:00\", \"to\" : \"2022-03-24 00:00\", \"timeZone\" : \"+0300\", \"type\" : \"MONTH\"}");
        mvc.perform(request)
                .andExpect(status().isNotFound());
        verify(dayStatService, times(0))
                .createStats(anyLong(), any(), anyLong());
        verify(monthStatService, times(1))
                .createStats(eq(7L), any(), eq(1L));
        verify(hourStatService, times(0))
                .createStats(anyLong(), any(), anyLong());
    }

}
