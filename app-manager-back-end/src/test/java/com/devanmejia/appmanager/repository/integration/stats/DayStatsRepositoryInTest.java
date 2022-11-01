package com.devanmejia.appmanager.repository.integration.stats;

import com.devanmejia.appmanager.repository.stats.DayStatsRepository;
import com.devanmejia.appmanager.repository.stats.StatsRepository;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
public class DayStatsRepositoryInTest {
    private static DataSource DATASOURCE;
    private static DateTimeFormatter FORMATTER;
    private final StatsRepository statsRepository;

    @Autowired
    public DayStatsRepositoryInTest() {
        this.statsRepository = new DayStatsRepository(new JdbcTemplate(DATASOURCE));
    }

    @BeforeAll
    public static void init() {
        var config = new HikariConfig();
        config.setPassword(container.getPassword());
        config.setUsername(container.getUsername());
        config.setJdbcUrl(container.getJdbcUrl());
        DATASOURCE = new HikariDataSource(config);
        FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:s Z");
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
    public void container_Is_Up_And_Running(){
        assertTrue(container.isRunning());
    }

    @Test
    public void getRawApplicationStatsByDays_Test() {
        var from = OffsetDateTime.parse("2022-03-14 22:14:07 +0300", FORMATTER);
        var to = OffsetDateTime.parse("2022-04-14 23:39:07 +0300", FORMATTER);
        var expected = statsRepository.getRawApplicationStats(2, from, to);
        assertEquals(3, expected.size());
        assertEquals(2, expected.get("14.03.2022"));
        assertEquals(1, expected.get("17.03.2022"));
        assertEquals(1, expected.get("14.04.2022"));
    }
}
