package com.devanmejia.appmanager.controller;

import com.devanmejia.appmanager.configuration.security.details.UserPrincipal;
import com.devanmejia.appmanager.service.event.EventService;
import com.devanmejia.appmanager.transfer.event.EventRequestDTO;
import com.devanmejia.appmanager.transfer.event.EventResponseDTO;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/app")
public class EventController {
    private final EventService eventService;

    @PostMapping("/{appId}/event")
    public EventResponseDTO addAppEvent(
            @PathVariable long appId,
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody @Valid EventRequestDTO request){
        return eventService.addEvent(appId, request, principal.email());
    }
}
