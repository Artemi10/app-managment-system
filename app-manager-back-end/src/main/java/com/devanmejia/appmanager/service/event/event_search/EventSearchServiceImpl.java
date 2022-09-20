package com.devanmejia.appmanager.service.event.event_search;

import com.devanmejia.appmanager.entity.Event;
import com.devanmejia.appmanager.repository.event.EventNameSearchRepository;
import com.devanmejia.appmanager.transfer.criteria.PageCriteria;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class EventSearchServiceImpl implements EventSearchService {
    private final EventNameSearchRepository eventNameSearchRepository;

    @Override
    public List<Event> findAppEvents(long appId, long userId, String searchParam, PageCriteria pageCriteria) {
        var name = searchParam + ":*";
        return eventNameSearchRepository
                .findAppEventsByName(appId, userId, name, pageCriteria.getLimit(), pageCriteria.getOffset());
    }

    @Override
    public int getAppEventsAmount(long appId, long userId, String searchParam) {
        var name = searchParam + ":*";
        return eventNameSearchRepository.getAppEventsAmountByName(appId, userId, name);
    }
}
