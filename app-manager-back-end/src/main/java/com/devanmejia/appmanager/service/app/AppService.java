package com.devanmejia.appmanager.service.app;


import com.devanmejia.appmanager.transfer.app.AppRequestDTO;
import com.devanmejia.appmanager.transfer.app.AppResponseDTO;
import com.devanmejia.appmanager.transfer.criteria.PageCriteria;
import com.devanmejia.appmanager.transfer.criteria.SortCriteria;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public interface AppService {
    AppResponseDTO findUserApp(long appId, String email);
    List<AppResponseDTO> findUserApps(String email, PageCriteria pageCriteria, SortCriteria sortCriteria);
    int getPageAmount(int pageSize, String email);
    AppResponseDTO addUserApp(long userId, AppRequestDTO appDTO);
    AppResponseDTO updateUserApp(long appId, AppRequestDTO appDTO, String email);
    void deleteUserApp(long appId, String email);
    boolean isUserApp(long appId, String email);
}
