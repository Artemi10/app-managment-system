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
    public List<AppResponseDTO> findUserApps(long userId, String searchParam, PageCriteria pageCriteria) {
        var pageable = pageCriteria.toPageable();
        var name = searchParam + ":*";
        return appNameSearchRepository
                .findUserAppsByName(userId, name, pageable)
                .stream()
                .map(AppResponseDTO::from)
                .toList();
    }

    @Override
    public int getPageAmount(long userId, int pageSize, String searchParam) {
        var name = searchParam + ":*";
        var noteAmount = appNameSearchRepository.getUserAppsAmountByName(userId, name);
        if (noteAmount > 0 && noteAmount % pageSize == 0) {
            return noteAmount / pageSize;
        }
        else {
            return noteAmount / pageSize + 1;
        }
    }
}
