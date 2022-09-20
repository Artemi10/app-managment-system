package com.devanmejia.appmanager.service.event.event_search;

import com.devanmejia.appmanager.entity.Event;
import com.devanmejia.appmanager.transfer.criteria.PageCriteria;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface EventSearchService {

    List<Event> findAppEvents(long appId, long userId, String searchParam, PageCriteria pageCriteria);

    int getAppEventsAmount(long appId, long userId, String searchParam);

}
