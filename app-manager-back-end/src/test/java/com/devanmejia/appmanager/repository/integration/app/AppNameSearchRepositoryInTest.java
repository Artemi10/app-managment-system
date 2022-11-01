package com.devanmejia.appmanager.repository.integration.app;

import com.devanmejia.appmanager.repository.app.AppNameSearchRepository;
import com.devanmejia.appmanager.repository.app.AppRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AppNameSearchRepositoryInTest {
    private final AppNameSearchRepository appNameSearchRepository;

    @Autowired
    public AppNameSearchRepositoryInTest(AppNameSearchRepository appNameSearchRepository) {
        this.appNameSearchRepository = appNameSearchRepository;
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
    public void findUserAppsByName_Test() {
        var apps = appNameSearchRepository.findUserAppsByName(1, "app", 5, 0);
        assertEquals(1, apps.size());
        assertEquals("Simple CRUD App", apps.get(0).getName());
    }

    @Test
    public void return_Empty_List_When_User_Does_Not_Have_Apps_When_findUserAppsByName_Test() {
        var apps = appNameSearchRepository.findUserAppsByName(1, "app", 5, 2);
        assertEquals(0, apps.size());
        var emptyApps = appNameSearchRepository.findUserAppsByName(1, "art", 1, 0);
        assertEquals(0, emptyApps.size());
    }

    @Test
    public void getUserAppsAmountByName_Test() {
        var appAmount = appNameSearchRepository.getUserAppsAmountByName(1, "app");
        assertEquals(1, appAmount);
        var emptyAppAmount = appNameSearchRepository.getUserAppsAmountByName(1, "art");
        assertEquals(0, emptyAppAmount);
    }

}
