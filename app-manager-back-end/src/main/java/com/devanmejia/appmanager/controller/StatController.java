package com.devanmejia.appmanager.controller;

import com.devanmejia.appmanager.configuration.security.details.UserPrincipal;
import com.devanmejia.appmanager.exception.EntityException;
import com.devanmejia.appmanager.service.stat.StatService;
import com.devanmejia.appmanager.transfer.stat.StatRequestDTO;
import com.devanmejia.appmanager.transfer.stat.StatResponseDTO;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.Collections;
import java.util.List;
import java.util.Map;


@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/app")
public class StatController {
    private final Map<String, StatService> statServices;

    @GetMapping("/{appId}/stat")
    @ApiOperation("Generate app event stats")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Ok"),
            @ApiResponse(code = 401, message = "User is not authorized"),
            @ApiResponse(code = 403, message = "Access token is invalid"),
            @ApiResponse(code = 404, message = "Application not found")
    })
    public List<StatResponseDTO> createStats(
            @PathVariable long appId,
            @RequestParam String type,
            @RequestParam String from,
            @RequestParam String to,
            @AuthenticationPrincipal UserPrincipal principal) {

        List<StatResponseDTO> stats;
        if (!from.isBlank() && !to.isBlank()) {
            try {
                var requestDTO = StatRequestDTO.from(principal.id(), from, to);
                stats = statServices.containsKey(type) ?
                        statServices.get(type).createStats(appId, requestDTO) : Collections.emptyList();
            } catch (EntityException exception) {
                stats = Collections.emptyList();
            }
        }
        else {
            try {
                stats = statServices.containsKey(type) ?
                        statServices.get(type).createStats(appId, principal.id()) : Collections.emptyList();
            } catch (EntityException exception) {
                    stats = Collections.emptyList();
            }
        }
        return stats;
    }
}
