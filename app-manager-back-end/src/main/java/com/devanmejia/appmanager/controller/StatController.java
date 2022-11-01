package com.devanmejia.appmanager.controller;

import com.devanmejia.appmanager.security.details.UserPrincipal;
import com.devanmejia.appmanager.service.stat.StatService;
import com.devanmejia.appmanager.transfer.stat.StatRequestDTO;
import com.devanmejia.appmanager.transfer.stat.StatResponseDTO;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/app")
public class StatController {
    private final Map<String, StatService> statServices;

    @PostMapping("/{appId}/stat")
    @ApiOperation("Generate app event stats")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Ok"),
            @ApiResponse(code = 401, message = "User is not authorized"),
            @ApiResponse(code = 403, message = "Access token is invalid"),
            @ApiResponse(code = 404, message = "Application not found")
    })
    public List<StatResponseDTO> createStats(
            @PathVariable long appId,
            @Valid @RequestBody StatRequestDTO statsRequest,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        var serviceName = statsRequest.getType().getStatServiceName();
        return statServices.containsKey(serviceName)
                ? statServices.get(serviceName).createStats(appId, statsRequest, principal.id())
                : Collections.emptyList();
    }
}
