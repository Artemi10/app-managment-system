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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class DayStatServiceTest {
    private static SimpleDateFormat FORMATTER;
    private final AppService appService;
    private final StatsRepository statsRepository;
    private final StatService dayStatService;

    @Autowired
    public DayStatServiceTest() {
        this.appService = spy(AppService.class);
        this.statsRepository = spy(StatsRepository.class);
        this.dayStatService = new DayStatService(appService, statsRepository);
    }

    @BeforeAll
    public static void init() {
        FORMATTER = new SimpleDateFormat("dd.MM.yyyy");
    }

    @BeforeEach
    public void initMock() {
        when(statsRepository.getRawApplicationStatsByDays(
                eq(2L),
                argThat(time -> FORMATTER.format(time).equals("07.04.2022")),
                argThat(time -> FORMATTER.format(time).equals("09.04.2022")))
        ).thenReturn(Map.of(
                "07.04.2022", 1,
                "09.04.2022", 5));
        when(appService.isUserApp(2L, "lyah.artem10@mail.ru"))
                .thenReturn(true);
        when(appService.isUserApp(2L, "d@mail.ru"))
                .thenReturn(false);
    }

    @Test
    public void createStats_Test() throws ParseException {
        var from = new Timestamp(FORMATTER.parse("07.04.2022").getTime());
        var to = new Timestamp(FORMATTER.parse("09.04.2022").getTime());
        var statistic = new StatRequestDTO("lyah.artem10@mail.ru", from, to);
        var expected = dayStatService.createStats(2, statistic);
        assertEquals(3, expected.size());
        assertEquals("07.04.2022", expected.get(0).date());
        assertEquals(1, expected.get(0).amount());
        assertEquals("08.04.2022", expected.get(1).date());
        assertEquals(0, expected.get(1).amount());
        assertEquals("09.04.2022", expected.get(2).date());
        assertEquals(5, expected.get(2).amount());
    }

    @Test
    public void create_Basic_Stats_Test() {
        var expected = dayStatService.createStats(2, "lyah.artem10@mail.ru");
        verify(statsRepository, times(1))
                .getRawApplicationStatsByDays(eq(2L), any(), any());
    }

    @Test
    public void throw_Exception_When_CreateStats_If_User_Does_Not_Have_App_Test() throws ParseException {
        var from = new Timestamp(FORMATTER.parse("07.04.2022").getTime());
        var to = new Timestamp(FORMATTER.parse("09.04.2022").getTime());
        var statistic = new StatRequestDTO("d@mail.ru", from, to);
        var exception = assertThrows(EntityException.class, () -> dayStatService.createStats(2, statistic));
        assertEquals("Application not found", exception.getMessage());
    }
}
