package com.devanmejia.appmanager.repository;

import com.devanmejia.appmanager.entity.Event;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("""
              SELECT event
              FROM Event event
              WHERE event.app.id = :appId
              AND event.id = :eventId
              AND event.app.user.id = :userId""")
    Optional<Event> findEvent(long eventId, long appId, long userId);

    @Query("""
              SELECT event
              FROM Event event
              WHERE event.app.id = :appId
              AND event.app.user.id = :userId""")
    List<Event> findEventsByApp(long appId, long userId, Pageable pageable);

    @Query("""
              SELECT count(event)
              FROM Event event
              WHERE event.app.id = :appId
              AND event.app.user.id = :userId""")
    int getAppEventsAmount(long appId, long userId);
}
