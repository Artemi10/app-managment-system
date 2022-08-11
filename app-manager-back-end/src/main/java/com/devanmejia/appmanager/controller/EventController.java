package com.devanmejia.appmanager.controller;

import com.devanmejia.appmanager.security.details.UserPrincipal;
import com.devanmejia.appmanager.service.event.EventService;
import com.devanmejia.appmanager.service.time.TimeService;
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
    private final TimeService timeService;

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
            @RequestHeader(value = "Time-Zone-Offset", defaultValue = "0") int timeZoneSecondsOffset,
            @RequestBody @Valid EventRequestDTO request){
        var currentTime = timeService.now(timeZoneSecondsOffset);
        var event = eventService.addAppEvent(appId, request, principal.id(), currentTime);
        return EventResponseDTO.from(appId, event, timeZoneSecondsOffset);
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
            @RequestHeader(value = "Time-Zone-Offset", defaultValue = "0") int timeZoneSecondsOffset,
            @Valid PageCriteria pageCriteria) {
        return eventService.findAppEvents(appId, principal.id(), pageCriteria)
                .stream()
                .map(event -> EventResponseDTO.from(appId, event, timeZoneSecondsOffset))
                .toList();
    }

    @GetMapping("/{appId}/events/count")
    @ApiOperation("Get all page amount")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Ok", response = Integer.class),
            @ApiResponse(code = 401, message = "User is not authorized"),
            @ApiResponse(code = 403, message = "Access token is invalid")
    })
    public int getAppEventsPageAmount(
            @PathVariable long appId,
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(defaultValue = "1") int pageSize) {
        return eventService.getPageAmount(appId, pageSize, principal.id());
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
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestHeader(value = "Time-Zone-Offset", defaultValue = "0") int timeZoneSecondsOffset,
            @RequestBody @Valid EventRequestDTO request) {
        var event = eventService.updateAppEvent(appId, eventId, request, principal.id());
        return EventResponseDTO.from(appId, event, timeZoneSecondsOffset);
    }
}
