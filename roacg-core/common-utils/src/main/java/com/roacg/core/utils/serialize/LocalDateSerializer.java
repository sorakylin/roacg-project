package com.roacg.core.utils.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateSerializer extends JsonSerializer<LocalDate> {


    public static LocalDateSerializer INSTANCE = new LocalDateSerializer();

    private LocalDateSerializer() {
        if (INSTANCE != null) {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public void serialize(LocalDate date, JsonGenerator generator, SerializerProvider sp)
            throws IOException, JsonProcessingException {
        String formattedDateTime = date.format(DateTimeFormatter.ISO_DATE);
        generator.writeString(formattedDateTime);
    }
}