package com.electralab.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AllocationRequestDtoTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @DisplayName("Should successfully deserialize date-only string from HTML5 date picker")
    void shouldDeserializeDateOnlyString() throws Exception {
        String json = """
                {
                    "studentName": "John Doe",
                    "rollNumber": "21B91A0401",
                    "equipmentId": 1,
                    "quantity": 1,
                    "expectedReturnDate": "2026-10-16",
                    "remarks": "Lab experiment"
                }
                """;

        AllocationRequestDto dto = objectMapper.readValue(json, AllocationRequestDto.class);

        assertNotNull(dto);
        assertEquals("John Doe", dto.getStudentName());
        assertEquals("21B91A0401", dto.getRollNumber());
        assertEquals(1L, dto.getEquipmentId());
        assertEquals(1, dto.getQuantity());
        assertNotNull(dto.getExpectedReturnDate());
        assertEquals(2026, dto.getExpectedReturnDate().getYear());
        assertEquals(10, dto.getExpectedReturnDate().getMonthValue());
        assertEquals(16, dto.getExpectedReturnDate().getDayOfMonth());
        assertEquals(18, dto.getExpectedReturnDate().getHour());
    }

    @Test
    @DisplayName("Should successfully deserialize ISO date-time string")
    void shouldDeserializeIsoDateTimeString() throws Exception {
        String json = """
                {
                    "studentName": "John Doe",
                    "rollNumber": "21B91A0401",
                    "equipmentId": 2,
                    "quantity": 1,
                    "expectedReturnDate": "2026-10-16T14:30:00",
                    "remarks": "Afternoon lab"
                }
                """;

        AllocationRequestDto dto = objectMapper.readValue(json, AllocationRequestDto.class);

        assertNotNull(dto);
        assertEquals(LocalDateTime.of(2026, 10, 16, 14, 30, 0), dto.getExpectedReturnDate());
    }

    @Test
    @DisplayName("Should successfully handle empty string as null return date without error")
    void shouldHandleEmptyStringAsNull() throws Exception {
        String json = """
                {
                    "studentName": "John Doe",
                    "rollNumber": "21B91A0401",
                    "equipmentId": 3,
                    "quantity": 1,
                    "expectedReturnDate": "",
                    "remarks": ""
                }
                """;

        AllocationRequestDto dto = objectMapper.readValue(json, AllocationRequestDto.class);

        assertNotNull(dto);
        assertNull(dto.getExpectedReturnDate());
    }

    @Test
    @DisplayName("Should serialize EquipmentResponseDto with both quantityAvailable and availableQuantity")
    void shouldSerializeEquipmentResponseDtoWithBothProperties() throws Exception {
        EquipmentResponseDto dto = new EquipmentResponseDto(
                1L, "Oscilloscope", "DSO-01", "Instruments", "SN123",
                5, 10, "AVAILABLE", "Rack A", LocalDateTime.now(), LocalDateTime.now()
        );

        String json = objectMapper.writeValueAsString(dto);

        assertTrue(json.contains("\"quantityAvailable\":5"));
        assertTrue(json.contains("\"availableQuantity\":5"));
    }
}
