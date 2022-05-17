package com.devanmejia.appmanager.service.event;

import com.devanmejia.appmanager.entity.App;
import com.devanmejia.appmanager.entity.Event;
import com.devanmejia.appmanager.exception.EntityException;
import com.devanmejia.appmanager.repository.EventRepository;
import com.devanmejia.appmanager.service.app.AppService;
import com.devanmejia.appmanager.transfer.event.EventRequestDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

        when(eventRepository.findEventsByApp(eq(1L), eq("lyah.artem10@mail.ru")))
                .thenReturn(events);
        when(eventRepository.findEventsByApp(eq(2L), eq("lyah.artem@mail.ru")))
                .thenReturn(new ArrayList<>());

        when(appService.isUserApp(eq(1L), eq("lyah.artem10@mail.ru")))
                .thenReturn(true);
        when(appService.isUserApp(eq(2L), eq("lyah.artem@mail.ru")))
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
        var actual = eventService.findAppEvents(1, "lyah.artem10@mail.ru");
        assertEquals(4, actual.size());
        verify(eventRepository, times(1))
                .findEventsByApp(1, "lyah.artem10@mail.ru");
    }

    @Test
    public void return_Empty_List_If_Events_Do_Not_Exist(){
        var actual = eventService.findAppEvents(2, "lyah.artem@mail.ru");
        assertTrue(actual.isEmpty());
        verify(eventRepository, times(1))
                .findEventsByApp(2, "lyah.artem@mail.ru");
    }

    @Test
    public void addEvent_When_App_Exists(){
        var requestBody = new EventRequestDTO("New event", "Description");
        Assertions.assertDoesNotThrow(() -> eventService.addEvent(1, requestBody, "lyah.artem10@mail.ru"));
        verify(eventRepository, times(1))
                .save(argThat(event -> event.getName().equals(requestBody.name())
                                && event.getExtraInformation().equals(requestBody.extraInformation())
                                && event.getApp().getId() == 1));
        verify(appService, times(1))
                .isUserApp(1, "lyah.artem10@mail.ru");
    }

    @Test
    public void throw_Exception_When_Add_Event_To_Nonexistent_App(){
        var requestBody = new EventRequestDTO("New event", "Description");
        var exception = assertThrows(
                EntityException.class,
                () -> eventService.addEvent(2, requestBody, "lyah.artem@mail.ru"));
        assertEquals("Application not found", exception.getMessage());
        verify(appService, times(1))
                .isUserApp(2, "lyah.artem@mail.ru");
    }
}
