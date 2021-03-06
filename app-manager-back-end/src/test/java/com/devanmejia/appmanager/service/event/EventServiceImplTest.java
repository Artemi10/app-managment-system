package com.devanmejia.appmanager.service.event;

import com.devanmejia.appmanager.entity.Event;
import com.devanmejia.appmanager.exception.EntityException;
import com.devanmejia.appmanager.repository.EventRepository;
import com.devanmejia.appmanager.service.app.AppService;
import com.devanmejia.appmanager.transfer.criteria.PageCriteria;
import com.devanmejia.appmanager.transfer.event.EventRequestDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class EventServiceImplTest {
    private final AppService appService;
    private final EventRepository eventRepository;
    private final EventService eventService;

    @Autowired
    public EventServiceImplTest() {
        this.appService = spy(AppService.class);
        this.eventRepository = spy(EventRepository.class);
        this.eventService = new EventServiceImpl(appService, eventRepository);
    }

    @BeforeEach
    public void initMocks(){
        var events = List.of(
                Event.builder()
                        .id(1)
                        .name("User successfully signed up")
                        .extraInformation("Extra information")
                        .time(new Timestamp(new Date().getTime()))
                        .build(),
                Event.builder()
                        .id(2)
                        .name("User successfully logged in")
                        .extraInformation("Extra information")
                        .time(new Timestamp(new Date().getTime()))
                        .build(),
                Event.builder()
                        .id(3)
                        .name("Add new note")
                        .extraInformation("Extra information")
                        .time(new Timestamp(new Date().getTime()))
                        .build(),
                Event.builder()
                        .id(4)
                        .name("Update note")
                        .extraInformation("Extra information")
                        .time(new Timestamp(new Date().getTime()))
                        .build()
        );

        when(eventRepository.findEventsByApp(eq(1L), eq(1L), eq(PageRequest.of(0, 4))))
                .thenReturn(events);
        when(eventRepository.findEventsByApp(eq(2L), eq(3L), any()))
                .thenReturn(new ArrayList<>());
        when(eventRepository.findEvent(eq(1L), eq(1L), eq(1L)))
                .thenReturn(Optional.of(events.get(0)));
        when(eventRepository.findEvent(eq(2L), eq(1L), eq(1L)))
                .thenReturn(Optional.empty());

        when(appService.isUserApp(eq(1L), eq(1L)))
                .thenReturn(true);
        when(appService.isUserApp(eq(2L), eq(3L)))
                .thenReturn(false);
        when(eventRepository.save(any(Event.class)))
                .thenAnswer(answer -> {
                    var event = (Event) answer.getArgument(0);
                    return Event.builder()
                            .id(5)
                            .name(event.getName())
                            .time(event.getTime())
                            .extraInformation(event.getExtraInformation())
                            .app(event.getApp())
                            .build();
                });
    }

    @Test
    public void findAppEvents_Test(){
        var actual = eventService.findAppEvents(1, 1, new PageCriteria(1, 4));
        assertEquals(4, actual.size());
        verify(eventRepository, times(1))
                .findEventsByApp(1, 1, PageRequest.of(0, 4));
    }

    @Test
    public void return_Empty_List_If_Events_Do_Not_Exist(){
        var actual = eventService.findAppEvents(2, 3, new PageCriteria(1, 4));
        assertTrue(actual.isEmpty());
        verify(eventRepository, times(1))
                .findEventsByApp(2, 3, PageRequest.of(0, 4));
    }

    @Test
    public void addEvent_When_App_Exists(){
        var requestBody = new EventRequestDTO("New event", "Description");
        Assertions.assertDoesNotThrow(() -> eventService.addAppEvent(1, requestBody, 1));
        verify(eventRepository, times(1))
                .save(argThat(event -> event.getName().equals(requestBody.name())
                                && event.getExtraInformation().equals(requestBody.extraInformation())
                                && event.getApp().getId() == 1));
        verify(appService, times(1))
                .isUserApp(1, 1);
    }

    @Test
    public void throw_Exception_When_Add_Event_To_Nonexistent_App(){
        var requestBody = new EventRequestDTO("New event", "Description");
        var exception = assertThrows(
                EntityException.class,
                () -> eventService.addAppEvent(2, requestBody, 3));
        assertEquals("Application not found", exception.getMessage());
        verify(appService, times(1))
                .isUserApp(2, 3);
    }

    @Test
    public void deleteAppEvent_If_Event_Exists_Test() {
        assertDoesNotThrow(() -> eventService.deleteAppEvent(1, 1, 1));
        verify(eventRepository, times(1))
                .delete(argThat(event -> event.getId() == 1));
    }

    @Test
    public void throw_Exception_When_deleteAppEvent_If_Event_Does_Not_Exist_Test() {
        var exception = assertThrows(
                EntityException.class,
                () -> eventService.deleteAppEvent(2, 1, 1));
        assertEquals("Event not found", exception.getMessage());
        verify(eventRepository, times(0))
                .delete(argThat(event -> event.getId() == 2));
    }

    @Test
    public void updateAppEvent_If_Event_Exists_Test() {
        var requestBody = new EventRequestDTO("New event name", "New event extra information");
        assertDoesNotThrow(() -> eventService.updateAppEvent(1, 1, requestBody, 1));
        verify(eventRepository, times(1))
                .save(argThat(event -> event.getId() == 1
                        && event.getExtraInformation().equals(requestBody.extraInformation())
                        && event.getName().equals(requestBody.name())));
    }

    @Test
    public void throw_Exception_When_updateAppEvent_If_Event_Does_Not_Exist_Test() {
        var requestBody = new EventRequestDTO("New event name", "New event extra information");
        var exception = assertThrows(
                EntityException.class,
                () -> eventService.updateAppEvent(2, 1, requestBody, 1));
        assertEquals("Event not found", exception.getMessage());
        verify(eventRepository, times(0))
                .save(argThat(event -> event.getId() == 2));
    }
}
