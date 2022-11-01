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

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class MonthStatServiceTest {
    private static DateTimeFormatter FORMATTER;
    private final AppService appService;
    private final StatsRepository statsRepository;
    private final StatService monthStatService;

    @Autowired
    public MonthStatServiceTest() {
        this.appService = spy(AppService.class);
        this.statsRepository = spy(StatsRepository.class);
        this.monthStatService = new StatServiceImpl(
                appService,
                statsRepository,
                DateTimeFormatter.ofPattern("MM.yyyy"),
                date -> date.plusMonths(1)
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
                argThat(time -> FORMATTER.format(time).equals("00:00 01.04.2022 +0300")),
                argThat(time -> FORMATTER.format(time).equals("00:00 01.07.2022 +0300")))
        ).thenReturn(Map.of(
                "04.2022", 1,
                "07.2022", 8));
        when(appService.isUserApp(2L, 1))
                .thenReturn(true);
        when(appService.isUserApp(2L, 4))
                .thenReturn(false);
    }

    @Test
    public void createStats_Test() {
        var from = OffsetDateTime.parse("00:00 01.04.2022 +0300", FORMATTER);
        var to = OffsetDateTime.parse("00:00 01.07.2022 +0300", FORMATTER);
        var statistic = new StatRequestDTO(from, to);
        var expected = monthStatService.createStats(2, statistic, 1);
        assertEquals(4, expected.size());
        assertEquals("04.2022", expected.get(0).date());
        assertEquals(1, expected.get(0).amount());
        assertEquals("05.2022", expected.get(1).date());
        assertEquals(0, expected.get(1).amount());
        assertEquals("06.2022", expected.get(2).date());
        assertEquals(0, expected.get(2).amount());
        assertEquals("07.2022", expected.get(3).date());
        assertEquals(8, expected.get(3).amount());
    }

    @Test
    public void throw_Exception_When_CreateStats_If_User_Does_Not_Have_App_Test() {
        var from = OffsetDateTime.parse("00:00 01.04.2022 +0300", FORMATTER);
        var to = OffsetDateTime.parse("00:00 01.07.2022 +0300", FORMATTER);
        var statistic = new StatRequestDTO(from, to);
        var exception = assertThrows(
                EntityException.class,
                () -> monthStatService.createStats(2, statistic, 4)
        );
        assertEquals("Application not found", exception.getMessage());
    }
}
