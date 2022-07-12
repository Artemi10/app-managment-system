package com.devanmejia.appmanager.transfer.event;

import com.devanmejia.appmanager.entity.Event;

import java.text.SimpleDateFormat;

public record EventResponseDTO(long id, String name, String extraInformation, String time, long appId) {
    private static final SimpleDateFormat DATE_FORMAT
            = new SimpleDateFormat("dd.MM.yyyy HH:mm:s");

    public EventResponseDTO(long id, Event event) {
        this(event.getId(), event.getName(), event.getExtraInformation(), DATE_FORMAT.format(event.getTime()), id);
    }
}
