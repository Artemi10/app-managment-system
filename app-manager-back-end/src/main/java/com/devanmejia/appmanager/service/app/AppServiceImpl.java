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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
                .map(AppResponseDTO::new)
                .orElseThrow(() -> new EntityException("Application not found"));
    }

    @Override
    public List<AppResponseDTO> findUserApps(long userId, PageCriteria pageCriteria, SortCriteria sortCriteria) {
        var sort = sortCriteria.isDescending() ?
                Sort.by(sortCriteria.getValue()).descending() : Sort.by(sortCriteria.getValue()).ascending();
        var pageable = PageRequest.of(pageCriteria.getPage() - 1, pageCriteria.getPageSize(), sort);
        try {
            return appRepository.findAllByUserId(userId, pageable)
                    .stream()
                    .map(AppResponseDTO::new)
                    .toList();
        } catch (InvalidDataAccessApiUsageException exception) {
            var message = "Sorting param is invalid. Field %s does not exist.".formatted(sortCriteria.getValue());
            throw new EntityException(message);
        }
    }

    @Override
    public int getPageAmount(int pageSize, long userId) {
        var noteAmount = appRepository.getUserAppsAmount(userId);
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
    @Transactional
    public AppResponseDTO updateUserApp(long appId, AppRequestDTO appDTO, long userId) {
        var app = appRepository.findUserAppById(appId, userId)
                .orElseThrow(() -> new EntityException("Application not found"));
        app.setName(appDTO.name());
        var savedApp = appRepository.save(app);
        return new AppResponseDTO(savedApp);
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
