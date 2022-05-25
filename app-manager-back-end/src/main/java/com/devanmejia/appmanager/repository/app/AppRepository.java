package com.devanmejia.appmanager.repository.app;

import com.devanmejia.appmanager.entity.App;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface AppRepository extends JpaRepository<App, Long> {
    @Query(value = """
              SELECT app
              FROM App app
              WHERE app.user.email = :email""")
    Page<App> findAllByUserEmail(String email, Pageable pageable);

    @Query("""
              SELECT count(app)
              FROM App app
              WHERE app.user.email = :email""")
    int getUserAppsAmount(String email);

    @Query("""
              SELECT app
              FROM App app
              WHERE app.user.email = :email
              AND app.id = :appId""")
    Optional<App> findUserAppById(long appId, String email);

    @Modifying
    @Transactional
    void deleteByIdAndUserEmail(long appId, String email);
}
