package com.devanmejia.appmanager.service.app;

import com.devanmejia.appmanager.entity.App;
import com.devanmejia.appmanager.entity.user.User;
import com.devanmejia.appmanager.exception.EntityException;
import com.devanmejia.appmanager.repository.app.AppRepository;
import com.devanmejia.appmanager.transfer.app.AppRequestDTO;
import com.devanmejia.appmanager.transfer.app.AppResponseDTO;
import com.devanmejia.appmanager.transfer.criteria.PageCriteria;
import com.devanmejia.appmanager.transfer.criteria.SortCriteria;
import lombok.AllArgsConstructor;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor
public class AppServiceImpl implements AppService {
    private final AppRepository appRepository;

    @Override
    public AppResponseDTO findUserApp(long appId, long userId) {
        return appRepository.findUserAppById(appId, userId)
                .map(AppResponseDTO::from)
                .orElseThrow(() -> new EntityException("Application not found"));
    }

    @Override
    public List<AppResponseDTO> findUserApps(long userId, PageCriteria pageCriteria, SortCriteria sortCriteria) {
        var sort = sortCriteria.toSort();
        var pageable = pageCriteria.toPageable(sort);
        try {
            return appRepository.findAllByUserId(userId, pageable)
                    .stream()
                    .map(AppResponseDTO::from)
                    .toList();
        } catch (InvalidDataAccessApiUsageException exception) {
            throw new EntityException("Sorting param is invalid");
        }
    }

    @Override
    public int getPageAmount(int pageSize, long userId) {
        var appAmount = appRepository.getUserAppsAmount(userId);
        if (appAmount > 0 && appAmount % pageSize == 0) {
            return appAmount / pageSize;
        }
        else {
            return appAmount / pageSize + 1;
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
        return AppResponseDTO.from(savedApp);
    }

    @Override
    @Transactional
    public AppResponseDTO updateUserApp(long appId, AppRequestDTO appDTO, long userId) {
        var app = appRepository.findUserAppById(appId, userId)
                .orElseThrow(() -> new EntityException("Application not found"));
        app.setName(appDTO.name());
        var savedApp = appRepository.save(app);
        return AppResponseDTO.from(savedApp);
    }

    @Override
    @Transactional
    public void deleteUserApp(long appId, long userId) {
        var app = appRepository.findUserAppById(appId, userId)
                .orElseThrow(() -> new EntityException("Application not found"));
        appRepository.delete(app);
    }

    @Override
    public boolean isUserApp(long appId, long userId) {
        return appRepository.findUserAppById(appId, userId).isPresent();
    }
}
