package com.devanmejia.appmanager.repository.integration.event;

import com.devanmejia.appmanager.repository.event.EventNameSearchRepository;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EventNameSearchRepositoryInTest {
    private final EventNameSearchRepository eventNameSearchRepository;

    @Autowired
    public EventNameSearchRepositoryInTest(EventNameSearchRepository eventNameSearchRepository) {
        this.eventNameSearchRepository = eventNameSearchRepository;
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

    @Test
    @Order(1)
    public void container_Is_Up_And_Running(){
        assertTrue(container.isRunning());
    }

    @Test
    public void findAppEventsByName_By_Event_Name_Test() {
        var eventsByName = eventNameSearchRepository
                .findAppEventsByName(2, 1, "success", 5, 0);
        assertEquals(2, eventsByName.size());
        assertEquals("User successfully signed up", eventsByName.get(0).getName());
        assertEquals("User successfully logged in", eventsByName.get(1).getName());
    }

    @Test
    public void findAppEventsByName_By_Event_ExtraInformation_Test() {
        var eventsByName = eventNameSearchRepository
                .findAppEventsByName(2, 1, "extra", 5, 0);
        assertEquals(4, eventsByName.size());
        assertTrue(eventsByName.get(0).getExtraInformation().map(inf -> inf.equals("Extra information")).orElse(false));
        assertTrue(eventsByName.get(1).getExtraInformation().map(inf -> inf.equals("Extra information")).orElse(false));
        assertTrue(eventsByName.get(2).getExtraInformation().map(inf -> inf.equals("Extra information")).orElse(false));
        assertTrue(eventsByName.get(3).getExtraInformation().map(inf -> inf.equals("Extra information")).orElse(false));
    }

    @Test
    public void getAppEventsAmountByName_Test() {
        var eventAmount = eventNameSearchRepository
                .getAppEventsAmountByName(2, 1, "extra");
        assertEquals(4, eventAmount);
    }

    @Test
    public void return_Empty_List_When_User_App_Not_Have_Events_When_getAppEventsAmountByName_Test() {
        var eventAmount = eventNameSearchRepository
                .getAppEventsAmountByName(2, 1, "empty");
        assertEquals(0, eventAmount);
    }
}
