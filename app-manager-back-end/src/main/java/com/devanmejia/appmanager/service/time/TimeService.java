package com.devanmejia.appmanager.service.time;

import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
public interface TimeService {
    OffsetDateTime now(int secondsOffset);

    default OffsetDateTime now() {
        return now(0);
    }
}
