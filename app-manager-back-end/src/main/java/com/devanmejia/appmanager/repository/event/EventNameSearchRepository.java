package com.devanmejia.appmanager.repository.event;

import com.devanmejia.appmanager.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EventNameSearchRepository extends JpaRepository<Event, Long> {
    @Query(value = """
              SELECT events.id, events.name, extra_information, events.creation_time, application_id
              FROM events
              LEFT JOIN applications a on a.id = events.application_id
              WHERE a.user_id = :userId
              AND a.id = :appId
              AND (name_ts @@ to_tsquery('english', :name) OR extra_information_ts @@ to_tsquery('english', :name))
              LIMIT :limit
              OFFSET :offset""", nativeQuery = true)
    List<Event> findAppEventsByName(long appId, long userId, String name, int limit, long offset);

    @Query(value = """
              SELECT count(*)
              FROM events
              LEFT JOIN applications a on a.id = events.application_id
              WHERE a.user_id = :userId
              AND a.id = :appId
              AND (name_ts @@ to_tsquery('english', :name) OR extra_information_ts @@ to_tsquery('english', :name))""", nativeQuery = true)
    int getAppEventsAmountByName(long appId, long userId, String name);
}
