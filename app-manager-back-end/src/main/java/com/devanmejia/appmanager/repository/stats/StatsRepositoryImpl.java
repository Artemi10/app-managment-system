package com.devanmejia.appmanager.repository.stats;

import com.devanmejia.appmanager.transfer.stat.StatResponseDTO;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
public class StatsRepositoryImpl implements StatsRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Map<String, Integer> getRawApplicationStatsByMonths(long appId, Timestamp from, Timestamp to) {
        var query = """
              SELECT to_char(creation_time, 'MM.YYYY') AS date, count(*) AS amount
              FROM events
              WHERE application_id = ?
              AND creation_time BETWEEN ? AND ?
              GROUP BY date""";
        return jdbcTemplate
                .query(query, new StatsRowMapper(), appId, from, to)
                .stream()
                .collect(Collectors.toMap(
                        StatResponseDTO::date,
                        StatResponseDTO::amount));
    }

    @Override
    public Map<String, Integer> getRawApplicationStatsByDays(long appId, Timestamp from, Timestamp to) {
        var query = """
              SELECT to_char(creation_time, 'DD.MM.YYYY') AS date, count(*) AS amount
              FROM events
              WHERE application_id = ?
              AND creation_time BETWEEN ? AND ?
              GROUP BY date""";
        return jdbcTemplate
                .query(query, new StatsRowMapper(), appId, from, to)
                .stream()
                .collect(Collectors.toMap(
                        StatResponseDTO::date,
                        StatResponseDTO::amount));
    }

    @Override
    public Map<String, Integer> getRawApplicationStatsByHours(long appId, Timestamp from, Timestamp to) {
        var query = """
              SELECT to_char(creation_time, 'HH24:00 DD.MM.YYYY') AS date, count(*) AS amount
              FROM events
              WHERE application_id = ?
              AND creation_time BETWEEN ? AND ?
              GROUP BY date""";
        return jdbcTemplate
                .query(query, new StatsRowMapper(), appId, from, to)
                .stream()
                .collect(Collectors.toMap(
                        StatResponseDTO::date,
                        StatResponseDTO::amount));
    }
}
