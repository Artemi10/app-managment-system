package com.devanmejia.appmanager.controller.app;

import com.devanmejia.appmanager.configuration.security.details.UserPrincipal;
import com.devanmejia.appmanager.service.app.AppService;
import com.devanmejia.appmanager.transfer.app.AppRequestDTO;
import com.devanmejia.appmanager.transfer.app.AppResponseDTO;
import com.devanmejia.appmanager.transfer.criteria.PageCriteria;
import com.devanmejia.appmanager.transfer.criteria.sort.SortCriteria;
import io.swagger.annotations.*;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/apps")
public class AppController {
    private final AppService appService;

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
            @Valid PageCriteria pageCriteria,
            @Valid SortCriteria sortCriteria) {
        return appService.findUserApps(principal.id(), pageCriteria, sortCriteria);
    }

    @GetMapping("/count")
    @ApiOperation("Get all page amount")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Ok", response = Integer.class),
            @ApiResponse(code = 401, message = "User is not authorized"),
            @ApiResponse(code = 403, message = "Access token is invalid")
    })
    public int getPageAmount(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(defaultValue = "1") int pageSize) {
        return appService.getPageAmount(pageSize, principal.id());
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
            @RequestBody @Valid AppRequestDTO requestBody) {
        return appService.addUserApp(principal.id(), requestBody);
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
            @ApiParam(value = "App id to update", required = true) @PathVariable long appId,
            @RequestBody @Valid AppRequestDTO requestBody) {
        return appService.updateUserApp(appId, requestBody, principal.id());
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
