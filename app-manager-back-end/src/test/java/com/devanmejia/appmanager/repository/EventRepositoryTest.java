package com.devanmejia.appmanager.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
@ExtendWith(SpringExtension.class)
public class EventRepositoryTest {
    private final EventRepository eventRepository;

    @Autowired
    public EventRepositoryTest(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Test
    public void findEventsByApp_Test(){
        var actual = eventRepository.findEventsByApp(2, 1);
        assertEquals(4, actual.size());
    }

    @Test
    public void return_Empty_List_If_User_Does_Not_Have_App(){
        var actual = eventRepository.findEventsByApp(2, 3);
        assertTrue(actual.isEmpty());
    }

    @Test
    public void return_Empty_List_If_App_Does_Not_Exist(){
        var actual = eventRepository.findEventsByApp(24, 1);
        assertTrue(actual.isEmpty());
    }

    @Test
    public void return_Empty_List_If_App_Does_Not_Have_Events(){
        var actual = eventRepository.findEventsByApp(1, 1);
        assertTrue(actual.isEmpty());
    }
}
