package com.devanmejia.appmanager.service.stat;

import com.devanmejia.appmanager.exception.EntityException;
import com.devanmejia.appmanager.repository.stats.StatsRepository;
import com.devanmejia.appmanager.service.app.AppService;
import com.devanmejia.appmanager.transfer.stat.StatRequestDTO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class HourStatServiceTest {
    private static DateTimeFormatter FORMATTER;
    private final AppService appService;
    private final StatsRepository statsRepository;
    private final StatService hourStatService;

    @Autowired
    public HourStatServiceTest() {
        this.appService = spy(AppService.class);
        this.statsRepository = spy(StatsRepository.class);
        this.hourStatService = new StatServiceImpl(
                appService,
                statsRepository,
                DateTimeFormatter.ofPattern("HH:00 dd.MM.yyyy Z"),
                date -> date.plusHours(1)
        );
    }

    @BeforeAll
    public static void init() {
        FORMATTER = DateTimeFormatter.ofPattern("HH:00 dd.MM.yyyy Z");
    }

    @BeforeEach
    public void initMock() {
        when(statsRepository.getRawApplicationStats(
                eq(2L),
                argThat(time -> FORMATTER.format(time).equals("12:00 07.04.2022 +0300")),
                argThat(time -> FORMATTER.format(time).equals("15:00 07.04.2022 +0300")))
        ).thenReturn(
                Map.of(
                        "12:00 07.04.2022 +0300", 1,
                        "15:00 07.04.2022 +0300", 8
                ));
        when(appService.isUserApp(2L, 1))
                .thenReturn(true);
        when(appService.isUserApp(2L, 4))
                .thenReturn(false);
    }

    @Test
    public void createStats_Test() {
        var from = OffsetDateTime.parse("12:00 07.04.2022 +0300", FORMATTER);
        var to = OffsetDateTime.parse("15:00 07.04.2022 +0300", FORMATTER);
        var statistic = new StatRequestDTO(from, to);
        var expected = hourStatService.createStats(2, statistic, 1);
        assertEquals(4, expected.size());
        assertEquals("12:00 07.04.2022 +0300", expected.get(0).date());
        assertEquals(1, expected.get(0).amount());
        assertEquals("13:00 07.04.2022 +0300", expected.get(1).date());
        assertEquals(0, expected.get(1).amount());
        assertEquals("14:00 07.04.2022 +0300", expected.get(2).date());
        assertEquals(0, expected.get(2).amount());
        assertEquals("15:00 07.04.2022 +0300", expected.get(3).date());
        assertEquals(8, expected.get(3).amount());
        verify(statsRepository, times(1))
                .getRawApplicationStats(eq(2L), eq(from), eq(to));
    }

    @Test
    public void throw_Exception_When_CreateStats_If_User_Does_Not_Have_App_Test() {
        var from = OffsetDateTime.parse("12:00 07.04.2022 +0300", FORMATTER);
        var to = OffsetDateTime.parse("15:00 07.04.2022 +0300", FORMATTER);
        var statistic = new StatRequestDTO(from, to);
        var exception = assertThrows(
                EntityException.class,
                () -> hourStatService.createStats(2, statistic, 4)
        );
        assertEquals("Application not found", exception.getMessage());
    }
}
