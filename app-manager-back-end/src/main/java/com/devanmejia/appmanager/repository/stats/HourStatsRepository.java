package com.devanmejia.appmanager.repository.stats;

import com.devanmejia.appmanager.transfer.stat.StatResponseDTO;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
@Repository("hourStatsRepository")
public class HourStatsRepository implements StatsRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Map<String, Integer> getRawApplicationStats(long appId, OffsetDateTime from, OffsetDateTime to) {
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
                        StatResponseDTO::amount)
                );
    }
}
