package com.devanmejia.appmanager.integration;

import com.devanmejia.appmanager.repository.stats.StatsRepository;
import com.devanmejia.appmanager.repository.stats.StatsRepositoryImpl;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@SpringBootTest
public class StatsRepositoryInTest {
    private static DataSource DATASOURCE;
    private static SimpleDateFormat FORMATTER;
    private final StatsRepository statsRepository;

    @Autowired
    public StatsRepositoryInTest() {
        this.statsRepository = new StatsRepositoryImpl(new JdbcTemplate(DATASOURCE));
    }

    @BeforeAll
    public static void init() {
        var config = new HikariConfig();
        config.setPassword(container.getPassword());
        config.setUsername(container.getUsername());
        config.setJdbcUrl(container.getJdbcUrl());
        DATASOURCE = new HikariDataSource(config);
        FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:s");
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
    public void container_Is_Up_And_Running(){
        assertTrue(container.isRunning());
    }

    @Test
    public void getRawApplicationStatsByMonths_Test() throws ParseException {
        var from = new Timestamp(FORMATTER.parse("2022-03-14 22:14:07").getTime());
        var to = new Timestamp(FORMATTER.parse("2022-04-14 23:39:07").getTime());
        var expected = statsRepository.getRawApplicationStatsByMonths(2, from, to);
        assertEquals(2, expected.size());
        assertEquals(3, expected.get("03.2022"));
        assertEquals(1, expected.get("04.2022"));
    }

    @Test
    public void getRawApplicationStatsByDays_Test() throws ParseException {
        var from = new Timestamp(FORMATTER.parse("2022-03-14 22:14:07").getTime());
        var to = new Timestamp(FORMATTER.parse("2022-04-14 23:39:07").getTime());
        var expected = statsRepository.getRawApplicationStatsByDays(2, from, to);
        assertEquals(3, expected.size());
        assertEquals(2, expected.get("14.03.2022"));
        assertEquals(1, expected.get("17.03.2022"));
        assertEquals(1, expected.get("14.04.2022"));
    }

    @Test
    public void getRawApplicationStatsByHours_Test() throws ParseException {
        var from = new Timestamp(FORMATTER.parse("2022-03-14 22:14:07").getTime());
        var to = new Timestamp(FORMATTER.parse("2022-04-14 23:39:07").getTime());
        var expected = statsRepository.getRawApplicationStatsByHours(2, from, to);
        assertEquals(4, expected.size());
        assertEquals(1, expected.get("22:00 14.03.2022"));
        assertEquals(1, expected.get("23:00 14.03.2022"));
        assertEquals(1, expected.get("23:00 17.03.2022"));
        assertEquals(1, expected.get("23:00 14.04.2022"));
    }
}
