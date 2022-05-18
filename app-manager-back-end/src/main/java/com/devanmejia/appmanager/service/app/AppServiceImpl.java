package com.devanmejia.appmanager.service.app;


import com.devanmejia.appmanager.entity.App;
import com.devanmejia.appmanager.entity.user.User;
import com.devanmejia.appmanager.exception.EntityException;
import com.devanmejia.appmanager.repository.AppRepository;
import com.devanmejia.appmanager.transfer.app.AppRequestDTO;
import com.devanmejia.appmanager.transfer.app.AppResponseDTO;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor
public class AppServiceImpl implements AppService {
    private final AppRepository appRepository;

    @Override
    public AppResponseDTO findUserApp(long appId, String email) {
        return appRepository.findUserAppById(appId, email)
                .map(AppResponseDTO::new)
                .orElseThrow(() -> new EntityException("Application not found"));
    }

    @Override
    public List<AppResponseDTO> findUserApps(int page, int pageSize, String email) {
        if (page <= 0) {
            throw new EntityException("Application not found");
        }
        var pageable = PageRequest.of(page - 1, pageSize);
        return appRepository
                .findAllByUserEmail(email, pageable).stream()
                .map(AppResponseDTO::new)
                .toList();
    }

    @Override
    public List<AppResponseDTO> findUserApps(int pageSize, String email) {
        return findUserApps(1, pageSize, email);
    }

    @Override
    public int getPageAmount(int pageSize, String email) {
        var noteAmount = appRepository.getUserAppsAmount(email);
        if (noteAmount > 0 && noteAmount % pageSize == 0) {
            return noteAmount / pageSize;
        }
        else {
            return noteAmount / pageSize + 1;
        }
    }

    @Override
    public AppResponseDTO addUserApp(long userId, AppRequestDTO appDTO) {
        var user = User.builder()
                .id(userId)
                .build();
        var currentTime = new Timestamp(new Date().getTime());
        var app = App.builder()
                .name(appDTO.name())
                .creationTime(currentTime)
                .user(user)
                .events(new ArrayList<>())
                .build();
        var savedApp = appRepository.save(app);
        return new AppResponseDTO(savedApp);
    }

    @Override
    public AppResponseDTO updateUserApp(long appId, AppRequestDTO appDTO, String email) {
        var app = appRepository.findUserAppById(appId, email)
                .orElseThrow(() -> new EntityException("Application not found"));
        app.setName(appDTO.name());
        var savedApp = appRepository.save(app);
        return new AppResponseDTO(savedApp);
    }

    @Override
    public void deleteUserApp(long appId, String email) {
        appRepository.deleteByIdAndUserEmail(appId, email);
    }

    @Override
    public boolean isUserApp(long appId, String email) {
        return appRepository.findUserAppById(appId, email).isPresent();
    }
}
