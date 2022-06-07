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
import com.devanmejia.appmanager.exception.EntityException;
import com.devanmejia.appmanager.service.auth.AuthService;
import com.devanmejia.appmanager.service.stat.DayStatService;
import com.devanmejia.appmanager.service.stat.HourStatService;
import com.devanmejia.appmanager.service.stat.MonthStatService;
import com.devanmejia.appmanager.transfer.stat.StatRequestDTO;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@WebMvcTest(StatController.class)
public class StatControllerInTest {
    @MockBean(name = "days")
    private DayStatService dayStatService;
    @MockBean(name = "hours")
    private HourStatService hourStatService;
    @MockBean(name = "months")
    private MonthStatService monthStatService;
    private final MockMvc mvc;

    @Autowired
    public StatControllerInTest(MockMvc mvc) {
        this.mvc = mvc;
    }

    @BeforeEach
    public void initMocks(){
        doThrow(EntityException.class)
                .when(monthStatService)
                .createStats(eq(2L), anyLong());
        doThrow(EntityException.class)
                .when(monthStatService)
                .createStats(eq(2L), any(StatRequestDTO.class));
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

    @Test
    @WithUserDetails(
            value = "lyah.artem10@mail.ru",
            userDetailsServiceBeanName = "testUserDetailsService"
    )
    public void createDaysStat_If_User_Is_Authenticated() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/api/v1/app/1/stat?type=days&from=&to=");
        mvc.perform(request)
                .andExpect(status().isOk());
        verify(dayStatService, times(1))
                .createStats(eq(1L), anyLong());
    }

    @Test
    @WithUserDetails(
            value = "lyah.artem10@mail.ru",
            userDetailsServiceBeanName = "testUserDetailsService"
    )
    public void createHoursStat_If_User_Is_Authenticated() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/api/v1/app/1/stat?type=hours&from=&to=");
        mvc.perform(request)
                .andExpect(status().isOk());
        verify(hourStatService, times(1))
                .createStats(eq(1L), anyLong());
    }

    @Test
    @WithUserDetails(
            value = "lyah.artem10@mail.ru",
            userDetailsServiceBeanName = "testUserDetailsService"
    )
    public void createMonthStat_If_User_Is_Authenticated() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/api/v1/app/1/stat?type=months&from=&to=");
        mvc.perform(request)
                .andExpect(status().isOk());
        verify(monthStatService, times(1))
                .createStats(eq(1L), anyLong());
    }

    @Test
    @WithUserDetails(
            value = "lyah.artem10@mail.ru",
            userDetailsServiceBeanName = "testUserDetailsService"
    )
    public void createStatByDatePeriod_If_User_Is_Authenticated() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/api/v1/app/1/stat?type=days&from=22.03.2022&to=24.03.2022");
        mvc.perform(request)
                .andExpect(status().isOk());
        verify(dayStatService, times(1))
                .createStats(eq(1L), any(StatRequestDTO.class));
    }

    @Test
    public void return_401_When_createStat_If_User_Is_Not_Authenticated() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/api/v1/app/1/stat?type=months&from=&to=");
        mvc.perform(request)
                .andExpect(status().isUnauthorized());
        verify(monthStatService, times(0))
                .createStats(anyLong(), anyLong());
    }

    @Test
    @WithUserDetails(
            value = "lyah.artem10@gmail.com",
            userDetailsServiceBeanName = "testUserDetailsService"
    )
    public void return_403_When_createStat_If_User_Does_Not_Have_Permission() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/api/v1/app/1/stat?type=months&from=&to=");
        mvc.perform(request)
                .andExpect(status().isForbidden());
        verify(monthStatService, times(0))
                .createStats(anyLong(), anyLong());
    }

    @Test
    @WithUserDetails(
            value = "lyah.artem10@mail.ru",
            userDetailsServiceBeanName = "testUserDetailsService"
    )
    public void createEmptyStat_If_Application_Not_Found() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/api/v1/app/2/stat?type=months&from=&to=");
        mvc.perform(request)
                .andExpect(status().isOk());
        verify(monthStatService, times(1))
                .createStats(eq(2L), anyLong());
    }

    @Test
    @WithUserDetails(
            value = "lyah.artem10@mail.ru",
            userDetailsServiceBeanName = "testUserDetailsService"
    )
    public void createEmptyStatByDatePeriod_If_Application_Not_Found() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/api/v1/app/2/stat?type=months&from=22.03.2022&to=24.03.2022");
        mvc.perform(request)
                .andExpect(status().isOk());
        verify(monthStatService, times(1))
                .createStats(eq(2L), any(StatRequestDTO.class));
    }
}
