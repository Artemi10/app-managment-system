package com.devanmejia.appmanager.service.app.app_search;

import com.devanmejia.appmanager.entity.App;
import com.devanmejia.appmanager.repository.app.AppNameSearchRepository;
import com.devanmejia.appmanager.transfer.criteria.PageCriteria;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
class AppSearchServiceImplTest {
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
    private final AppNameSearchRepository appNameSearchRepository;
    private final AppSearchService appSearchService;
    private final Clock clock;

    @Autowired
    public AppSearchServiceImplTest() {
        this.clock = spy(Clock.class);
        this.appNameSearchRepository = spy(AppNameSearchRepository.class);
        this.appSearchService = new AppSearchServiceImpl(appNameSearchRepository);
    }

    @BeforeEach
    public void initMock() {
        when(clock.getZone()).thenReturn(NOW.getZone());
        when(clock.instant()).thenReturn(NOW.toInstant());

        var userApps = Lists.list(
                App.builder()
                        .id(1)
                        .name("Simple CRUD App")
                        .creationTime(OffsetDateTime.now(clock).minusHours(3))
                        .build(),
                App.builder()
                        .id(2)
                        .name("Todo list App")
                        .creationTime(OffsetDateTime.now(clock).minusHours(2))
                        .build(),
                App.builder()
                        .id(3)
                        .name("Flight Timetable App")
                        .creationTime(OffsetDateTime.now(clock).minusHours(1))
                        .build(),
                App.builder()
                        .id(4)
                        .name("User chat app")
                        .creationTime(OffsetDateTime.now(clock).minusMinutes(30))
                        .build()
        );

        when(appNameSearchRepository.findUserAppsByName(1L, "app:*", 4, 0L))
                .thenReturn(userApps);

        when(appNameSearchRepository.findUserAppsByName(eq(2L), anyString(), anyInt(), anyLong()))
                .thenReturn(new ArrayList<>());

        when(appNameSearchRepository.getUserAppsAmountByName(1, "app:*"))
                .thenReturn(userApps.size());

        when(appNameSearchRepository.getUserAppsAmountByName(eq(2L), any()))
                .thenReturn(0);
    }

    @Test
    public void findUserApps_If_Apps_Exist() {
        var id = 1L;
        var searchName = "app";
        var actual = appSearchService.findUserApps(id, searchName, new PageCriteria(1, 4));
        assertEquals(4, actual.size());
        verify(appNameSearchRepository, times(1))
                .findUserAppsByName(id, searchName + ":*", 4, 0L);
    }

    @Test
    public void returnEmptyList_When_findUserApps_If_Apps_Do_Not_Exist() {
        var id = 2;
        var searchName = "app";
        var actual = appSearchService.findUserApps(id, searchName, new PageCriteria(8, 9));
        assertEquals(0, actual.size());
        verify(appNameSearchRepository, times(1))
                .findUserAppsByName(id, searchName + ":*", 9, 63);
    }

    @Test
    public void getUserAppsAmount_Test() {
        var id = 1;
        var searchName = "app";
        var actual = appSearchService.getUserAppsAmount(id, searchName);
        assertEquals(4, actual);
        verify(appNameSearchRepository, times(1))
                .getUserAppsAmountByName(id, searchName + ":*");
    }

    @Test
    public void getUserAppsAmount_If_Apps_Are_Empty_Test() {
        var id = 2;
        var searchName = "app";
        var actual = appSearchService.getUserAppsAmount(id, searchName);
        assertEquals(0, actual);
        verify(appNameSearchRepository, times(1))
                .getUserAppsAmountByName(id, searchName + ":*");
    }
}
