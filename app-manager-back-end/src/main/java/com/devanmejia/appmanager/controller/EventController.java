package com.devanmejia.appmanager.controller;

import com.devanmejia.appmanager.configuration.security.details.UserPrincipal;
import com.devanmejia.appmanager.service.event.EventService;
import com.devanmejia.appmanager.transfer.criteria.PageCriteria;
import com.devanmejia.appmanager.transfer.event.EventRequestDTO;
import com.devanmejia.appmanager.transfer.event.EventResponseDTO;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/app")
public class EventController {
    private final EventService eventService;

    @PostMapping("/{appId}/event")
    @ApiOperation("Add new application event")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Ok"),
            @ApiResponse(code = 401, message = "User is not authenticated"),
            @ApiResponse(code = 403, message = "Access token is invalid"),
            @ApiResponse(code = 422, message = "Request body is invalid"),
            @ApiResponse(code = 404, message = "Application not found")
    })
    public EventResponseDTO addAppEvent(
            @PathVariable long appId,
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody @Valid EventRequestDTO request){
        return eventService.addAppEvent(appId, request, principal.id());
    }

    @GetMapping("/{appId}/events")
    @ApiOperation("Get application events")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Ok"),
            @ApiResponse(code = 401, message = "User is not authenticated"),
            @ApiResponse(code = 403, message = "Access token is invalid"),
            @ApiResponse(code = 422, message = "Query params are invalid")
    })
    public List<EventResponseDTO> getAppEvents(
            @PathVariable long appId,
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid PageCriteria pageCriteria) {
        return eventService.findAppEvents(appId, principal.id(), pageCriteria);
    }

    @DeleteMapping("/{appId}/event/{eventId}")
    @ApiOperation("Delete application event")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Ok"),
            @ApiResponse(code = 401, message = "User is not authenticated"),
            @ApiResponse(code = 403, message = "Access token is invalid"),
            @ApiResponse(code = 404, message = "Event not found")
    })
    public void deleteAppEvent(
            @PathVariable long appId,
            @PathVariable long eventId,
            @AuthenticationPrincipal UserPrincipal principal) {
        eventService.deleteAppEvent(eventId, appId, principal.id());
    }

    @PutMapping("/{appId}/event/{eventId}")
    @ApiOperation("Update application event")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Ok"),
            @ApiResponse(code = 401, message = "User is not authenticated"),
            @ApiResponse(code = 403, message = "Access token is invalid"),
            @ApiResponse(code = 404, message = "Event not found"),
            @ApiResponse(code = 422, message = "Query params are invalid")
    })
    public EventResponseDTO updateAppEvent(
            @PathVariable long appId,
            @PathVariable long eventId,
            @RequestBody @Valid EventRequestDTO request,
            @AuthenticationPrincipal UserPrincipal principal) {
        return eventService.updateAppEvent(appId, eventId, request, principal.id());
    }
}
