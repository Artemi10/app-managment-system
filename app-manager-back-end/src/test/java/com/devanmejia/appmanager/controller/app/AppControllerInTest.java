package com.devanmejia.appmanager.controller.app;

import com.devanmejia.appmanager.configuration.TestUserDetailsService;
import com.devanmejia.appmanager.security.JwtAuthenticationEntryPoint;
import com.devanmejia.appmanager.security.JwtAuthenticationManager;
import com.devanmejia.appmanager.security.oauth.OAuth2AuthenticationFailureHandler;
import com.devanmejia.appmanager.security.oauth.OAuth2AuthenticationSuccessHandler;
import com.devanmejia.appmanager.security.oauth.OAuth2RequestRepository;
import com.devanmejia.appmanager.security.oauth.cookie.CookieService;
import com.devanmejia.appmanager.security.providers.JwtProvider;
import com.devanmejia.appmanager.security.token.JwtService;
import com.devanmejia.appmanager.entity.App;
import com.devanmejia.appmanager.exception.EntityException;
import com.devanmejia.appmanager.service.app.AppService;
import com.devanmejia.appmanager.service.auth.AuthService;
import com.devanmejia.appmanager.service.time.TimeService;
import com.devanmejia.appmanager.service.time.TimeServiceImpl;
import com.devanmejia.appmanager.transfer.app.AppRequestDTO;
import com.devanmejia.appmanager.transfer.criteria.PageCriteria;
import com.devanmejia.appmanager.transfer.criteria.SortCriteria;
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

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@WebMvcTest(AppController.class)
public class AppControllerInTest {
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
    private AppService appService;
    private final MockMvc mvc;
    private final ObjectMapper objectMapper;

    @Autowired
    public AppControllerInTest(MockMvc mvc, ObjectMapper objectMapper) {
        this.mvc = mvc;
        this.objectMapper = objectMapper;
    }

    @BeforeEach
    public void initMock(){
        doThrow(new EntityException("App not found"))
                .when(appService)
                .findUserApp(4, 2);
        var body = new AppRequestDTO("Simple CRUD application");
        var offsetTime = NOW.withZoneSameInstant(ZoneOffset.ofTotalSeconds(14400)).toOffsetDateTime();
        when(appService.addUserApp(eq(1L), argThat(requestBody -> requestBody.name().equals(body.name())), eq(offsetTime)))
                .thenReturn(App.builder()
                        .id(1)
                        .name(body.name())
                        .creationTime(offsetTime)
                        .build());
        when(appService.addUserApp(eq(1L), argThat(requestBody -> requestBody.name().equals(body.name())), eq(NOW.toOffsetDateTime())))
                .thenReturn(App.builder()
                        .id(1)
                        .name(body.name())
                        .creationTime(NOW.toOffsetDateTime())
                        .build());
        when(appService.updateUserApp(eq(1L), argThat(requestBody -> requestBody.name().equals(body.name())), eq(1L)))
                .thenReturn(App.builder()
                        .id(1)
                        .name(body.name())
                        .creationTime(NOW.toOffsetDateTime())
                        .build());
        when(appService.findUserApp(1, 1))
                .thenReturn(App.builder()
                        .id(1)
                        .name("Simple CRUD App")
                        .build());
        when(appService.getAppsAmount(eq(1L)))
                .thenReturn(6);
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
    public void findUserApps_If_User_Is_Authenticated() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/api/v1/apps");
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(header().string("X-Total-Count", "6"));
        verify(appService, times(1))
                .getAppsAmount(eq(1L));
        verify(appService, times(1))
                .findUserApps(eq(1L), eq(new PageCriteria(1, 3)), eq(new SortCriteria("id", SortCriteria.OrderType.DESC)));
    }

