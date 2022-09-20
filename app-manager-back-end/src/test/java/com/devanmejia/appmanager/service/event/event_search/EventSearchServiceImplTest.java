package com.devanmejia.appmanager.service.event.event_search;

import com.devanmejia.appmanager.entity.App;
import com.devanmejia.appmanager.entity.Event;
import com.devanmejia.appmanager.repository.event.EventNameSearchRepository;
import com.devanmejia.appmanager.transfer.criteria.PageCriteria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class EventSearchServiceImplTest {
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
    private final EventNameSearchRepository eventNameSearchRepository;
    private final EventSearchService eventSearchService;
    private final Clock clock;

    @Autowired
    public EventSearchServiceImplTest() {
        this.clock = spy(Clock.class);
        this.eventNameSearchRepository = spy(EventNameSearchRepository.class);
        this.eventSearchService = new EventSearchServiceImpl(eventNameSearchRepository);
    }

    @BeforeEach
    public void initMock() {
        when(clock.getZone()).thenReturn(NOW.getZone());
        when(clock.instant()).thenReturn(NOW.toInstant());

        var appEvent = List.of(
                Event.builder()
                        .id(1)
                        .name("Log In")
                        .extraInformation("User successfully log in")
                        .creationTime(OffsetDateTime.now(clock).minusHours(3))
                        .app(App.builder().id(1).build())
                        .build(),
                Event.builder()
                        .id(2)
                        .name("Sign Up")
                        .extraInformation("New user successfully sign up")
                        .creationTime(OffsetDateTime.now(clock).plusHours(4))
                        .app(App.builder().id(1).build())
                        .build(),
                Event.builder()
                        .id(3)
                        .name("Refresh token")
                        .extraInformation("User updated his tokens")
                        .creationTime(OffsetDateTime.now(clock).plusHours(5))
                        .app(App.builder().id(1).build())
                        .build(),
                Event.builder()
                        .id(4)
                        .name("Credentials modification")
                        .extraInformation("User updated his credentials")
                        .creationTime(OffsetDateTime.now(clock).plusHours(6))
                        .app(App.builder().id(1).build())
                        .build()
        );

        when(eventNameSearchRepository.findAppEventsByName(1L, 1L, "User:*", 4, 0L))
                .thenReturn(appEvent);

        when(eventNameSearchRepository.findAppEventsByName(eq(2L), anyLong(), anyString(), anyInt(), anyLong()))
                .thenReturn(new ArrayList<>());

        when(eventNameSearchRepository.getAppEventsAmountByName(1L, 1L, "User:*"))
                .thenReturn(appEvent.size());

        when(eventNameSearchRepository.getAppEventsAmountByName(eq(2L), anyLong(), anyString()))
                .thenReturn(0);
    }

    @Test
    public void findAppEvents_If_Events_Exist() {
        var id = 1L;
        var userId = 1L;
        var searchName = "User";
        var actual = eventSearchService.findAppEvents(id, userId, searchName, new PageCriteria(1, 4));
        assertEquals(4, actual.size());
        verify(eventNameSearchRepository, times(1))
                .findAppEventsByName(id, userId, searchName + ":*", 4, 0L);
    }

    @Test
    public void returnEmptyList_When_findAppEvents_If_Apps_Do_Not_Exist() {
        var id = 2;
        var userId = 5;
        var searchName = "User";
        var actual = eventSearchService.findAppEvents(id, userId, searchName, new PageCriteria(8, 10));
        assertEquals(0, actual.size());
        verify(eventNameSearchRepository, times(1))
                .findAppEventsByName(id, userId, searchName + ":*", 10, 70);
    }

    @Test
    public void getAppEventsAmount_Test() {
        var id = 1;
        var userId = 1L;
        var searchName = "User";
        var actual = eventSearchService.getAppEventsAmount(id, userId, searchName);
        assertEquals(4, actual);
        verify(eventNameSearchRepository, times(1))
                .getAppEventsAmountByName(id, userId, searchName + ":*");
    }

    @Test
    public void getAppEventsAmount_If_Events_Are_Empty_Test() {
        var id = 2;
        var userId = 5;
        var searchName = "User";
        var actual = eventSearchService.getAppEventsAmount(id, userId, searchName);
        assertEquals(0, actual);
        verify(eventNameSearchRepository, times(1))
                .getAppEventsAmountByName(id, userId, searchName + ":*");
    }
}
