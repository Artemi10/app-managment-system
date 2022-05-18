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
@Service("days")
public class DayStatService implements StatService {
    private final AppService appService;
    private final StatsRepository statsRepository;

    @Override
    public List<StatResponseDTO> createStats(long appId, String email) {
        var to = new Timestamp(new Date().getTime());
        var calendar = new GregorianCalendar();
        calendar.setTime(to);
        calendar.add(Calendar.MONTH, -1);
        var statistics = new StatRequestDTO(email, new Timestamp(calendar.getTime().getTime()), to);
        return createStats(appId, statistics);
    }

    @Override
    public List<StatResponseDTO> createStats(long appId, StatRequestDTO statistics) {
        if (!appService.isUserApp(appId, statistics.email())){
            throw new EntityException("Application not found");
        }
        var rawStat = statsRepository
                .getRawApplicationStatsByDays(appId, statistics.from(), statistics.to());
        return createDayStatByRawStat(rawStat, statistics.from(), statistics.to());
    }

    private List<StatResponseDTO> createDayStatByRawStat(Map<String, Integer> rawStat, Timestamp from, Timestamp to){
        var stat = new ArrayList<StatResponseDTO>();
        var formatter = new SimpleDateFormat("dd.MM.yyyy");

        var startDate = getCalendarForDayPattern(from);
        var endDate = getCalendarForDayPattern(to);
        while (startDate.before(endDate)) {
            var dateStr = formatter.format(startDate.getTime());
            stat.add(new StatResponseDTO(dateStr, rawStat.getOrDefault(dateStr, 0)));
            startDate.add(Calendar.DAY_OF_MONTH, 1);
        }

        var dateStr = formatter.format(startDate.getTime());
        stat.add(new StatResponseDTO(dateStr, rawStat.getOrDefault(dateStr, 0)));
        startDate.add(Calendar.DAY_OF_MONTH, 1);
        return stat;
    }

    private Calendar getCalendarForDayPattern(Timestamp date) {
        var calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        return calendar;
    }
}
