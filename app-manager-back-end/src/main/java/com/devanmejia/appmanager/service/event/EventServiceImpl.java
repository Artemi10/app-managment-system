package com.devanmejia.appmanager.service.event;


import com.devanmejia.appmanager.entity.App;
import com.devanmejia.appmanager.entity.Event;
import com.devanmejia.appmanager.exception.EntityException;
import com.devanmejia.appmanager.repository.EventRepository;
import com.devanmejia.appmanager.service.app.AppService;
import com.devanmejia.appmanager.transfer.event.EventRequestDTO;
import com.devanmejia.appmanager.transfer.event.EventResponseDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor
public class EventServiceImpl implements EventService {
    private final AppService appService;
    private final EventRepository eventRepository;

    @Override
    public EventResponseDTO addEvent(long appId, EventRequestDTO requestDTO, String email) {
        if (!appService.isUserApp(appId, email)){
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
    public List<EventResponseDTO> findAppEvents(long appId, String email) {
        return eventRepository.findEventsByApp(appId, email)
                .stream()
                .map(event -> new EventResponseDTO(appId, event))
                .toList();
    }
}
