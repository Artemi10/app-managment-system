package com.devanmejia.appmanager.service.stat;

import com.devanmejia.appmanager.exception.EntityException;
import com.devanmejia.appmanager.repository.stats.StatsRepository;
import com.devanmejia.appmanager.service.app.AppService;
import com.devanmejia.appmanager.transfer.stat.StatRequestDTO;
import com.devanmejia.appmanager.transfer.stat.StatResponseDTO;
import lombok.AllArgsConstructor;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@AllArgsConstructor
public class StatServiceImpl implements StatService {
    private final AppService appService;
    private final StatsRepository statsRepository;
    private final DateTimeFormatter dateTimeFormatter;
    private final Function<OffsetDateTime, OffsetDateTime> nextStatsDateGenerator;

    @Override
    public List<StatResponseDTO> createStats(long appId, StatRequestDTO statistics, long userId) {
        if (!appService.isUserApp(appId, userId)){
            throw new EntityException("Application not found");
        }
        var rawStat = statsRepository
                .getRawApplicationStats(appId, statistics.getFrom(), statistics.getTo());
        return createStatByRawStat(rawStat, statistics);
    }

    private List<StatResponseDTO> createStatByRawStat(
            Map<String, Integer> rawStat,
            StatRequestDTO statistics
    ) {
        var stat = new ArrayList<StatResponseDTO>();
        var startDate = statistics.getFrom();
        var endDate = statistics.getTo();
        while (!startDate.isAfter(endDate)) {
            var dateStr = dateTimeFormatter.format(startDate);
            stat.add(new StatResponseDTO(dateStr, rawStat.getOrDefault(dateStr, 0)));
            startDate = nextStatsDateGenerator.apply(startDate);
        }
        return stat;
    }
}
