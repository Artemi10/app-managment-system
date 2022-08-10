package com.devanmejia.appmanager.controller.app;

import com.devanmejia.appmanager.configuration.TestUserDetailsService;
import com.devanmejia.appmanager.configuration.security.JwtAuthenticationEntryPoint;
import com.devanmejia.appmanager.configuration.security.JwtAuthenticationManager;
import com.devanmejia.appmanager.configuration.security.oauth.OAuth2AuthenticationFailureHandler;
import com.devanmejia.appmanager.configuration.security.oauth.OAuth2AuthenticationSuccessHandler;
import com.devanmejia.appmanager.configuration.security.oauth.OAuth2RequestRepository;
import com.devanmejia.appmanager.configuration.security.oauth.cookie.CookieService;
import com.devanmejia.appmanager.configuration.security.providers.JwtProvider;
import com.devanmejia.appmanager.configuration.security.token.JwtService;
import com.devanmejia.appmanager.service.app_search.AppSearchService;
import com.devanmejia.appmanager.service.auth.AuthService;
import com.devanmejia.appmanager.service.time.TimeService;
import com.devanmejia.appmanager.service.time.TimeServiceImpl;
import com.devanmejia.appmanager.transfer.criteria.PageCriteria;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@WebMvcTest(AppNameSearchController.class)
public class AppNameSearchControllerInTest {
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
    private AppSearchService appSearchService;
    private final MockMvc mvc;

    @Autowired
    public AppNameSearchControllerInTest(MockMvc mvc) {
        this.mvc = mvc;
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
    public void findUserApps_Test() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/api/v1/apps/name/test?page=1&pageSize=4");
        mvc.perform(request)
                .andExpect(status().isOk());
        verify(appSearchService, times(1))
                .findUserApps(eq(1L), eq("test"), eq(new PageCriteria(1, 4)));
    }

    @Test
    public void return_401_When_findUserApps_If_User_Is_Not_Authenticated() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/api/v1/apps/name/test?page=1&pageSize=4");
        mvc.perform(request)
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails(
            value = "lyah.artem10@gmail.com",
            userDetailsServiceBeanName = "testUserDetailsService"
    )
    public void return_403_When_findUserApps_If_User_Does_Not_Have_Permission() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/api/v1/apps/name/test?page=1&pageSize=4");
        mvc.perform(request)
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails(
            value = "lyah.artem10@mail.ru",
            userDetailsServiceBeanName = "testUserDetailsService"
    )
    public void getPageAmount_Test() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/api/v1/apps/name/test/count?pageSize=4");
        mvc.perform(request)
                .andExpect(status().isOk());
        verify(appSearchService, times(1))
                .getPageAmount(eq(1L), eq(4), eq("test"));
    }

    @Test
    public void return_401_When_getPageAmount_If_User_Is_Not_Authenticated() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/api/v1/apps/name/test/count?pageSize=4");
        mvc.perform(request)
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails(
            value = "lyah.artem10@gmail.com",
            userDetailsServiceBeanName = "testUserDetailsService"
    )
    public void return_403_When_getPageAmount_If_User_Does_Not_Have_Permission() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/api/v1/apps/name/test/count?pageSize=4");
        mvc.perform(request)
                .andExpect(status().isForbidden());
    }
}
