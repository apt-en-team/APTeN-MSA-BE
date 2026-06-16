package com.apten.facilityreservation.docs;

import com.apten.facilityreservation.application.controller.AdminReservationController;
import com.apten.facilityreservation.application.service.AdminReservationOverviewService;
import com.apten.facilityreservation.application.service.AdminReservationStatsService;
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
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(controllers = AdminReservationController.class)
class AdminReservationControllerDocsTest extends FacilityDocsTestBase {

    @MockBean
    private ReservationService reservationService;
    @MockBean
    private AdminReservationOverviewService adminReservationOverviewService;
    @MockBean
    private AdminReservationStatsService adminReservationStatsService;

    @Test
    void 관리자_예약_목록_조회() throws Exception {
        given(reservationService.getAdminReservationList(any(), any())).willReturn(null);
        mockMvc.perform(get("/api/admin/reservations")
                        .header("X-User-Id", "100").header("X-User-Role", "MANAGER").header("X-Complex-Id", "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-reservation-list",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 예약").summary("관리자 예약 목록 조회").build())));
    }

    @Test
    void 관리자_예약_통계_조회() throws Exception {
        given(adminReservationStatsService.getStats(any())).willReturn(null);
        mockMvc.perform(get("/api/admin/reservations/stats")
                        .header("X-User-Id", "100").header("X-User-Role", "MANAGER").header("X-Complex-Id", "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-reservation-stats",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 예약").summary("관리자 예약 통계 조회").build())));
    }

    @Test
    void 관리자_예약_통합_개요_조회() throws Exception {
        given(adminReservationOverviewService.getOverview(any(), any())).willReturn(null);
        mockMvc.perform(get("/api/admin/reservations/overview")
                        .header("X-User-Id", "100").header("X-User-Role", "MANAGER").header("X-Complex-Id", "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-reservation-overview",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 예약").summary("관리자 예약 통합 개요 조회").build())));
    }

    @Test
    void 관리자_예약_상세_조회() throws Exception {
        given(reservationService.getAdminReservationDetail(any(), any())).willReturn(null);
        mockMvc.perform(get("/api/admin/reservations/{reservationId}", 1L)
                        .header("X-User-Id", "100").header("X-User-Role", "MANAGER").header("X-Complex-Id", "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-reservation-detail",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 예약").summary("관리자 예약 상세 조회").build())));
    }

    @Test
    void 관리자_예약_강제_취소() throws Exception {
        given(reservationService.cancelReservationByAdmin(any(), any(), any())).willReturn(null);
        mockMvc.perform(patch("/api/admin/reservations/{reservationId}/cancel", 1L)
                        .header("X-User-Id", "100").header("X-User-Role", "MANAGER").header("X-Complex-Id", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("reason", "운영 정책"))))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-reservation-cancel",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 예약").summary("관리자 예약 강제 취소").build())));
    }
}
