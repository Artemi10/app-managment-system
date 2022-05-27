package com.devanmejia.appmanager.controller.app;

import com.devanmejia.appmanager.configuration.security.details.UserPrincipal;
import com.devanmejia.appmanager.service.app_search.AppSearchService;
import com.devanmejia.appmanager.transfer.app.AppResponseDTO;
import com.devanmejia.appmanager.transfer.criteria.PageCriteria;
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
    public List<AppResponseDTO> findUserApps(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String searchParam,
            @Valid PageCriteria pageCriteria) {
        return appSearchService.findUserApps(principal.id(), searchParam, pageCriteria);
    }

    @GetMapping("/name/{searchParam}/count")
    public int getPageAmount(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String searchParam,
            @RequestParam(defaultValue = "1") int pageSize) {
        return appSearchService.getPageAmount(principal.id(), pageSize, searchParam);
    }
}
