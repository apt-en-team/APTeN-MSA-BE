package com.apten.facilityreservation.docs;

import com.apten.facilityreservation.application.controller.GxReservationController;
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
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(controllers = GxReservationController.class)
class GxReservationControllerDocsTest extends FacilityDocsTestBase {

    @MockBean
    private GxReservationService gxReservationService;

    @Test
    void 내_GX_예약_목록_조회() throws Exception {
        given(gxReservationService.getMyGxReservations(any(), any())).willReturn(null);
        mockMvc.perform(get("/api/gx-reservations/my")
                        .header("X-User-Id", "100").header("X-User-Role", "USER").header("X-Complex-Id", "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("resident-my-gx-reservation-list",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Resident - GX 예약").summary("내 GX 예약 목록 조회").build())));
    }

    @Test
    void GX_예약_신청() throws Exception {
        given(gxReservationService.createGxReservation(any(), any(), any())).willReturn(null);
        mockMvc.perform(post("/api/gx-reservations")
                        .header("X-User-Id", "100").header("X-User-Role", "USER").header("X-Complex-Id", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("programId", 1L))))
                .andExpect(status().isCreated())
                .andDo(MockMvcRestDocumentationWrapper.document("resident-gx-reservation-create",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Resident - GX 예약").summary("GX 예약 신청").build())));
    }

    @Test
    void GX_대기_순번_조회() throws Exception {
        given(gxReservationService.getWaiting(any(), any(), any())).willReturn(null);
        mockMvc.perform(get("/api/gx-reservations/{gxReservationId}/waiting", 1L)
                        .header("X-User-Id", "100").header("X-User-Role", "USER").header("X-Complex-Id", "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("resident-gx-reservation-waiting",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Resident - GX 예약").summary("GX 대기 순번 조회").build())));
    }

    @Test
    void GX_예약_취소() throws Exception {
        given(gxReservationService.cancelGxReservation(any(), any(), any())).willReturn(null);
        mockMvc.perform(patch("/api/gx-reservations/{gxReservationId}/cancel", 1L)
                        .header("X-User-Id", "100").header("X-User-Role", "USER").header("X-Complex-Id", "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("resident-gx-reservation-cancel",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Resident - GX 예약").summary("GX 예약 취소").build())));
    }
}
