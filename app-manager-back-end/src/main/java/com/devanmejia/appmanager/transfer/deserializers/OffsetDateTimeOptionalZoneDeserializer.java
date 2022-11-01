package com.devanmejia.appmanager.transfer.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class OffsetDateTimeOptionalZoneDeserializer extends StdScalarDeserializer<OffsetDateTime> {
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:00 Z");

    protected OffsetDateTimeOptionalZoneDeserializer(Class<?> vc) {super(vc);}

    @Override
    public OffsetDateTime deserialize(
            JsonParser jsonParser,
            DeserializationContext deserializationContext
    ) throws IOException {
        return OffsetDateTime.parse(jsonParser.getText(), FORMATTER);
    }
}
