package com.devanmejia.appmanager.service.app_search;

import com.devanmejia.appmanager.repository.app.AppNameSearchRepository;
import com.devanmejia.appmanager.transfer.app.AppResponseDTO;
import com.devanmejia.appmanager.transfer.criteria.PageCriteria;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class AppSearchServiceImpl implements AppSearchService{
    private final AppNameSearchRepository appNameSearchRepository;

    @Override
    public List<AppResponseDTO> findUserApps(String searchParam, String email, PageCriteria pageCriteria) {
        var pageable = PageRequest.of(pageCriteria.getPage() - 1, pageCriteria.getPageSize());
        return appNameSearchRepository
                .findAllByUserEmail(email, searchParam + ":*", pageable)
                .stream()
                .map(AppResponseDTO::new)
                .toList();
    }

    @Override
    public int getPageAmount(int pageSize, String searchParam, String email) {
        var noteAmount = appNameSearchRepository.getUserAppsAmountByName(email, searchParam + ":*");
        if (noteAmount > 0 && noteAmount % pageSize == 0) {
            return noteAmount / pageSize;
        }
        else {
            return noteAmount / pageSize + 1;
        }
    }
}
