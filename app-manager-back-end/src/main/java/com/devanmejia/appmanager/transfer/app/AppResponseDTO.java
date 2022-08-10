package com.devanmejia.appmanager.transfer.app;


import com.devanmejia.appmanager.entity.App;

import java.text.SimpleDateFormat;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public record AppResponseDTO(long id, String name, String creationTime) {
    private static final DateTimeFormatter DATE_FORMATTER
            = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:s");

    public static AppResponseDTO from(App app, int secondsZoneOffset) {
        var dateString = app.getCreationTime()
                .format(DATE_FORMATTER.withZone(ZoneOffset.ofTotalSeconds(secondsZoneOffset)));
        return new AppResponseDTO(app.getId(), app.getName(), dateString);
    }

}
