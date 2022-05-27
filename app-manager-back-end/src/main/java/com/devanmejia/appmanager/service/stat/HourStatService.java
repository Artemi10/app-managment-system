package com.devanmejia.appmanager.service.stat;


import com.devanmejia.appmanager.exception.EntityException;
import com.devanmejia.appmanager.repository.stats.StatsRepository;
import com.devanmejia.appmanager.service.app.AppService;
import com.devanmejia.appmanager.transfer.stat.StatRequestDTO;
import com.devanmejia.appmanager.transfer.stat.StatResponseDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

@AllArgsConstructor
@Service("hours")
public class HourStatService implements StatService {
    private final AppService appService;
    private final StatsRepository statsRepository;

    @Override
    public List<StatResponseDTO> createStats(long appId, long userId) {
        var to = new Timestamp(new Date().getTime());
        var calendar = new GregorianCalendar();
        calendar.setTime(to);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        var statistics = new StatRequestDTO(userId, new Timestamp(calendar.getTime().getTime()), to);
        return createStats(appId, statistics);
    }

    @Override
    public List<StatResponseDTO> createStats(long appId, StatRequestDTO statistics) {
        if (!appService.isUserApp(appId, statistics.userId())){
            throw new EntityException("Application not found");
        }
        var rowResult =  statsRepository
                .getRawApplicationStatsByHours(appId, statistics.from(), statistics.to());
        return createHourStatByRawStat(rowResult, statistics.from(), statistics.to());
    }

    private List<StatResponseDTO> createHourStatByRawStat(Map<String, Integer> rawStat, Timestamp from, Timestamp to){
        var stat = new ArrayList<StatResponseDTO>();
        var formatter = new SimpleDateFormat("HH:00 dd.MM.yyyy");

        var startDate = getCalendarForHourPattern(from);
        var endDate = getCalendarForHourPattern(to);
        while (startDate.before(endDate)) {
            var dateStr = formatter.format(startDate.getTime());
            stat.add(new StatResponseDTO(dateStr, rawStat.getOrDefault(dateStr, 0)));
            startDate.add(Calendar.HOUR, 1);
        }

        var dateStr = formatter.format(startDate.getTime());
        stat.add(new StatResponseDTO(dateStr, rawStat.getOrDefault(dateStr, 0)));
        startDate.add(Calendar.HOUR, 1);
        return stat;
    }

    private Calendar getCalendarForHourPattern(Timestamp date) {
        var calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }
}
