package com.devanmejia.appmanager.controller.app;

import com.devanmejia.appmanager.configuration.security.details.UserPrincipal;
import com.devanmejia.appmanager.service.app.AppService;
import com.devanmejia.appmanager.transfer.app.AppRequestDTO;
import com.devanmejia.appmanager.transfer.app.AppResponseDTO;
import com.devanmejia.appmanager.transfer.criteria.PageCriteria;
import com.devanmejia.appmanager.transfer.criteria.SortCriteria;
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
    public List<AppResponseDTO> findUserApps(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid PageCriteria pageCriteria,
            @Valid SortCriteria sortCriteria) {
        return appService.findUserApps(principal.email(), pageCriteria, sortCriteria);
    }

    @GetMapping("/count")
    public int getPageAmount(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(defaultValue = "1") int pageSize) {
        return appService.getPageAmount(pageSize, principal.email());
    }

    @PostMapping
    public AppResponseDTO createUserApp(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody @Valid AppRequestDTO requestBody) {
        return appService.addUserApp(principal.id(), requestBody);
    }

    @PutMapping("/{appId}")
    public AppResponseDTO updateUserApp(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable long appId,
            @RequestBody @Valid AppRequestDTO requestBody) {
        return appService.updateUserApp(appId, requestBody, principal.email());
    }

    @DeleteMapping("/{appId}")
    public void deleteUserApp(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable long appId) {
        appService.deleteUserApp(appId, principal.email());
    }
}
