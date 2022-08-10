package com.devanmejia.appmanager.transfer.app;


import com.devanmejia.appmanager.entity.App;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

public record AppResponseDTO(long id, String name, String creationTime) {
    private static final DateTimeFormatter DATE_FORMATTER
            = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:s");

    public static AppResponseDTO from(App app) {
        var dateString = app.getCreationTime().format(DATE_FORMATTER);
        return new AppResponseDTO(app.getId(), app.getName(), dateString);
    }

}
