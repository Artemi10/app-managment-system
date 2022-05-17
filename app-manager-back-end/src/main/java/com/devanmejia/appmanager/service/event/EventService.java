package com.devanmejia.appmanager.service.event;


import com.devanmejia.appmanager.transfer.event.EventRequestDTO;
import com.devanmejia.appmanager.transfer.event.EventResponseDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface EventService {
    EventResponseDTO addEvent(long appIp, EventRequestDTO requestDTO, String email);
    List<EventResponseDTO> findAppEvents(long appId, String email);
}
