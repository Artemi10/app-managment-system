package com.devanmejia.appmanager.integration;

import com.devanmejia.appmanager.entity.App;
import com.devanmejia.appmanager.entity.Event;
import com.devanmejia.appmanager.repository.EventRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Timestamp;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EventRepositoryInTest {
    private final EventRepository eventRepository;

    @Autowired
    public EventRepositoryInTest(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Container
    private static final PostgreSQLContainer<?> container =
            new PostgreSQLContainer<>("postgres:latest")
                    .withDatabaseName("testpostres")
                    .withPassword("2424285")
                    .withUsername("postgres")
                    .withInitScript("database-integration/init.sql");

    @DynamicPropertySource
    public static void overrideProperties(DynamicPropertyRegistry registry){
        registry.add("spring.datasource.url", container::getJdbcUrl);
        registry.add("spring.datasource.username", container::getUsername);
        registry.add("spring.datasource.password", container::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.PostgreSQL95Dialect");
        registry.add("spring.jpa.properties.hibernate.dialect", () -> "org.hibernate.dialect.PostgreSQL95Dialect");
    }

    @Test
    @Order(1)
    public void container_Is_Up_And_Running(){
        assertTrue(container.isRunning());
    }

    @Test
    @Order(2)
    public void findEventsByApp_If_User_Has_App() {
        var actual = eventRepository.findEventsByApp(2, 1);
        assertEquals(4, actual.size());
    }

    @Test
    @Order(3)
    public void return_Empty_List_When_FindEventsByApp_If_User_Does_Not_Have_App() {
        var actual = eventRepository.findEventsByApp(2, 3);
        assertTrue(actual.isEmpty());
    }

    @Test
    @Order(4)
    public void return_Empty_List_When_FindEventsByApp_If_App_Does_Not_Exist() {
        var actual = eventRepository.findEventsByApp(12, 1);
        assertTrue(actual.isEmpty());
    }

    @Test
    @Order(5)
    public void return_Empty_List_When_FindEventsByApp_If_User_Does_Not_Exist() {
        var actual = eventRepository.findEventsByApp(2, 6);
        assertTrue(actual.isEmpty());
    }

    @Test
    @Order(6)
    public void return_Empty_List_When_FindEventsByApp_If_App_Does_Not_Have_Events() {
        var actual = eventRepository.findEventsByApp(1, 1);
        assertTrue(actual.isEmpty());
    }

    @Test
    @Order(7)
    public void save_If_App_Exists() {
        var actualBefore = eventRepository.findEventsByApp(2, 1);
        assertEquals(4, actualBefore.size());
        var time = new Timestamp(new Date().getTime());
        var app = App.builder()
                .id(2)
                .build();
        var event = Event.builder()
                .name("Test event name")
                .extraInformation("Test information")
                .time(time)
                .app(app)
                .build();
        eventRepository.save(event);
        var actualAfter = eventRepository.findEventsByApp(2, 1);
        assertEquals(5, actualAfter.size());
    }
}
