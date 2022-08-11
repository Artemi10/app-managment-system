package com.devanmejia.appmanager.service.time;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
class TimeServiceImplTest {
    private static final DateTimeFormatter DATE_FORMATTER
            = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:s");
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

    private final TimeService timeService;

    @Autowired
    public TimeServiceImplTest() {
        this.timeService = new TimeServiceImpl(Clock.fixed(NOW.toInstant(), NOW.getZone()));
    }

    @Test
    public void now_With_Positive_Offset_Test() {
        var actual = timeService.now(10800);
        assertEquals("10.08.2022 17:22:54", actual.format(DATE_FORMATTER));
        actual = timeService.now(14400);
        assertEquals("10.08.2022 18:22:54", actual.format(DATE_FORMATTER));
        actual = timeService.now(3600);
        assertEquals("10.08.2022 15:22:54", actual.format(DATE_FORMATTER));
    }

    @Test
    public void now_With_Negative_Offset_Test() {
        var actual = timeService.now(-10800);
        assertEquals("10.08.2022 11:22:54", actual.format(DATE_FORMATTER));
        actual = timeService.now(-14400);
        assertEquals("10.08.2022 10:22:54", actual.format(DATE_FORMATTER));
        actual = timeService.now(-3600);
        assertEquals("10.08.2022 13:22:54", actual.format(DATE_FORMATTER));
    }

    @Test
    public void now_Without_Offset_Test() {
        var actual = timeService.now(0);
        assertEquals("10.08.2022 14:22:54", actual.format(DATE_FORMATTER));
        actual = timeService.now();
        assertEquals("10.08.2022 14:22:54", actual.format(DATE_FORMATTER));
    }
}
