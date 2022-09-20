package com.devanmejia.appmanager.service.app.app_search;

import com.devanmejia.appmanager.entity.App;
import com.devanmejia.appmanager.repository.app.AppNameSearchRepository;
import com.devanmejia.appmanager.transfer.criteria.PageCriteria;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class AppSearchServiceImpl implements AppSearchService{
    private final AppNameSearchRepository appNameSearchRepository;

    @Override
    public List<App> findUserApps(long userId, String searchParam, PageCriteria pageCriteria) {
        var name = searchParam + ":*";
        return appNameSearchRepository
                .findUserAppsByName(userId, name, pageCriteria.getLimit(), pageCriteria.getOffset());
    }

    @Override
    public int getUserAppsAmount(long userId, String searchParam) {
        var name = searchParam + ":*";
        return appNameSearchRepository.getUserAppsAmountByName(userId, name);
    }
}
