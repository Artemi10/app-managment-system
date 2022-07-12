package com.devanmejia.appmanager.service.stat;


import com.devanmejia.appmanager.exception.EntityException;
import com.devanmejia.appmanager.repository.stats.StatsRepository;
import com.devanmejia.appmanager.service.app.AppService;
import com.devanmejia.appmanager.transfer.stat.StatRequestDTO;
import com.devanmejia.appmanager.transfer.stat.StatResponseDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

@AllArgsConstructor
@Service("months")
public class MonthStatService implements StatService {
    private final AppService appService;
    private final StatsRepository statsRepository;

    @Override
    public List<StatResponseDTO> createStats(long appId, long userId) {
        var to = new Timestamp(new Date().getTime());
        var calendar = new GregorianCalendar();
        calendar.setTime(to);
        calendar.add(Calendar.YEAR, -1);
        var statistics = new StatRequestDTO(userId, new Timestamp(calendar.getTime().getTime()), to);
        return createStats(appId, statistics);
    }

    @Override
    @Transactional
    public List<StatResponseDTO> createStats(long appId, StatRequestDTO statistics) {
        if (!appService.isUserApp(appId, statistics.userId())){
            throw new EntityException("Application not found");
        }
        var rawStat = statsRepository
                .getRawApplicationStatsByMonths(appId, statistics.from(), statistics.to());
        return createMonthStatByRawStat(rawStat, statistics.from(), statistics.to());
    }

    private List<StatResponseDTO> createMonthStatByRawStat(Map<String, Integer> rawStat, Timestamp from, Timestamp to){
        var stat = new ArrayList<StatResponseDTO>();
        var formatter = new SimpleDateFormat("MM.yyyy");

        var startDate = getCalendarForMonthPattern(from);
        var endDate = getCalendarForMonthPattern(to);
        while (startDate.before(endDate)) {
            var dateStr = formatter.format(startDate.getTime());
            stat.add(new StatResponseDTO(dateStr, rawStat.getOrDefault(dateStr, 0)));
            startDate.add(Calendar.MONTH, 1);
        }

        var dateStr = formatter.format(startDate.getTime());
        stat.add(new StatResponseDTO(dateStr, rawStat.getOrDefault(dateStr, 0)));
        startDate.add(Calendar.MONTH, 1);
        return stat;
    }

    private Calendar getCalendarForMonthPattern(Timestamp date) {
        var calendar = new GregorianCalendar();
        calendar.setTime(new Date(date.getTime()));
        return calendar;
    }
}
