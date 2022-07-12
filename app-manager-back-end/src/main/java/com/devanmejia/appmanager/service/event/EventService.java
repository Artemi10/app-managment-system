package com.devanmejia.appmanager.service.event;


import com.devanmejia.appmanager.transfer.criteria.PageCriteria;
import com.devanmejia.appmanager.transfer.event.EventRequestDTO;
import com.devanmejia.appmanager.transfer.event.EventResponseDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface EventService {

    EventResponseDTO addAppEvent(long appId, EventRequestDTO requestDTO, long userId);

    List<EventResponseDTO> findAppEvents(long appId, long userId, PageCriteria pageCriteria);

    void deleteAppEvent(long eventId, long appId, long userId);

    EventResponseDTO updateAppEvent(long appId, long eventId, EventRequestDTO requestDTO, long userId);
}
