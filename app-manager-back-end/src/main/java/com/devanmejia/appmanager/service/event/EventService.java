package com.devanmejia.appmanager.service.event;


import com.devanmejia.appmanager.entity.Event;
import com.devanmejia.appmanager.transfer.criteria.PageCriteria;
import com.devanmejia.appmanager.transfer.event.EventRequestDTO;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public interface EventService {

    Event addAppEvent(long appId, EventRequestDTO requestDTO, long userId, OffsetDateTime creationTime);

    int getPageAmount(long appId, int pageSize, long userId);

    List<Event> findAppEvents(long appId, long userId, PageCriteria pageCriteria);

    void deleteAppEvent(long eventId, long appId, long userId);

    Event updateAppEvent(long appId, long eventId, EventRequestDTO requestDTO, long userId);
}
