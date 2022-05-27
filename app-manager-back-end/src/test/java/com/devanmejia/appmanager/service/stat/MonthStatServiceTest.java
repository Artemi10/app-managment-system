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

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class MonthStatServiceTest {
    private static SimpleDateFormat FORMATTER;
    private final AppService appService;
    private final StatsRepository statsRepository;
    private final StatService monthStatService;

    @Autowired
    public MonthStatServiceTest() {
        this.appService = spy(AppService.class);
        this.statsRepository = spy(StatsRepository.class);
        this.monthStatService = new MonthStatService(appService, statsRepository);
    }

    @BeforeAll
    public static void init() {
        FORMATTER = new SimpleDateFormat("MM.yyyy");
    }

    @BeforeEach
    public void initMock() {
        when(statsRepository.getRawApplicationStatsByMonths(
                eq(2L),
                argThat(time -> FORMATTER.format(time).equals("04.2022")),
                argThat(time -> FORMATTER.format(time).equals("07.2022")))
        ).thenReturn(Map.of(
                "04.2022", 1,
                "07.2022", 8));
        when(appService.isUserApp(2L, 1))
                .thenReturn(true);
        when(appService.isUserApp(2L, 4))
                .thenReturn(false);
    }

    @Test
    public void createStats_Test() throws ParseException {
        var from = new Timestamp(FORMATTER.parse("04.2022").getTime());
        var to = new Timestamp(FORMATTER.parse("07.2022").getTime());
        var statistic = new StatRequestDTO(1, from, to);
        var expected = monthStatService.createStats(2, statistic);
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
    public void create_Basic_Stats_Test() {
        var expected = monthStatService.createStats(2, 1);
        verify(statsRepository, times(1))
                .getRawApplicationStatsByMonths(eq(2L), any(), any());
    }

    @Test
    public void throw_Exception_When_CreateStats_If_User_Does_Not_Have_App_Test() throws ParseException {
        var from = new Timestamp(FORMATTER.parse("04.2022").getTime());
        var to = new Timestamp(FORMATTER.parse("07.2022").getTime());
        var statistic = new StatRequestDTO(4, from, to);
        var exception = assertThrows(EntityException.class, () -> monthStatService.createStats(2, statistic));
        assertEquals("Application not found", exception.getMessage());
    }
}
