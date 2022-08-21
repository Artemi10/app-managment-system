package com.devanmejia.appmanager.controller.app;

import com.devanmejia.appmanager.security.details.UserPrincipal;
import com.devanmejia.appmanager.service.app.AppService;
import com.devanmejia.appmanager.service.time.TimeService;
import com.devanmejia.appmanager.transfer.app.AppRequestDTO;
import com.devanmejia.appmanager.transfer.app.AppResponseDTO;
import com.devanmejia.appmanager.transfer.criteria.PageCriteria;
import com.devanmejia.appmanager.transfer.criteria.SortCriteria;
import io.swagger.annotations.*;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/apps")
public class AppController {
    private final AppService appService;
    private final TimeService timeService;

    @GetMapping
    @ApiOperation("Get user app")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Ok", response = AppResponseDTO.class, responseContainer = "List"),
            @ApiResponse(code = 404, message = "Application not found"),
            @ApiResponse(code = 401, message = "User is not authorized"),
            @ApiResponse(code = 403, message = "Access token is invalid"),
            @ApiResponse(code = 422, message = "Request body is invalid")
    })
    public List<AppResponseDTO> findUserApps(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestHeader(value = "Time-Zone-Offset", defaultValue = "0") int timeZoneSecondsOffset,
            @Valid PageCriteria pageCriteria,
            @Valid SortCriteria sortCriteria,
            HttpServletResponse response) {
        var userId = principal.id();
        var appsAmount = appService.getAppsAmount(userId);
        response.addHeader("X-Total-Count", String.valueOf(appsAmount));
        return appService
                .findUserApps(userId, pageCriteria, sortCriteria)
                .stream()
                .map(app -> AppResponseDTO.from(app, timeZoneSecondsOffset))
                .toList();
    }

    @PostMapping
    @ApiOperation("Create new user app")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Ok", response = AppResponseDTO.class),
            @ApiResponse(code = 401, message = "User is not authorized"),
            @ApiResponse(code = 403, message = "Access token is invalid"),
            @ApiResponse(code = 422, message = "Request body is invalid")
    })
    public AppResponseDTO createUserApp(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestHeader(value = "Time-Zone-Offset", defaultValue = "0") int timeZoneSecondsOffset,
            @RequestBody @Valid AppRequestDTO requestBody) {
        var currentTime = timeService.now(timeZoneSecondsOffset);
        var app = appService.addUserApp(principal.id(), requestBody, currentTime);
        return AppResponseDTO.from(app, timeZoneSecondsOffset);
    }

    @PutMapping("/{appId}")
    @ApiOperation("Rename existed user app")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Ok", response = AppResponseDTO.class),
            @ApiResponse(code = 404, message = "Application not found"),
            @ApiResponse(code = 401, message = "User is not authorized"),
            @ApiResponse(code = 403, message = "Access token is invalid"),
            @ApiResponse(code = 422, message = "Request body is invalid")
    })
    public AppResponseDTO updateUserApp(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestHeader(value = "Time-Zone-Offset", defaultValue = "0") int timeZoneSecondsOffset,
            @ApiParam(value = "App id to update", required = true) @PathVariable long appId,
            @RequestBody @Valid AppRequestDTO requestBody) {
        var app = appService.updateUserApp(appId, requestBody, principal.id());
        return AppResponseDTO.from(app, timeZoneSecondsOffset);
    }

    @DeleteMapping("/{appId}")
    @ApiOperation("Delete existed user app")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Ok"),
            @ApiResponse(code = 401, message = "User is not authorized"),
            @ApiResponse(code = 403, message = "Access token is invalid")
    })
    public void deleteUserApp(
            @AuthenticationPrincipal UserPrincipal principal,
            @ApiParam(value = "App id to delete", required = true) @PathVariable long appId) {
        appService.deleteUserApp(appId, principal.id());
    }
}
