package com.devanmejia.appmanager.security.token;

import com.devanmejia.appmanager.entity.user.Authority;
import com.devanmejia.appmanager.service.time.TimeServiceImpl;
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

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
class JwtServiceTest {
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
    private final String secretKey;
    private final Long validTimePeriod;
    private final JwtService jwtService;
    private final TimeServiceImpl timeService;

    @Autowired
    public JwtServiceTest() {
        this.secretKey = "devanmejia2003";
        this.validTimePeriod = 720L;
        var clock = Clock.fixed(NOW.toInstant(), NOW.getZone());
        this.timeService = new TimeServiceImpl(clock);
        this.jwtService = new JwtService(timeService);
    }

    @BeforeEach
    public void initFields() {
        jwtService.setSecretKey(secretKey);
        jwtService.setValidTimePeriod(validTimePeriod);
    }

    @Test
    public void non_expired_AccessToken_Test() {
        var token = jwtService.createAccessToken("lyah.artem10@mail.ru", Authority.ACTIVE);
        assertEquals("lyah.artem10@mail.ru", jwtService.getEmail(token));
        assertEquals(Authority.ACTIVE.name(), jwtService.getAuthorityName(token));
        assertTrue(jwtService.isValid(token));
    }

    @Test
    public void expired_AccessToken_When_Current_time_equals_Expiration_time_Test() {
        var token = jwtService.createAccessToken("lyah.artem10@mail.ru", Authority.ACTIVE);
        var now = NOW.plusSeconds(validTimePeriod);
        this.timeService.setClock(Clock.fixed(now.toInstant(), now.getZone()));
        assertEquals("lyah.artem10@mail.ru", jwtService.getEmail(token));
        assertEquals(Authority.ACTIVE.name(), jwtService.getAuthorityName(token));
        assertFalse(jwtService.isValid(token));
    }

    @Test
    public void expired_AccessToken_When_Current_time_isAfter_Expiration_time_Test() {
        var token = jwtService.createAccessToken("lyah.artem10@mail.ru", Authority.ACTIVE);
        var now = NOW.plusSeconds(validTimePeriod + 1);
        this.timeService.setClock(Clock.fixed(now.toInstant(), now.getZone()));
        assertEquals("lyah.artem10@mail.ru", jwtService.getEmail(token));
        assertEquals(Authority.ACTIVE.name(), jwtService.getAuthorityName(token));
        assertFalse(jwtService.isValid(token));
    }
}
