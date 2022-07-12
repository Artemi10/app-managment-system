package com.devanmejia.appmanager.service.app_search;

import com.devanmejia.appmanager.transfer.app.AppResponseDTO;
import com.devanmejia.appmanager.transfer.criteria.PageCriteria;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AppSearchService {

    List<AppResponseDTO> findUserApps(long userId, String searchParam, PageCriteria pageCriteria);

    int getPageAmount(long userId, int pageSize, String searchParam);
}
