package com.devanmejia.appmanager.transfer.event;

import com.devanmejia.appmanager.entity.Event;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public record EventResponseDTO(long id, String name, String extraInformation, String time, long appId) {
    private static final DateTimeFormatter DATE_FORMATTER
            = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:s");

    public static EventResponseDTO from(long appId, Event event, int secondsZoneOffset) {
        DATE_FORMATTER.withZone(ZoneOffset.ofTotalSeconds(secondsZoneOffset));
        var dateString = event.getCreationTime().format(DATE_FORMATTER);
        return new EventResponseDTO(
                event.getId(),
                event.getName(),
                event.getExtraInformation(),
                dateString,
                appId);
    }
}
