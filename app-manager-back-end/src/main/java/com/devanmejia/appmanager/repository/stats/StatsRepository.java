package com.devanmejia.appmanager.repository.stats;

import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Map;

@Repository
public interface StatsRepository {
    Map<String, Integer> getRawApplicationStatsByMonths(long appId, Timestamp from, Timestamp to);
    Map<String, Integer> getRawApplicationStatsByDays(long appId, Timestamp from, Timestamp to);
    Map<String, Integer> getRawApplicationStatsByHours(long appId, Timestamp from, Timestamp to);
}
