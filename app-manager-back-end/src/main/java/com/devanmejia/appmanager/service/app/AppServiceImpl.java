package com.devanmejia.appmanager.service.app;

import com.devanmejia.appmanager.entity.App;
import com.devanmejia.appmanager.entity.user.User;
import com.devanmejia.appmanager.exception.EntityException;
import com.devanmejia.appmanager.repository.app.AppRepository;
import com.devanmejia.appmanager.transfer.app.AppRequestDTO;
import com.devanmejia.appmanager.transfer.criteria.PageCriteria;
import com.devanmejia.appmanager.transfer.criteria.SortCriteria;
import lombok.AllArgsConstructor;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class AppServiceImpl implements AppService {
    private final AppRepository appRepository;

    @Override
    public App findUserApp(long appId, long userId) {
        return appRepository.findUserAppById(appId, userId)
                .orElseThrow(() -> new EntityException("Application not found"));
    }

    @Override
    public List<App> findUserApps(long userId, PageCriteria pageCriteria, SortCriteria sortCriteria) {
        var sort = sortCriteria.toSort();
        var pageable = pageCriteria.toPageable(sort);
        try {
            return appRepository.findAllByUserId(userId, pageable)
                    .stream()
                    .toList();
        } catch (InvalidDataAccessApiUsageException exception) {
            throw new EntityException("Sorting param is invalid");
        }
    }

    @Override
    public int getAppsAmount(long userId) {
        return appRepository.getUserAppsAmount(userId);
    }

    @Override
    public App addUserApp(long userId, AppRequestDTO appDTO, OffsetDateTime creationTime) {
        var user = User.builder()
                .id(userId)
                .build();
        var app = App.builder()
                .name(appDTO.name())
                .creationTime(creationTime)
                .user(user)
                .events(new ArrayList<>())
                .build();
        return appRepository.save(app);
    }

    @Override
    @Transactional
    public App updateUserApp(long appId, AppRequestDTO appDTO, long userId) {
        var app = appRepository.findUserAppById(appId, userId)
                .orElseThrow(() -> new EntityException("Application not found"));
        app.setName(appDTO.name());
        return appRepository.save(app);
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
