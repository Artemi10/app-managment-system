package com.devanmejia.appmanager.repository.app;

import com.devanmejia.appmanager.entity.App;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AppNameSearchRepository extends JpaRepository<App, Long> {
    @Query(value = """
              SELECT id, name, creation_time, user_id
              FROM applications
              WHERE user_id = :userId
              AND ts @@ to_tsquery('english', :name)
              LIMIT :limit
              OFFSET :offset""", nativeQuery = true)
    List<App> findUserAppsByName(long userId, String name, int limit, long offset);

    @Query(value = """
              SELECT count(*)
              FROM applications
              WHERE user_id = :userId
              AND ts @@ to_tsquery('english', :name)""", nativeQuery = true)
    int getUserAppsAmountByName(long userId, String name);
}
