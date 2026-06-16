package com.apten.facilityreservation.docs;

import com.apten.facilityreservation.application.controller.ReservationController;
import com.apten.facilityreservation.application.service.ReservationService;
import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceDocumentation;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(controllers = ReservationController.class)
class ReservationControllerDocsTest extends FacilityDocsTestBase {

    @MockBean
    private ReservationService reservationService;

    @Test
    void 예약_가능_시간_조회() throws Exception {
        given(reservationService.getAvailableTimeList(any(), any(), any())).willReturn(null);
        mockMvc.perform(get("/api/reservations/available-times")
                        .header("X-User-Id", "100").header("X-User-Role", "USER").header("X-Complex-Id", "10")
                        .param("facilityId", "1").param("date", "2026-07-01"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("resident-reservation-available-times",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Resident - 예약").summary("예약 가능 시간 조회").build())));
    }

    @Test
    void 좌석_임시_선점() throws Exception {
        given(reservationService.createSeatHold(any(), any(), any())).willReturn(null);
        mockMvc.perform(post("/api/reservations/seat-holds")
                        .header("X-User-Id", "100").header("X-User-Role", "USER").header("X-Complex-Id", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("facilityId", 1L, "seatId", 2L, "date", "2026-07-01"))))
                .andExpect(status().isCreated())
                .andDo(MockMvcRestDocumentationWrapper.document("resident-seat-hold-create",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Resident - 예약").summary("좌석 임시 선점").build())));
    }

    @Test
    void 예약_생성() throws Exception {
        given(reservationService.createReservation(any(), any(), any())).willReturn(null);
        mockMvc.perform(post("/api/reservations")
                        .header("X-User-Id", "100").header("X-User-Role", "USER").header("X-Complex-Id", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("facilityId", 1L, "date", "2026-07-01"))))
                .andExpect(status().isCreated())
                .andDo(MockMvcRestDocumentationWrapper.document("resident-reservation-create",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Resident - 예약").summary("예약 생성").build())));
    }

    @Test
    void 내_예약_통합_목록_조회() throws Exception {
        given(reservationService.getMyUnifiedReservations(any(), any(), any())).willReturn(null);
        mockMvc.perform(get("/api/reservations/my")
                        .header("X-User-Id", "100").header("X-User-Role", "USER").header("X-Complex-Id", "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("resident-my-reservation-list",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Resident - 예약").summary("내 예약 통합 목록 조회").build())));
    }

    @Test
    void 내_예약_상세_조회() throws Exception {
        given(reservationService.getMyReservationDetail(any(), any(), any())).willReturn(null);
        mockMvc.perform(get("/api/reservations/{reservationId}", 1L)
                        .header("X-User-Id", "100").header("X-User-Role", "USER").header("X-Complex-Id", "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("resident-reservation-detail",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Resident - 예약").summary("내 예약 상세 조회").build())));
    }

    @Test
    void 예약_취소() throws Exception {
        given(reservationService.cancelReservation(any(), any(), any(), any())).willReturn(null);
        mockMvc.perform(patch("/api/reservations/{reservationId}/cancel", 1L)
                        .header("X-User-Id", "100").header("X-User-Role", "USER").header("X-Complex-Id", "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("resident-reservation-cancel",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Resident - 예약").summary("예약 취소").build())));
    }
}
