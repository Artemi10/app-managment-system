package com.devanmejia.appmanager.controller.event;

import com.devanmejia.appmanager.security.details.UserPrincipal;
import com.devanmejia.appmanager.service.event.event_search.EventSearchService;
import com.devanmejia.appmanager.transfer.criteria.PageCriteria;
import com.devanmejia.appmanager.transfer.event.EventResponseDTO;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/app")
public class EventNameSearchController {
    private final EventSearchService eventSearchService;

    @GetMapping("/{appId}/events/name/{searchParam}")
    @ApiOperation("Full text search by event name and event extra information")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Ok"),
            @ApiResponse(code = 403, message = "Access token is invalid"),
            @ApiResponse(code = 401, message = "User is unauthorized"),
            @ApiResponse(code = 422, message = "Request body is invalid")
    })
    public List<EventResponseDTO> findAppEvents(
            @AuthenticationPrincipal UserPrincipal principal,
            @ApiParam(value = "Application id", required = true) @PathVariable long appId,
            @ApiParam(value = "Search application name word", required = true) @PathVariable String searchParam,
            @RequestHeader(value = "Time-Zone-Offset", defaultValue = "0") int timeZoneSecondsOffset,
            @Valid PageCriteria pageCriteria,
            HttpServletResponse response
    ) {
        var userId = principal.id();
        var eventsAmount = eventSearchService.getAppEventsAmount(appId, userId, searchParam);
        response.addHeader("X-Total-Count", String.valueOf(eventsAmount));
        return eventSearchService.findAppEvents(appId, userId, searchParam, pageCriteria)
                .stream()
                .map(event -> EventResponseDTO.from(appId, event, timeZoneSecondsOffset))
                .toList();
    }
}
