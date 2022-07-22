package com.devanmejia.appmanager.transfer.app;


import com.devanmejia.appmanager.entity.App;

import java.text.SimpleDateFormat;

public record AppResponseDTO(long id, String name, String creationTime) {
    private static final SimpleDateFormat DATE_FORMAT
            = new SimpleDateFormat("dd.MM.yyyy HH:mm:s");

    public static AppResponseDTO from(App app) {
        var dateString = DATE_FORMAT.format(app.getCreationTime());
        return new AppResponseDTO(app.getId(), app.getName(), dateString);
    }

}
