package com.devanmejia.appmanager.controller.app;

import com.devanmejia.appmanager.security.details.UserPrincipal;
import com.devanmejia.appmanager.service.app_search.AppSearchService;
import com.devanmejia.appmanager.transfer.app.AppResponseDTO;
import com.devanmejia.appmanager.transfer.criteria.PageCriteria;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/apps")
public class AppNameSearchController {
    private final AppSearchService appSearchService;

    @GetMapping("/name/{searchParam}")
    @ApiOperation("Full text search by app name")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Ok"),
            @ApiResponse(code = 403, message = "Access token is invalid"),
            @ApiResponse(code = 401, message = "User is unauthorized"),
            @ApiResponse(code = 422, message = "Request body is invalid")
    })
    public List<AppResponseDTO> findUserApps(
            @AuthenticationPrincipal UserPrincipal principal,
            @ApiParam(value = "Search application name word", required = true) @PathVariable String searchParam,
            @RequestHeader(value = "Time-Zone-Offset", defaultValue = "0") int timeZoneSecondsOffset,
            @Valid PageCriteria pageCriteria) {
        return appSearchService.findUserApps(principal.id(), searchParam, pageCriteria)
                .stream()
                .map(app -> AppResponseDTO.from(app, timeZoneSecondsOffset))
                .toList();
    }

    @GetMapping("/name/{searchParam}/count")
    @ApiOperation("Get filtered page amount")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Ok"),
            @ApiResponse(code = 401, message = "User is unauthorized"),
            @ApiResponse(code = 403, message = "Access token is invalid")
    })
    public int getPageAmount(
            @AuthenticationPrincipal UserPrincipal principal,
            @ApiParam(value = "Search application name word", required = true) @PathVariable String searchParam,
            @RequestParam(defaultValue = "1") int pageSize) {
        return appSearchService.getPageAmount(principal.id(), pageSize, searchParam);
    }
}
