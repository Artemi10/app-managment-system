package com.devanmejia.appmanager.service.event;


import com.devanmejia.appmanager.entity.App;
import com.devanmejia.appmanager.entity.Event;
import com.devanmejia.appmanager.exception.EntityException;
import com.devanmejia.appmanager.repository.EventRepository;
import com.devanmejia.appmanager.service.app.AppService;
import com.devanmejia.appmanager.transfer.criteria.PageCriteria;
import com.devanmejia.appmanager.transfer.event.EventRequestDTO;
import com.devanmejia.appmanager.transfer.event.EventResponseDTO;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor
public class EventServiceImpl implements EventService {
    private final AppService appService;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public EventResponseDTO addAppEvent(long appId, EventRequestDTO requestDTO, long userId) {
        if (!appService.isUserApp(appId, userId)){
            throw new EntityException("Application not found");
        }
        var time = new Timestamp(new Date().getTime());
        var app = App.builder()
                .id(appId)
                .build();
        var event = Event.builder()
                .name(requestDTO.name())
                .extraInformation(requestDTO.extraInformation())
                .time(time)
                .app(app)
                .build();
        var savedEvent = eventRepository.save(event);
        return new EventResponseDTO(appId, savedEvent);
    }

    @Override
    public List<EventResponseDTO> findAppEvents(long appId, long userId, PageCriteria pageCriteria) {
        var pageable = pageCriteria.toPageable();
        return eventRepository.findEventsByApp(appId, userId, pageable)
                .stream()
                .map(event -> new EventResponseDTO(appId, event))
                .toList();
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
    public EventResponseDTO updateAppEvent(long appId, long eventId, EventRequestDTO requestDTO, long userId) {
        var event = eventRepository.findEvent(eventId, appId, userId)
                .orElseThrow(() -> new EntityException("Event not found"));
        event.setName(requestDTO.name());
        event.setExtraInformation(requestDTO.extraInformation());
        var updatedEvent = eventRepository.save(event);
        return new EventResponseDTO(appId, updatedEvent);
    }
}
