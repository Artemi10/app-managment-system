package com.devanmejia.appmanager.repository.app;

import com.devanmejia.appmanager.entity.App;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AppNameSearchRepository extends JpaRepository<App, Long> {
    @Query(value = """
              SELECT app.id, app.name, app.creation_time, app.user_id
              FROM applications app
              LEFT JOIN users usr ON app.user_id = usr.id
              WHERE usr.email = :email
              AND ts @@ to_tsquery('english', :name)""", nativeQuery = true)
    Page<App> findAllByUserEmail(String email, String name, Pageable pageable);

    @Query(value = """
              SELECT count(*)
              FROM applications app
              LEFT JOIN users usr ON app.user_id = usr.id
              WHERE usr.email = :email
              AND ts @@ to_tsquery('english', :name)""", nativeQuery = true)
    int getUserAppsAmountByName(String email, String name);
}
