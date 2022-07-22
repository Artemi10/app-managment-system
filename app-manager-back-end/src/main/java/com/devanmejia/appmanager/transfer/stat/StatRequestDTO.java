package com.devanmejia.appmanager.transfer.stat;

import com.devanmejia.appmanager.exception.RequestBodyParseException;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public record StatRequestDTO(long userId, Timestamp from, Timestamp to) {
    private static final SimpleDateFormat DATE_FORMAT
            = new SimpleDateFormat("dd.MM.yyyy");

    public static StatRequestDTO from(long userId, String from, String  to) {
        try {
            var fromDate = new Timestamp(DATE_FORMAT.parse(from).getTime());
            var toDate = new Timestamp(DATE_FORMAT.parse(to).getTime());
            return new StatRequestDTO(userId, fromDate, toDate);
        } catch (ParseException exception) {
            throw new RequestBodyParseException("Invalid date parameters");
        }
    }
}
