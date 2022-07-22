package com.devanmejia.appmanager.transfer.event;

import com.devanmejia.appmanager.entity.Event;

import java.text.SimpleDateFormat;

public record EventResponseDTO(long id, String name, String extraInformation, String time, long appId) {
    private static final SimpleDateFormat DATE_FORMAT
            = new SimpleDateFormat("dd.MM.yyyy HH:mm:s");

    public static EventResponseDTO from(long appId, Event event) {
        var dateString = DATE_FORMAT.format(event.getTime());
        return new EventResponseDTO(
                event.getId(),
                event.getName(),
                event.getExtraInformation(),
                dateString, appId);
    }
}
