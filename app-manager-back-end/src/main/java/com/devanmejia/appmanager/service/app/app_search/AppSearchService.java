package com.devanmejia.appmanager.service.app.app_search;

import com.devanmejia.appmanager.entity.App;
import com.devanmejia.appmanager.transfer.criteria.PageCriteria;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AppSearchService {

    List<App> findUserApps(long userId, String searchParam, PageCriteria pageCriteria);

    int getUserAppsAmount(long userId, String searchParam);
}
