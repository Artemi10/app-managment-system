package com.devanmejia.appmanager.transfer.stat;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public record StatRequestDTO(String email, Timestamp from, Timestamp to) {
    private static final SimpleDateFormat DATE_FORMAT
            = new SimpleDateFormat("dd.MM.yyyy");

    public StatRequestDTO(String email, String from, String  to) throws ParseException {
       this(
               email,
               new Timestamp(DATE_FORMAT.parse(from).getTime()),
               new Timestamp(DATE_FORMAT.parse(to).getTime())
       );
    }
}
