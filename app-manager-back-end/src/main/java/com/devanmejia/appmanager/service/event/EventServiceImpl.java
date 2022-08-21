package com.devanmejia.appmanager.service.event;


import com.devanmejia.appmanager.entity.App;
import com.devanmejia.appmanager.entity.Event;
import com.devanmejia.appmanager.exception.EntityException;
import com.devanmejia.appmanager.repository.EventRepository;
import com.devanmejia.appmanager.service.app.AppService;
import com.devanmejia.appmanager.transfer.criteria.PageCriteria;
import com.devanmejia.appmanager.transfer.event.EventRequestDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class EventServiceImpl implements EventService {
    private final AppService appService;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public Event addAppEvent(long appId, EventRequestDTO requestDTO, long userId, OffsetDateTime creationTime) {
        if (!appService.isUserApp(appId, userId)){
            throw new EntityException("Application not found");
        }
        var app = App.builder()
                .id(appId)
                .build();
        var event = Event.builder()
                .name(requestDTO.name())
                .extraInformation(requestDTO.extraInformation())
                .creationTime(creationTime)
                .app(app)
                .build();
        return eventRepository.save(event);
    }

    @Override
    public List<Event> findAppEvents(long appId, long userId, PageCriteria pageCriteria) {
        var pageable = pageCriteria.toPageable();
        return eventRepository.findEventsByApp(appId, userId, pageable)
                .stream()
                .toList();
    }

    @Override
    public int getEventsAmount(long appId, long userId) {
        return eventRepository.getAppEventsAmount(appId, userId);
    }

    @Override
    @Transactional
    public void deleteAppEvent(long eventId, long appId, long userId) {
        var event = eventRepository.findEvent(eventId, appId, userId)
                .orElseThrow(() -> new EntityException("Event not found"));
        eventRepository.delete(event);
    }

    @Override
    @Transactional
    public Event updateAppEvent(long appId, long eventId, EventRequestDTO requestDTO, long userId) {
        var event = eventRepository.findEvent(eventId, appId, userId)
                .orElseThrow(() -> new EntityException("Event not found"));
        event.setName(requestDTO.name());
        event.setExtraInformation(requestDTO.extraInformation());
        return eventRepository.save(event);
    }
}
