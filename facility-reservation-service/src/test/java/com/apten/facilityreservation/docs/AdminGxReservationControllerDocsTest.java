package com.apten.facilityreservation.docs;

import com.apten.facilityreservation.application.controller.AdminGxReservationController;
import com.apten.facilityreservation.application.service.GxReservationService;
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
@WebMvcTest(controllers = AdminGxReservationController.class)
class AdminGxReservationControllerDocsTest extends FacilityDocsTestBase {

    @MockBean
    private GxReservationService gxReservationService;

    @Test
    void 관리자_GX_예약_상세_조회() throws Exception {
        given(gxReservationService.getAdminGxReservationDetail(any(), any())).willReturn(null);
        mockMvc.perform(get("/api/admin/gx-reservations/{gxReservationId}", 1L)
                        .header("X-User-Id", "100").header("X-User-Role", "MANAGER").header("X-Complex-Id", "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-gx-reservation-detail",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - GX 예약").summary("관리자 GX 예약 상세 조회").build())));
    }

    @Test
    void GX_단건_승인() throws Exception {
        given(gxReservationService.approveGxReservation(any(), any())).willReturn(null);
        mockMvc.perform(patch("/api/admin/gx-reservations/{gxReservationId}/approve", 1L)
                        .header("X-User-Id", "100").header("X-User-Role", "MANAGER").header("X-Complex-Id", "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-gx-reservation-approve",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - GX 예약").summary("GX 단건 승인 처리").build())));
    }

    @Test
    void GX_단건_거절() throws Exception {
        given(gxReservationService.rejectGxReservation(any(), any(), any())).willReturn(null);
        mockMvc.perform(patch("/api/admin/gx-reservations/{gxReservationId}/reject", 1L)
                        .header("X-User-Id", "100").header("X-User-Role", "MANAGER").header("X-Complex-Id", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("reason", "정원 초과"))))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-gx-reservation-reject",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - GX 예약").summary("GX 단건 거절 처리").build())));
    }

    @Test
    void 관리자_GX_예약_강제_취소() throws Exception {
        given(gxReservationService.cancelGxReservationByAdmin(any(), any())).willReturn(null);
        mockMvc.perform(patch("/api/admin/gx-reservations/{gxReservationId}/cancel", 1L)
                        .header("X-User-Id", "100").header("X-User-Role", "MANAGER").header("X-Complex-Id", "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-gx-reservation-cancel",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - GX 예약").summary("관리자 GX 예약 강제 취소").build())));
    }
}
