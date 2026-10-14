package com.electralab.dto;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Custom Jackson deserializer to handle various date and datetime formats for requests,
 * accommodating HTML5 date inputs (yyyy-MM-dd), standard ISO-8601 (yyyy-MM-dd'T'HH:mm:ss),
 * offset timestamps (e.g. UTC Z), and empty strings gracefully.
 */
public class FlexibleLocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

    private static final DateTimeFormatter SPACE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm[:ss]");

    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getText();
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        value = value.trim();

        // 1. Standard ISO-8601 date-time: 2026-10-16T18:00:00
        try {
            return LocalDateTime.parse(value, DateTimeFormatter.ISO_DATE_TIME);
        } catch (Exception ignored) {
        }

        // 2. Date-only format from HTML5 <input type="date">: 2026-10-16
        try {
            LocalDate date = LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE);
            return date.atTime(18, 0, 0); // Default to lab session closing time (6:00 PM)
        } catch (Exception ignored) {
        }

        // 3. ISO OffsetDateTime (e.g., 2026-10-16T18:00:00.000Z)
        try {
            return java.time.OffsetDateTime.parse(value).toLocalDateTime();
        } catch (Exception ignored) {
        }

        // 4. Space separated date-time: 2026-10-16 18:00:00
        try {
            return LocalDateTime.parse(value, SPACE_FORMATTER);
        } catch (Exception e) {
            throw ctxt.instantiationException(LocalDateTime.class,
                    "Failed to parse expected return date: '" + value + "'. Accepted formats: yyyy-MM-dd or yyyy-MM-dd'T'HH:mm:ss");
        }
    }
}
