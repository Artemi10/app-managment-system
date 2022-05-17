package com.devanmejia.appmanager.integration;

import com.devanmejia.appmanager.entity.user.Authority;
import com.devanmejia.appmanager.entity.user.User;
import com.devanmejia.appmanager.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
public class UserRepositoryInTest {
    private final UserRepository userRepository;

    @Autowired
    public UserRepositoryInTest(UserRepository userRepository) {
        this.userRepository = userRepository;
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
    public void findByEmail_If_User_Exists_Test(){
        var actual = userRepository.findByEmail("lyah.artem10@mail.ru");
        assertTrue(actual.isPresent());
        assertEquals("lyah.artem10@mail.ru", actual.get().getEmail());
    }

    @Test
    public void return_Empty_When_FindByEmail_If_User_Does_Not_Exist_Test(){
        var actual = userRepository.findByEmail("empty@mail.ru");
        assertTrue(actual.isEmpty());
    }

    @Test
    public void save_New_User_Test(){
        var actualBefore = userRepository.findByEmail("toAdd@mail.ru");
        assertTrue(actualBefore.isEmpty());
        var user = User.builder()
                .email("toAdd@mail.ru")
                .password("$2y$10$ZPgg5k.SQaJIxjGF7AU15.GNVF2U7MVJJWgMxkyuXjW550XIEEK52")
                .authority(Authority.ACTIVE)
                .resetToken(null)
                .refreshToken(UUID.randomUUID().toString())
                .apps(new ArrayList<>())
                .build();
        userRepository.save(user);
        var actualAfter = userRepository.findByEmail("toAdd@mail.ru");
        assertTrue(actualAfter.isPresent());
        assertEquals("toAdd@mail.ru", actualAfter.get().getEmail());
    }

    @Test
    public void update_Existent_User_Test(){
        var actualBefore = userRepository.findByEmail("lyah.artem10@mail.ru");
        assertTrue(actualBefore.isPresent());
        assertEquals("lyah.artem10@mail.ru", actualBefore.get().getEmail());
        assertEquals(Authority.ACTIVE, actualBefore.get().getAuthority());
        var user = actualBefore.get();
        user.setAuthority(Authority.UPDATE_CONFIRMED);
        userRepository.save(user);
        var actualAfter = userRepository.findByEmail("lyah.artem10@mail.ru");
        assertTrue(actualAfter.isPresent());
        assertEquals("lyah.artem10@mail.ru", actualAfter.get().getEmail());
        assertEquals(Authority.UPDATE_CONFIRMED, actualAfter.get().getAuthority());
    }
}
