package com.devanmejia.appmanager.repository.app;

import com.devanmejia.appmanager.entity.App;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AppNameSearchRepository extends JpaRepository<App, Long> {
    @Query(value = """
              SELECT id, name, creation_time, user_id
              FROM applications
              WHERE user_id = :userId
              AND ts @@ to_tsquery('english', :name)""", nativeQuery = true)
    Page<App> findUserAppsByName(long userId, String name, Pageable pageable);

    @Query(value = """
              SELECT count(*)
              FROM applications
              WHERE user_id = :userId
              AND ts @@ to_tsquery('english', :name)""", nativeQuery = true)
    int getUserAppsAmountByName(long userId, String name);
}
