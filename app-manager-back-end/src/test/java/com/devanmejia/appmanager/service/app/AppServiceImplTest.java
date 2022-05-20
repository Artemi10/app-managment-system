package com.devanmejia.appmanager.service.app;

import com.devanmejia.appmanager.entity.App;
import com.devanmejia.appmanager.exception.EntityException;
import com.devanmejia.appmanager.repository.AppRepository;
import com.devanmejia.appmanager.transfer.app.AppRequestDTO;
import com.devanmejia.appmanager.transfer.criteria.PageCriteria;
import com.devanmejia.appmanager.transfer.criteria.SortCriteria;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class AppServiceImplTest {
    private final AppRepository appRepository;
    private final AppService appService;
    private List<App> userApps;
    private List<App> fullPageUserApps;

    @Autowired
    public AppServiceImplTest() {
        this.appRepository = spy(AppRepository.class);
        this.appService = new AppServiceImpl(appRepository);
    }

    @BeforeEach
    public void initApps() {
        userApps = Lists.list(
                App.builder()
                        .id(1)
                        .name("Simple CRUD App")
                        .creationTime(new Timestamp(new Date().getTime()))
                        .build(),
                App.builder()
                        .id(2)
                        .name("Todo list")
                        .creationTime(new Timestamp(new Date().getTime()))
                        .build(),
                App.builder()
                        .id(3)
                        .name("Flight Timetable")
                        .creationTime(new Timestamp(new Date().getTime()))
                        .build(),
                App.builder()
                        .id(4)
                        .name("User chat")
                        .creationTime(new Timestamp(new Date().getTime()))
                        .build()
        );
        fullPageUserApps = Lists.list(
                App.builder()
                        .id(1)
                        .name("Online shop")
                        .creationTime(new Timestamp(new Date().getTime()))
                        .build(),
                App.builder()
                        .id(2)
                        .name("Order manager")
                        .creationTime(new Timestamp(new Date().getTime()))
                        .build(),
                App.builder()
                        .id(3)
                        .name("Todo list App")
                        .creationTime(new Timestamp(new Date().getTime()))
                        .build()
        );
    }

    @BeforeEach
    public void initMock() {
        when(appRepository.findAllByUserEmail(
                eq("lyah.artem10@gmail.com"),
                argThat(pageable -> pageable.getPageNumber() == 0)))
                .thenReturn(new PageImpl<>(fullPageUserApps));
        when(appRepository.findAllByUserEmail(
                eq("lyah.artem10@gmail.com"),
                argThat(pageable -> pageable.getPageNumber() == 1)))
                .thenReturn(Page.empty());
        doThrow(new InvalidDataAccessApiUsageException("invalid data"))
                .when(appRepository)
                .findAllByUserEmail(eq("lyah.artem100@gmail.com"), any());
        when(appRepository.getUserAppsAmount("lyah.artem10@gmail.com"))
                .thenReturn(fullPageUserApps.size());
        when(appRepository.getUserAppsAmount("lyah.artem10@mail.ru"))
                .thenReturn(userApps.size());
        when(appRepository.getUserAppsAmount(
                argThat(email -> !email.equals("lyah.artem10@mail.ru") && !email.equals("lyah.artem10@gmail.com"))))
                .thenReturn(0);
        when(appRepository.findUserAppById(1, "lyah.artem10@mail.ru"))
                .thenReturn(Optional.of(userApps.get(0)));
        when(appRepository.findUserAppById(anyInt(), argThat(email -> !email.equals("lyah.artem10@mail.ru"))))
                .thenReturn(Optional.empty());
        when(appRepository.save(any(App.class)))
                .thenAnswer(answer -> {
                    var app = (App) answer.getArgument(0);
                    return App.builder()
                            .id(5)
                            .name(app.getName())
                            .events(app.getEvents())
                            .creationTime(app.getCreationTime())
                            .user(app.getUser())
                            .build();
                });
    }

    @Test
    public void findUserApps_First_Page_Test(){
        var pageCriteria = new PageCriteria(1, 3);
        var expected = appService
                .findUserApps("lyah.artem10@gmail.com", pageCriteria, new SortCriteria());
        assertEquals(3, expected.size());
    }

    @Test
    public void findUserApps_Second_Page_Test(){
        var pageCriteria = new PageCriteria(2, 3);
        var expected = appService
                .findUserApps("lyah.artem10@gmail.com", pageCriteria, new SortCriteria());
        assertTrue(expected.isEmpty());
    }

    @Test
    public void throwException_When_findUserApps_If_Sorting_Field_Does_Not_Exist_Test(){
        var sortCriteria = new SortCriteria("amount", true);
        var pageCriteria = new PageCriteria(1, 3);
        var exception= assertThrows(
                EntityException.class,
                () -> appService.findUserApps("lyah.artem100@gmail.com", pageCriteria, sortCriteria)
        );
        assertEquals("Sorting param is invalid. Field amount does not exist.", exception.getMessage());
    }

    @Test
    public void getPageAmount_From_Full_PageUser_Apps_Test() {
        assertEquals(1, appService.getPageAmount(3, "lyah.artem10@gmail.com"));
        verify(appRepository, times(1))
                .getUserAppsAmount("lyah.artem10@gmail.com");
    }

    @Test
    public void getPageAmount_From_User_Apps_Test() {
        assertEquals(2, appService.getPageAmount(3, "lyah.artem10@mail.ru"));
        verify(appRepository, times(1))
                .getUserAppsAmount("lyah.artem10@mail.ru");
    }

    @Test
    public void getPageAmount_From_Empty_User_Apps_Test() {
        assertEquals(1, appService.getPageAmount(3, "lyah.artem10@gmail.com"));
        verify(appRepository, times(1))
                .getUserAppsAmount("lyah.artem10@gmail.com");
    }

    @Test
    public void add_New_User_App_When_App_Is_Valid() {
        var userId = 6;
        var correctDTO = new AppRequestDTO("ToDo List App");
        assertDoesNotThrow(() -> appService.addUserApp(userId, correctDTO));
        verify(appRepository, times(1))
                .save(argThat(app ->
                        app.getName().equals(correctDTO.name())
                        && app.getUser().getId() == userId)
                );
    }

    @Test
    public void find_User_App_If_Exists() {
        var appId = 1;
        var email = "lyah.artem10@mail.ru";
        var response = assertDoesNotThrow(() -> appService.findUserApp(appId, email));
        assertEquals(appId, response.id());
        assertEquals("Simple CRUD App", response.name());
        verify(appRepository, times(1)).findUserAppById(appId, email);
    }

    @Test
    public void throw_Exception_When_Find_Not_Existent_App() {
        var exception = assertThrows(EntityException.class,
                () -> appService.findUserApp(1, "lyah.artem03@mail.ru"));
        assertEquals("Application not found", exception.getMessage());
        verify(appRepository, times(1)).findUserAppById(1, "lyah.artem03@mail.ru");
        exception = assertThrows(EntityException.class,
                () -> appService.findUserApp(10, "lyah.artem03@mail.ru"));
        assertEquals("Application not found", exception.getMessage());
        verify(appRepository, times(1)).findUserAppById(10, "lyah.artem03@mail.ru");
    }

    @Test
    public void update_User_App_If_Exists() {
        var appId = 1;
        var email = "lyah.artem10@mail.ru";
        var appDTO = new AppRequestDTO("TODO List app");
        assertDoesNotThrow(() -> appService.updateUserApp(appId, appDTO, email));
        verify(appRepository, times(1))
                .save(argThat(app -> app.getId() == appId
                        && app.getName().equals(appDTO.name()))
                );
    }

    @Test
    public void throw_Exception_When_Update_Not_Existent_App() {
        var appDTO = new AppRequestDTO("TODO List app");
        var exception = assertThrows(EntityException.class,
                () -> appService.updateUserApp(1, appDTO, "lyah.artem03@mail.ru"));
        assertEquals("Application not found", exception.getMessage());
        verify(appRepository, times(1)).findUserAppById(1, "lyah.artem03@mail.ru");
        exception = assertThrows(EntityException.class,
                () -> appService.updateUserApp(10, appDTO, "lyah.artem03@mail.ru"));
        assertEquals("Application not found", exception.getMessage());
        verify(appRepository, times(1)).findUserAppById(10, "lyah.artem03@mail.ru");
    }

    @Test
    public void deleteUserApp_Test(){
        appService.deleteUserApp(1, "lyah.artem10@mail.ru");
        verify(appRepository, times(1))
                .deleteByIdAndUserEmail(1, "lyah.artem10@mail.ru");
    }

    @Test
    public void return_True_When_User_Has_App(){
        assertTrue(appService.isUserApp(1, "lyah.artem10@mail.ru"));
        verify(appRepository, times(1))
                .findUserAppById(1, "lyah.artem10@mail.ru");
    }

    @Test
    public void return_False_When_User_Does_Not_Have_App(){
        assertFalse(appService.isUserApp(1, "d@mail.ru"));
        verify(appRepository, times(1))
                .findUserAppById(1, "d@mail.ru");
    }
}
