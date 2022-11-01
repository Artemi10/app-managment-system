package com.devanmejia.appmanager.repository.integration.event;

import com.devanmejia.appmanager.entity.App;
import com.devanmejia.appmanager.entity.Event;
import com.devanmejia.appmanager.repository.event.EventRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EventRepositoryInTest {
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
    private final EventRepository eventRepository;
    private final Clock clock;

    @Autowired
    public EventRepositoryInTest(EventRepository eventRepository) {
        this.clock = spy(Clock.class);
        this.eventRepository = eventRepository;
    }

    @Container
    private static final PostgreSQLContainer<?> container =
            new PostgreSQLContainer<>("postgres:latest")
                    .withDatabaseName("testpostres")
                    .withPassword("2424285")
                    .withUsername("postgres");

    @DynamicPropertySource
    public static void overrideProperties(DynamicPropertyRegistry registry){
        registry.add("spring.datasource.url", container::getJdbcUrl);
        registry.add("spring.datasource.username", container::getUsername);
        registry.add("spring.datasource.password", container::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.PostgreSQL95Dialect");
        registry.add("spring.jpa.properties.hibernate.dialect", () -> "org.hibernate.dialect.PostgreSQL95Dialect");
        registry.add("spring.flyway.locations", () -> "classpath:db/migration-integration_test");
    }

    @BeforeEach
    public void initMock() {
        when(clock.getZone()).thenReturn(NOW.getZone());
        when(clock.instant()).thenReturn(NOW.toInstant());
    }

    @Test
    @Order(1)
    public void container_Is_Up_And_Running(){
        assertTrue(container.isRunning());
    }

    @Test
    @Order(2)
    public void findEventsByApp_If_User_Has_App() {
        var actual = eventRepository.findEventsByApp(2, 1, PageRequest.of(0, 4));
        assertEquals(4, actual.size());
    }

    @Test
    @Order(3)
    public void return_Empty_List_When_FindEventsByApp_If_User_Does_Not_Have_App() {
        var actual = eventRepository.findEventsByApp(2, 3, PageRequest.of(0, 4));
        assertTrue(actual.isEmpty());
    }

    @Test
    @Order(4)
    public void return_Empty_List_When_FindEventsByApp_If_App_Does_Not_Exist() {
        var actual = eventRepository.findEventsByApp(12, 1, PageRequest.of(0, 4));
        assertTrue(actual.isEmpty());
    }

    @Test
    @Order(5)
    public void return_Empty_List_When_FindEventsByApp_If_User_Does_Not_Exist() {
        var actual = eventRepository.findEventsByApp(2, 6, PageRequest.of(0, 4));
        assertTrue(actual.isEmpty());
    }

    @Test
    @Order(6)
    public void return_Empty_List_When_FindEventsByApp_If_App_Does_Not_Have_Events() {
        var actual = eventRepository.findEventsByApp(1, 1, PageRequest.of(0, 4));
        assertTrue(actual.isEmpty());
    }

    @Test
    @Order(7)
    public void save_If_App_Exists() {
        var actualBefore = eventRepository.findEventsByApp(2, 1, PageRequest.of(0, 4));
        assertEquals(4, actualBefore.size());
        var creationTime = OffsetDateTime.now(clock).minusHours(3);
        var app = App.builder()
                .id(2)
                .build();
        var event = Event.builder()
                .name("Test event name")
                .extraInformation("Test information")
                .creationTime(creationTime)
                .app(app)
                .build();
        eventRepository.save(event);
        var actualAfter = eventRepository.findEventsByApp(2, 1, PageRequest.of(0, 5));
        assertEquals(5, actualAfter.size());
    }
}
