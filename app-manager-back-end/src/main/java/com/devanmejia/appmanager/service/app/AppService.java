package com.devanmejia.appmanager.service.app;


import com.devanmejia.appmanager.transfer.app.AppRequestDTO;
import com.devanmejia.appmanager.transfer.app.AppResponseDTO;
import com.devanmejia.appmanager.transfer.criteria.PageCriteria;
import com.devanmejia.appmanager.transfer.criteria.sort.SortCriteria;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public interface AppService {

    AppResponseDTO findUserApp(long appId, long userId);

    List<AppResponseDTO> findUserApps(long userId, PageCriteria pageCriteria, SortCriteria sortCriteria);

    int getPageAmount(int pageSize, long userId);

    AppResponseDTO addUserApp(long userId, AppRequestDTO appDTO);

    AppResponseDTO updateUserApp(long appId, AppRequestDTO appDTO, long userId);

    void deleteUserApp(long appId, long userId);

    boolean isUserApp(long appId, long userId);
}
