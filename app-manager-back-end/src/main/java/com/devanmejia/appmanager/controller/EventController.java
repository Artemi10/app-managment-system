package com.devanmejia.appmanager.controller;

import com.devanmejia.appmanager.configuration.security.details.UserPrincipal;
import com.devanmejia.appmanager.service.event.EventService;
import com.devanmejia.appmanager.transfer.event.EventRequestDTO;
import com.devanmejia.appmanager.transfer.event.EventResponseDTO;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
    @ApiOperation("Add new application event")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Ok"),
            @ApiResponse(code = 403, message = "Access token is invalid"),
            @ApiResponse(code = 422, message = "Request body is invalid"),
            @ApiResponse(code = 404, message = "Application not found")
    })
    public EventResponseDTO addAppEvent(
            @PathVariable long appId,
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody @Valid EventRequestDTO request){
        return eventService.addEvent(appId, request, principal.id());
    }
}
