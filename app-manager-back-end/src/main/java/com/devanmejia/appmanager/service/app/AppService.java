package com.devanmejia.appmanager.service.app;


import com.devanmejia.appmanager.transfer.app.AppRequestDTO;
import com.devanmejia.appmanager.transfer.app.AppResponseDTO;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public interface AppService {
    AppResponseDTO findUserApp(long appId, String email);
    List<AppResponseDTO> findUserApps(int page, int pageSize, String email);
    List<AppResponseDTO> findUserApps(int pageSize, String email);
    int getPageAmount(int pageSize, String email);
    AppResponseDTO addUserApp(long userId, AppRequestDTO appDTO);
    AppResponseDTO updateUserApp(long appId, AppRequestDTO appDTO, String email);
    void deleteUserApp(long appId, String email);
    boolean isUserApp(long appId, String email);
}
