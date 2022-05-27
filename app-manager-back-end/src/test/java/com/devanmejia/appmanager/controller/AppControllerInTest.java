package com.devanmejia.appmanager.controller;

import com.devanmejia.appmanager.configuration.TestUserDetailsService;
import com.devanmejia.appmanager.configuration.security.JwtAuthenticationEntryPoint;
import com.devanmejia.appmanager.configuration.security.JwtAuthenticationManager;
import com.devanmejia.appmanager.configuration.security.providers.JwtProvider;
import com.devanmejia.appmanager.configuration.security.token.JwtService;
import com.devanmejia.appmanager.controller.app.AppController;
import com.devanmejia.appmanager.exception.EntityException;
import com.devanmejia.appmanager.service.app.AppService;
import com.devanmejia.appmanager.transfer.app.AppRequestDTO;
import com.devanmejia.appmanager.transfer.app.AppResponseDTO;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@WebMvcTest(AppController.class)
public class AppControllerInTest {
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
        when(appService.findUserApp(1, 1))
                .thenReturn(new AppResponseDTO(1, "Simple CRUD App", "21 марта 2022 16:24:54"));
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
    public void findUserApps_If_User_Is_Authenticated() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/api/v1/apps");
        mvc.perform(request)
                .andExpect(status().isOk());
        verify(appService, times(1))
                .findUserApps(
                        eq(1L),
                        argThat(pageCriteria -> pageCriteria.getPage() == 1 && pageCriteria.getPageSize() == 3),
                        argThat(sortCriteria -> sortCriteria.getValue().equals("id") && sortCriteria.isDescending()));
    }

    @Test
    public void return_401_When_findUserApps_If_User_Is_Not_Authenticated() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/api/v1/apps");
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
    public void return_403_When_findUserApps_If_User_Does_Not_Have_Permission() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/api/v1/apps");
        mvc.perform(request)
                .andExpect(status().isForbidden());
        verify(appService, times(0))
                .findUserApps(anyLong(), any(), any());
    }

    @Test
    @WithUserDetails(
            value = "lyah.artem10@mail.ru",
            userDetailsServiceBeanName = "testUserDetailsService"
    )
    public void getPageAmount_If_User_Is_Authenticated() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/api/v1/apps/count?pageSize=3");
        mvc.perform(request)
                .andExpect(status().isOk());
        verify(appService, times(1))
                .getPageAmount(3, 1);
    }

    @Test
    public void  return_401_When_getPageAmount_If_User_Is_Not_Authenticated() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/api/v1/apps/count?pageSize=3");
        mvc.perform(request)
                .andExpect(status().isUnauthorized());
        verify(appService, times(0))
                .getPageAmount(anyInt(), anyLong());
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
                .andExpect(status().isForbidden());
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
                .content(objectMapper.writeValueAsString(requestBody));
        mvc.perform(request)
                .andExpect(status().isOk());
        verify(appService, times(1))
                .addUserApp(
                        eq(1L),
                        argThat(appDTO -> requestBody.name().equals(appDTO.name())));
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
                .addUserApp(anyLong(), any(AppRequestDTO.class));
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
                .addUserApp(anyLong(), any(AppRequestDTO.class));
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
                .addUserApp(anyLong(), any(AppRequestDTO.class));
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
