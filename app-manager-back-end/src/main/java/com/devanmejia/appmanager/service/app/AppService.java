package com.devanmejia.appmanager.service.app;


import com.devanmejia.appmanager.entity.App;
import com.devanmejia.appmanager.transfer.app.AppRequestDTO;
import com.devanmejia.appmanager.transfer.criteria.PageCriteria;
import com.devanmejia.appmanager.transfer.criteria.SortCriteria;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;


@Service
public interface AppService {

    App findUserApp(long appId, long userId);

    List<App> findUserApps(long userId, PageCriteria pageCriteria, SortCriteria sortCriteria);

    int getPageAmount(int pageSize, long userId);

    App addUserApp(long userId, AppRequestDTO appDTO, OffsetDateTime creationTime);

    App updateUserApp(long appId, AppRequestDTO appDTO, long userId);

    void deleteUserApp(long appId, long userId);

    boolean isUserApp(long appId, long userId);
}
