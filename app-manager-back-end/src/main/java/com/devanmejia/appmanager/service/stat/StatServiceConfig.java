package com.devanmejia.appmanager.service.stat;

import com.devanmejia.appmanager.repository.stats.StatsRepository;
import com.devanmejia.appmanager.service.app.AppService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.format.DateTimeFormatter;
import java.util.Map;

@Configuration
@AllArgsConstructor
public class StatServiceConfig {
    private final AppService appService;
    private final Map<String, StatsRepository> statsRepositoryMap;

    @Bean
    public StatService dayStatService() {
        return new StatServiceImpl(
                appService,
                statsRepositoryMap.get("dayStatsRepository"),
                DateTimeFormatter.ofPattern("dd.MM.yyyy"),
                date -> date.plusDays(1)
        );
    }

    @Bean
    public StatService hourStatService() {
        return new StatServiceImpl(
                appService,
                statsRepositoryMap.get("hourStatsRepository"),
                DateTimeFormatter.ofPattern("HH:00 dd.MM.yyyy Z"),
                date -> date.plusHours(1)
        );
    }

    @Bean
    public StatService monthStatService() {
        return new StatServiceImpl(
                appService,
                statsRepositoryMap.get("monthStatsRepository"),
                DateTimeFormatter.ofPattern("MM.yyyy"),
                date -> date.plusMonths(1)
        );
    }
}
