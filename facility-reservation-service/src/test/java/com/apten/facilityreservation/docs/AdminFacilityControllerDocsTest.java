package com.apten.facilityreservation.docs;

import com.apten.facilityreservation.application.controller.AdminFacilityController;
import com.apten.facilityreservation.application.service.FacilityService;
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
@WebMvcTest(controllers = AdminFacilityController.class)
class AdminFacilityControllerDocsTest extends FacilityDocsTestBase {

    @MockBean
    private FacilityService facilityService;

    private static final String ADMIN_USER_ID = "X-User-Id";
    private static final String ADMIN_ROLE = "X-User-Role";
    private static final String COMPLEX_ID = "X-Complex-Id";

    @Test
    void 시설_등록() throws Exception {
        given(facilityService.createFacility(any(), any())).willReturn(null);
        mockMvc.perform(post("/api/admin/facilities")
                        .header(ADMIN_USER_ID, "100").header(ADMIN_ROLE, "MANAGER").header(COMPLEX_ID, "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("name", "헬스장"))))
                .andExpect(status().isCreated())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-facility-create",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 시설").summary("시설 등록").build())));
    }

    @Test
    void 관리자_시설_목록_조회() throws Exception {
        given(facilityService.getAdminFacilityList(any(), any())).willReturn(null);
        mockMvc.perform(get("/api/admin/facilities")
                        .header(ADMIN_USER_ID, "100").header(ADMIN_ROLE, "MANAGER").header(COMPLEX_ID, "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-facility-list",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 시설").summary("관리자 시설 목록 조회").build())));
    }

    @Test
    void 관리자_시설_상세_조회() throws Exception {
        given(facilityService.getAdminFacilityDetail(any(), any())).willReturn(null);
        mockMvc.perform(get("/api/admin/facilities/{facilityId}", 1L)
                        .header(ADMIN_USER_ID, "100").header(ADMIN_ROLE, "MANAGER").header(COMPLEX_ID, "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-facility-detail",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 시설").summary("관리자 시설 상세 조회").build())));
    }

    @Test
    void 시설_수정() throws Exception {
        given(facilityService.updateFacility(any(), any(), any())).willReturn(null);
        mockMvc.perform(patch("/api/admin/facilities/{facilityId}", 1L)
                        .header(ADMIN_USER_ID, "100").header(ADMIN_ROLE, "MANAGER").header(COMPLEX_ID, "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("name", "수정된 헬스장"))))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-facility-update",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 시설").summary("시설 수정").build())));
    }

    @Test
    void 시설_삭제() throws Exception {
        given(facilityService.deleteFacility(any(), any())).willReturn(null);
        mockMvc.perform(delete("/api/admin/facilities/{facilityId}", 1L)
                        .header(ADMIN_USER_ID, "100").header(ADMIN_ROLE, "MANAGER").header(COMPLEX_ID, "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-facility-delete",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 시설").summary("시설 삭제").build())));
    }

    @Test
    void 시설_활성_상태_변경() throws Exception {
        given(facilityService.changeFacilityActive(any(), any(), any())).willReturn(null);
        mockMvc.perform(patch("/api/admin/facilities/{facilityId}/active", 1L)
                        .header(ADMIN_USER_ID, "100").header(ADMIN_ROLE, "MANAGER").header(COMPLEX_ID, "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("isActive", true))))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-facility-active",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 시설").summary("시설 활성/비활성 변경").build())));
    }

    @Test
    void 시설_타입_목록_조회() throws Exception {
        given(facilityService.getFacilityTypeList(any(), any())).willReturn(null);
        mockMvc.perform(get("/api/admin/facility-types")
                        .header(ADMIN_USER_ID, "100").header(ADMIN_ROLE, "MANAGER").header(COMPLEX_ID, "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-facility-type-list",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 시설").summary("시설 타입 목록 조회").build())));
    }

    @Test
    void 차단시간_등록() throws Exception {
        given(facilityService.createFacilityBlockTime(any(), any(), any())).willReturn(null);
        mockMvc.perform(post("/api/admin/facilities/{facilityId}/block-times", 1L)
                        .header(ADMIN_USER_ID, "100").header(ADMIN_ROLE, "MANAGER").header(COMPLEX_ID, "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("startTime", "09:00", "endTime", "10:00"))))
                .andExpect(status().isCreated())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-facility-block-time-create",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 시설 차단").summary("시설 차단 시간 등록").build())));
    }

    @Test
    void 차단시간_목록_조회() throws Exception {
        given(facilityService.getFacilityBlockTimeList(any(), any(), any())).willReturn(null);
        mockMvc.perform(get("/api/admin/facilities/{facilityId}/block-times", 1L)
                        .header(ADMIN_USER_ID, "100").header(ADMIN_ROLE, "MANAGER").header(COMPLEX_ID, "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-facility-block-time-list",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 시설 차단").summary("시설 차단 시간 목록 조회").build())));
    }

    @Test
    void 반복차단_배치_등록() throws Exception {
        given(facilityService.createFacilityBlockTimeBatch(any(), any(), any())).willReturn(null);
        mockMvc.perform(post("/api/admin/facilities/{facilityId}/block-times/batch", 1L)
                        .header(ADMIN_USER_ID, "100").header(ADMIN_ROLE, "MANAGER").header(COMPLEX_ID, "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("dayOfWeek", "MONDAY"))))
                .andExpect(status().isCreated())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-facility-block-batch-create",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 시설 차단").summary("반복 차단 배치 등록").build())));
    }

    @Test
    void 반복차단_그룹_비활성화() throws Exception {
        given(facilityService.deactivateFacilityBlockTimeBatch(any(), any(), any())).willReturn(null);
        mockMvc.perform(patch("/api/admin/facilities/{facilityId}/block-times/batch/{batchId}/deactivate", 1L, 2L)
                        .header(ADMIN_USER_ID, "100").header(ADMIN_ROLE, "MANAGER").header(COMPLEX_ID, "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-facility-block-batch-deactivate",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 시설 차단").summary("반복 차단 그룹 비활성화").build())));
    }

    @Test
    void 차단시간_단건_비활성화() throws Exception {
        given(facilityService.deactivateFacilityBlockTime(any(), any(), any())).willReturn(null);
        mockMvc.perform(patch("/api/admin/facilities/{facilityId}/block-times/{blockTimeId}/deactivate", 1L, 2L)
                        .header(ADMIN_USER_ID, "100").header(ADMIN_ROLE, "MANAGER").header(COMPLEX_ID, "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-facility-block-time-deactivate",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 시설 차단").summary("시설 차단 시간 단건 비활성화").build())));
    }

    @Test
    void 차단시간_단건_수정() throws Exception {
        given(facilityService.updateFacilityBlockTime(any(), any(), any(), any())).willReturn(null);
        mockMvc.perform(patch("/api/admin/facilities/{facilityId}/block-times/{blockTimeId}", 1L, 2L)
                        .header(ADMIN_USER_ID, "100").header(ADMIN_ROLE, "MANAGER").header(COMPLEX_ID, "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("startTime", "10:00"))))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-facility-block-time-update",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 시설 차단").summary("시설 차단 시간 단건 수정").build())));
    }

    @Test
    void 정기휴무_규칙_등록() throws Exception {
        given(facilityService.createClosureRule(any(), any(), any())).willReturn(null);
        mockMvc.perform(post("/api/admin/facilities/{facilityId}/closure-rules", 1L)
                        .header(ADMIN_USER_ID, "100").header(ADMIN_ROLE, "MANAGER").header(COMPLEX_ID, "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("dayOfWeek", "SUNDAY"))))
                .andExpect(status().isCreated())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-facility-closure-rule-create",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 정기 휴무").summary("정기 휴무 규칙 등록").build())));
    }

    @Test
    void 정기휴무_규칙_목록_조회() throws Exception {
        given(facilityService.getClosureRuleList(any(), any())).willReturn(null);
        mockMvc.perform(get("/api/admin/facilities/{facilityId}/closure-rules", 1L)
                        .header(ADMIN_USER_ID, "100").header(ADMIN_ROLE, "MANAGER").header(COMPLEX_ID, "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-facility-closure-rule-list",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 정기 휴무").summary("정기 휴무 규칙 목록 조회").build())));
    }

    @Test
    void 정기휴무_규칙_수정() throws Exception {
        given(facilityService.updateClosureRule(any(), any(), any(), any())).willReturn(null);
        mockMvc.perform(patch("/api/admin/facilities/{facilityId}/closure-rules/{ruleId}", 1L, 2L)
                        .header(ADMIN_USER_ID, "100").header(ADMIN_ROLE, "MANAGER").header(COMPLEX_ID, "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("isActive", false))))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-facility-closure-rule-update",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 정기 휴무").summary("정기 휴무 규칙 수정").build())));
    }

    @Test
    void 정기휴무_규칙_비활성화() throws Exception {
        given(facilityService.deactivateClosureRule(any(), any(), any())).willReturn(null);
        mockMvc.perform(patch("/api/admin/facilities/{facilityId}/closure-rules/{ruleId}/deactivate", 1L, 2L)
                        .header(ADMIN_USER_ID, "100").header(ADMIN_ROLE, "MANAGER").header(COMPLEX_ID, "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-facility-closure-rule-deactivate",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 정기 휴무").summary("정기 휴무 규칙 비활성화").build())));
    }

    @Test
    void 좌석_등록() throws Exception {
        given(facilityService.createFacilitySeat(any(), any(), any())).willReturn(null);
        mockMvc.perform(post("/api/admin/facilities/{facilityId}/seats", 1L)
                        .header(ADMIN_USER_ID, "100").header(ADMIN_ROLE, "MANAGER").header(COMPLEX_ID, "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("seatNumber", "A-01"))))
                .andExpect(status().isCreated())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-facility-seat-create",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 시설 좌석").summary("시설 좌석 등록").build())));
    }

    @Test
    void 좌석_일괄_등록() throws Exception {
        given(facilityService.createFacilitySeatsBulk(any(), any(), any())).willReturn(null);
        mockMvc.perform(post("/api/admin/facilities/{facilityId}/seats/bulk", 1L)
                        .header(ADMIN_USER_ID, "100").header(ADMIN_ROLE, "MANAGER").header(COMPLEX_ID, "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("count", 10))))
                .andExpect(status().isCreated())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-facility-seat-bulk-create",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 시설 좌석").summary("시설 좌석 일괄 등록").build())));
    }

    @Test
    void 좌석_목록_조회() throws Exception {
        given(facilityService.getFacilitySeatList(any(), any())).willReturn(null);
        mockMvc.perform(get("/api/admin/facilities/{facilityId}/seats", 1L)
                        .header(ADMIN_USER_ID, "100").header(ADMIN_ROLE, "MANAGER").header(COMPLEX_ID, "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-facility-seat-list",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 시설 좌석").summary("시설 좌석 목록 조회").build())));
    }

    @Test
    void 좌석_수정() throws Exception {
        given(facilityService.updateFacilitySeat(any(), any(), any())).willReturn(null);
        mockMvc.perform(patch("/api/admin/facility-seats/{seatId}", 1L)
                        .header(ADMIN_USER_ID, "100").header(ADMIN_ROLE, "MANAGER").header(COMPLEX_ID, "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("seatNumber", "B-01"))))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-facility-seat-update",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 시설 좌석").summary("시설 좌석 수정").build())));
    }

    @Test
    void 시설_이용_현황_조회() throws Exception {
        given(facilityService.getFacilityUsageStatus(any(), any())).willReturn(null);
        mockMvc.perform(get("/api/admin/facility-usage/status")
                        .header(ADMIN_USER_ID, "100").header(ADMIN_ROLE, "MANAGER").header(COMPLEX_ID, "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-facility-usage-status",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 시설 현황").summary("시설 이용 현황 조회").build())));
    }

    @Test
    void 좌석_상태_조회() throws Exception {
        given(facilityService.getSeatStatus(any(), any(), any())).willReturn(null);
        mockMvc.perform(get("/api/admin/facilities/{facilityId}/seat-status", 1L)
                        .header(ADMIN_USER_ID, "100").header(ADMIN_ROLE, "MANAGER").header(COMPLEX_ID, "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-facility-seat-status",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 시설 현황").summary("좌석 상태 조회").build())));
    }

    @Test
    void 정원형_이용_현황_조회() throws Exception {
        given(facilityService.getCountStatus(any(), any(), any())).willReturn(null);
        mockMvc.perform(get("/api/admin/facilities/{facilityId}/count-status", 1L)
                        .header(ADMIN_USER_ID, "100").header(ADMIN_ROLE, "MANAGER").header(COMPLEX_ID, "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-facility-count-status",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 시설 현황").summary("정원형 이용 현황 조회").build())));
    }
}