    @Test
    @WithUserDetails(
            value = "lyah.artem10@mail.ru",
            userDetailsServiceBeanName = "testUserDetailsService"
    )
    public void findUserApps_With_Page_And_Sort_Params_If_User_Is_Authenticated() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/api/v1/apps?page=2&pageSize=2&sortValue=name&orderType=ASC");
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(header().string("X-Total-Count", "6"));
        verify(appService, times(1))
                .getAppsAmount(eq(1L));
        verify(appService, times(1))
                .findUserApps(eq(1L), eq(new PageCriteria(2, 2)), eq(new SortCriteria("name", SortCriteria.OrderType.ASC)));
    }

    @Test
    public void return_401_When_findUserApps_If_User_Is_Not_Authenticated() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/api/v1/apps");
        mvc.perform(request)
                .andExpect(status().isUnauthorized())
                .andExpect(header().doesNotExist("X-Total-Count"));
        verify(appService, times(0))
                .findUserApps(anyLong(), any(), any());
    }

    @Test
    @WithUserDetails(
            value = "lyah.artem10@gmail.com",
            userDetailsServiceBeanName = "testUserDetailsService"
    )
    public void return_403_When_findUserApps_If_User_Does_Not_Have_Permission() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/api/v1/apps");
        mvc.perform(request)
                .andExpect(status().isForbidden())
                .andExpect(header().doesNotExist("X-Total-Count"));
        verify(appService, times(0))
                .findUserApps(anyLong(), any(), any());
    }

    @Test
    @WithUserDetails(
            value = "lyah.artem10@gmail.com",
            userDetailsServiceBeanName = "testUserDetailsService"
    )
    public void return_403_When_getPageAmount_If_User_Does_Not_Have_Permission() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/api/v1/apps");
        mvc.perform(request)
                .andExpect(status().isForbidden())
                .andExpect(header().doesNotExist("X-Total-Count"));
        verify(appService, times(0))
                .findUserApps(anyLong(), any(), any());
    }

    @Test
    @WithUserDetails(
            value = "lyah.artem10@mail.ru",
            userDetailsServiceBeanName = "testUserDetailsService"
    )
    public void createUserApp_If_User_Is_Authenticated() throws Exception {
        var requestBody = new AppRequestDTO("Simple CRUD application");
        var request = MockMvcRequestBuilders
                .post("/api/v1/apps")
                .contentType("application/json")
                .header("Time-Zone-Offset", 14400)
                .content(objectMapper.writeValueAsString(requestBody));
        mvc.perform(request)
                .andExpect(status().isOk());
        verify(appService, times(1))
                .addUserApp(
                        eq(1L),
                        argThat(appDTO -> requestBody.name().equals(appDTO.name())),
                        argThat(time -> time.format(DATE_FORMATTER).equals("10.08.2022 18:22:54")));
    }

    @Test
    @WithUserDetails(
            value = "lyah.artem10@mail.ru",
            userDetailsServiceBeanName = "testUserDetailsService"
    )
    public void createUserApp_With_Default_TimeZone_If_User_Is_Authenticated() throws Exception {
        var requestBody = new AppRequestDTO("Simple CRUD application");
        var request = MockMvcRequestBuilders
                .post("/api/v1/apps")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(requestBody));
        mvc.perform(request)
                .andExpect(status().isOk());
        verify(appService, times(1))
                .addUserApp(
                        eq(1L),
                        argThat(appDTO -> requestBody.name().equals(appDTO.name())),
                        argThat(time -> time.format(DATE_FORMATTER).equals("10.08.2022 14:22:54")));
    }

    @Test
    @WithUserDetails(
            value = "lyah.artem10@mail.ru",
            userDetailsServiceBeanName = "testUserDetailsService"
    )
    public void return_422_When_createUserApp_If_Request_Body_Is_Invalid() throws Exception {
        var requestBody = new AppRequestDTO(" ");
        var request = MockMvcRequestBuilders
                .post("/api/v1/apps")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(requestBody));
        mvc.perform(request)
                .andExpect(status().isUnprocessableEntity());
        verify(appService, times(0))
                .addUserApp(anyLong(), any(AppRequestDTO.class), any(OffsetDateTime.class));
    }

    @Test
    public void  return_401_When_createUserApp_If_User_Is_Not_Authenticated() throws Exception {
        var requestBody = new AppRequestDTO("Simple CRUD application");
        var request = MockMvcRequestBuilders
                .post("/api/v1/apps")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(requestBody));
        mvc.perform(request)
                .andExpect(status().isUnauthorized());
        verify(appService, times(0))
                .addUserApp(anyLong(), any(AppRequestDTO.class), any(OffsetDateTime.class));
    }

    @Test
    @WithUserDetails(
            value = "lyah.artem10@gmail.com",
            userDetailsServiceBeanName = "testUserDetailsService"
    )
    public void return_403_When_createUserApp_If_User_Does_Not_Have_Permission() throws Exception {
        var requestBody = new AppRequestDTO("Simple CRUD application");
        var request = MockMvcRequestBuilders
                .post("/api/v1/apps")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(requestBody));
        mvc.perform(request)
                .andExpect(status().isForbidden());
        verify(appService, times(0))
                .addUserApp(anyLong(), any(AppRequestDTO.class), any(OffsetDateTime.class));
    }

    @Test
    @WithUserDetails(
            value = "lyah.artem10@mail.ru",
            userDetailsServiceBeanName = "testUserDetailsService"
    )
    public void updateUserApp_If_User_Is_Authenticated() throws Exception {
        var requestBody = new AppRequestDTO("Simple CRUD application");
        var request = MockMvcRequestBuilders
                .put("/api/v1/apps/1")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(requestBody));
        mvc.perform(request)
                .andExpect(status().isOk());
        verify(appService, times(1))
                .updateUserApp(
                        eq(1L),
                        argThat(appDTO -> requestBody.name().equals(appDTO.name())),
                        eq(1L));
    }

    @Test
    public void return_401_When_updateUserApp_If_User_Is_Not_Authenticated() throws Exception {
        var requestBody = new AppRequestDTO("Simple CRUD application");
        var request = MockMvcRequestBuilders
                .put("/api/v1/apps/1")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(requestBody));
        mvc.perform(request)
                .andExpect(status().isUnauthorized());
        verify(appService, times(0))
                .updateUserApp(anyLong(), any(AppRequestDTO.class), anyLong());
    }

    @Test
    @WithUserDetails(
            value = "lyah.artem10@gmail.com",
            userDetailsServiceBeanName = "testUserDetailsService"
    )
    public void return_403_When_updateUserApp_If_User_Does_Not_Have_Permission() throws Exception {
        var requestBody = new AppRequestDTO("Simple CRUD application");
        var request = MockMvcRequestBuilders
                .put("/api/v1/apps/1")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(requestBody));
        mvc.perform(request)
                .andExpect(status().isForbidden());
        verify(appService, times(0))
                .updateUserApp(anyLong(), any(AppRequestDTO.class), anyLong());
    }

    @Test
    @WithUserDetails(
            value = "lyah.artem10@mail.ru",
            userDetailsServiceBeanName = "testUserDetailsService"
    )
    public void return_422_When_updateUserApp_If_Request_Body_Is_Invalid() throws Exception {
        var requestBody = new AppRequestDTO(" ");
        var request = MockMvcRequestBuilders
                .put("/api/v1/apps/1")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(requestBody));
        mvc.perform(request)
                .andExpect(status().isUnprocessableEntity());
        verify(appService, times(0))
                .updateUserApp(anyLong(), any(AppRequestDTO.class), anyLong());
    }

    @Test
    @WithUserDetails(
            value = "lyah.artem10@mail.ru",
            userDetailsServiceBeanName = "testUserDetailsService"
    )
    public void deleteUserApps_If_User_Is_Authenticated() throws Exception {
        var request = MockMvcRequestBuilders
                .delete("/api/v1/apps/1");
        mvc.perform(request)
                .andExpect(status().isOk());
        verify(appService, times(1))
                .deleteUserApp(1, 1);
    }

    @Test
    public void return_401_When_deleteUserApps_If_User_Is_Not_Authenticated() throws Exception {
        var request = MockMvcRequestBuilders
                .delete("/api/v1/apps/1");
        mvc.perform(request)
                .andExpect(status().isUnauthorized());
        verify(appService, times(0))
                .findUserApps(anyLong(), any(), any());
    }

    @Test
    @WithUserDetails(
            value = "lyah.artem10@gmail.com",
            userDetailsServiceBeanName = "testUserDetailsService"
    )
    public void return_403_When_deleteUserApps_If_User_Does_Not_Have_Permission() throws Exception {
        var request = MockMvcRequestBuilders
                .delete("/api/v1/apps/1");
        mvc.perform(request)
                .andExpect(status().isForbidden());
        verify(appService, times(0))
                .findUserApps(anyLong(), any(), any());
    }
}
