package com.roacg.core.utils.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {


    public static LocalDateTimeSerializer INSTANCE = new LocalDateTimeSerializer();

    private LocalDateTimeSerializer() {
        if (INSTANCE != null) {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public void serialize(LocalDateTime dateTime, JsonGenerator generator, SerializerProvider sp)
            throws IOException, JsonProcessingException {
        String formattedDateTime = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        generator.writeString(formattedDateTime);
    }
}