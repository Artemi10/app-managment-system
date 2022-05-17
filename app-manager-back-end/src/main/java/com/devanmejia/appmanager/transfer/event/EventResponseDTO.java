package com.devanmejia.appmanager.transfer.event;

import com.devanmejia.appmanager.entity.Event;

import java.text.SimpleDateFormat;

public record EventResponseDTO(long appId, String name, String extraInformation, String time) {
    private static final SimpleDateFormat DATE_FORMAT
            = new SimpleDateFormat("dd.MM.yyyy HH:mm:s");

    public EventResponseDTO(long id, Event event) {
        this(id, event.getName(), event.getExtraInformation(), DATE_FORMAT.format(event.getTime()));
    }
}
