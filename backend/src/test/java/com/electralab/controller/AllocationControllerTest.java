package com.electralab.controller;

import com.electralab.config.JwtUtils;
import com.electralab.dto.AllocationRequestDto;
import com.electralab.dto.AllocationResponseDto;
import com.electralab.service.AllocationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import javax.sql.DataSource;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AllocationController.class)
@AutoConfigureMockMvc(addFilters = false)
class AllocationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AllocationService allocationService;

    @MockBean
    private DataSource dataSource;

    @MockBean
    private JwtUtils jwtUtils;

    @Test
    @DisplayName("POST /api/allocations/issue accepts date-only string from HTML5 date picker without 400 JSON error")
    void shouldAcceptDateOnlyInIssueEquipmentRequest() throws Exception {
        AllocationResponseDto responseDto = new AllocationResponseDto(
                100L,
                "Jane Doe",
                "21B91A0450",
                1L,
                "Digital Storage Oscilloscope",
                1,
                LocalDateTime.now(),
                LocalDateTime.of(2026, 10, 16, 18, 0, 0),
                null,
                "ISSUED",
                "Lab experiment",
                LocalDateTime.now()
        );

        when(allocationService.issueEquipment(any(AllocationRequestDto.class))).thenReturn(responseDto);

        String jsonPayload = """
                {
                    "studentName": "Jane Doe",
                    "rollNumber": "21B91A0450",
                    "equipmentId": 1,
                    "quantity": 1,
                    "expectedReturnDate": "2026-10-16",
                    "remarks": "Lab experiment"
                }
                """;

        mockMvc.perform(post("/api/allocations/issue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100L))
                .andExpect(jsonPath("$.studentName").value("Jane Doe"))
                .andExpect(jsonPath("$.status").value("ISSUED"));
    }

    @Test
    @DisplayName("POST /api/allocations/issue accepts ISO date-time string without 400 JSON error")
    void shouldAcceptIsoDateTimeInIssueEquipmentRequest() throws Exception {
        AllocationResponseDto responseDto = new AllocationResponseDto(
                101L,
                "Jane Doe",
                "21B91A0450",
                1L,
                "Digital Storage Oscilloscope",
                1,
                LocalDateTime.now(),
                LocalDateTime.of(2026, 10, 16, 18, 0, 0),
                null,
                "ISSUED",
                "Lab experiment",
                LocalDateTime.now()
        );

        when(allocationService.issueEquipment(any(AllocationRequestDto.class))).thenReturn(responseDto);

        String jsonPayload = """
                {
                    "studentName": "Jane Doe",
                    "rollNumber": "21B91A0450",
                    "equipmentId": 1,
                    "quantity": 1,
                    "expectedReturnDate": "2026-10-16T18:00:00",
                    "remarks": "Lab experiment"
                }
                """;

        mockMvc.perform(post("/api/allocations/issue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(101L));
    }
}
