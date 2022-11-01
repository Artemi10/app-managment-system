package com.devanmejia.appmanager.repository.stats;

import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.Map;

@Repository
public interface StatsRepository {
    Map<String, Integer> getRawApplicationStats(long appId, OffsetDateTime from, OffsetDateTime to);
}
