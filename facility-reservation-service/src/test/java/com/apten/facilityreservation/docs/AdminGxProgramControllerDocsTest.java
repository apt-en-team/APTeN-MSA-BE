package com.apten.facilityreservation.docs;

import com.apten.facilityreservation.application.controller.AdminGxProgramController;
import com.apten.facilityreservation.application.service.GxProgramService;
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
@WebMvcTest(controllers = AdminGxProgramController.class)
class AdminGxProgramControllerDocsTest extends FacilityDocsTestBase {

    @MockBean
    private GxProgramService gxProgramService;

    @Test
    void GX_프로그램_등록() throws Exception {
        given(gxProgramService.createGxProgram(any(), any())).willReturn(null);
        mockMvc.perform(post("/api/admin/gx-programs")
                        .header("X-User-Id", "100").header("X-User-Role", "MANAGER").header("X-Complex-Id", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("name", "요가"))))
                .andExpect(status().isCreated())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-gx-program-create",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - GX 프로그램").summary("GX 프로그램 등록").build())));
    }

    @Test
    void 관리자_GX_프로그램_목록_조회() throws Exception {
        given(gxProgramService.getAdminGxProgramList(any(), any())).willReturn(null);
        mockMvc.perform(get("/api/admin/gx-programs")
                        .header("X-User-Id", "100").header("X-User-Role", "MANAGER").header("X-Complex-Id", "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-gx-program-list",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - GX 프로그램").summary("관리자 GX 프로그램 목록 조회").build())));
    }

    @Test
    void 관리자_GX_프로그램_상세_조회() throws Exception {
        given(gxProgramService.getAdminGxProgramDetail(any(), any())).willReturn(null);
        mockMvc.perform(get("/api/admin/gx-programs/{programId}", 1L)
                        .header("X-User-Id", "100").header("X-User-Role", "MANAGER").header("X-Complex-Id", "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-gx-program-detail",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - GX 프로그램").summary("관리자 GX 프로그램 상세 조회").build())));
    }

    @Test
    void GX_프로그램_수정() throws Exception {
        given(gxProgramService.updateGxProgram(any(), any(), any())).willReturn(null);
        mockMvc.perform(patch("/api/admin/gx-programs/{programId}", 1L)
                        .header("X-User-Id", "100").header("X-User-Role", "MANAGER").header("X-Complex-Id", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("name", "필라테스"))))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-gx-program-update",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - GX 프로그램").summary("GX 프로그램 수정").build())));
    }

    @Test
    void GX_프로그램_취소() throws Exception {
        given(gxProgramService.cancelGxProgram(any(), any(), any())).willReturn(null);
        mockMvc.perform(patch("/api/admin/gx-programs/{programId}/cancel", 1L)
                        .header("X-User-Id", "100").header("X-User-Role", "MANAGER").header("X-Complex-Id", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("reason", "강사 부재"))))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-gx-program-cancel",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - GX 프로그램").summary("GX 프로그램 취소").build())));
    }

    @Test
    void GX_모집_마감() throws Exception {
        given(gxProgramService.closeWaiting(any(), any(), any())).willReturn(null);
        mockMvc.perform(patch("/api/admin/gx-programs/{programId}/close-waiting", 1L)
                        .header("X-User-Id", "100").header("X-User-Role", "MANAGER").header("X-Complex-Id", "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-gx-program-close-waiting",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - GX 프로그램").summary("GX 모집 마감").build())));
    }

    @Test
    void GX_프로그램_신청자_목록_조회() throws Exception {
        given(gxProgramService.getAdminGxReservationList(any(), any(), any())).willReturn(null);
        mockMvc.perform(get("/api/admin/gx-programs/{programId}/reservations", 1L)
                        .header("X-User-Id", "100").header("X-User-Role", "MANAGER").header("X-Complex-Id", "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-gx-reservation-list-by-program",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - GX 프로그램").summary("GX 프로그램 신청자 목록 조회").build())));
    }

    @Test
    void GX_일괄_승인() throws Exception {
        given(gxProgramService.bulkApprove(any(), any(), any())).willReturn(null);
        mockMvc.perform(post("/api/admin/gx-programs/{programId}/bulk-approve", 1L)
                        .header("X-User-Id", "100").header("X-User-Role", "MANAGER").header("X-Complex-Id", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("reservationIds", new long[]{1L, 2L}))))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-gx-bulk-approve",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - GX 프로그램").summary("GX 일괄 승인 처리").build())));
    }

    @Test
    void GX_최소인원_검증() throws Exception {
        given(gxProgramService.checkMinimum(any(), any())).willReturn(null);
        mockMvc.perform(post("/api/admin/gx-programs/{programId}/minimum-check", 1L)
                        .header("X-User-Id", "100").header("X-User-Role", "MANAGER").header("X-Complex-Id", "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-gx-minimum-check",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - GX 프로그램").summary("GX 최소 인원 검증").build())));
    }

    @Test
    void GX_현황_조회() throws Exception {
        given(gxProgramService.getGxStatus(any(), any())).willReturn(null);
        mockMvc.perform(get("/api/admin/gx-programs/{programId}/status", 1L)
                        .header("X-User-Id", "100").header("X-User-Role", "MANAGER").header("X-Complex-Id", "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-gx-status",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - GX 프로그램").summary("GX 현황 조회").build())));
    }
}
