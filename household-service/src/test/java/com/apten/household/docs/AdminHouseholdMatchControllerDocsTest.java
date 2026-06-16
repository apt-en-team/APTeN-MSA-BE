package com.apten.household.docs;

import com.apten.household.application.controller.AdminHouseholdMatchController;
import com.apten.household.application.model.response.HouseholdMatchApproveRes;
import com.apten.household.application.model.response.HouseholdMatchListRes;
import com.apten.household.application.model.response.HouseholdMatchRejectRes;
import com.apten.household.application.service.HouseholdMatchService;
import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceDocumentation;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// 관리자 세대 매칭 API 문서화 테스트
@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(controllers = AdminHouseholdMatchController.class)
class AdminHouseholdMatchControllerDocsTest extends HouseholdDocsTestBase {

    @MockBean
    private HouseholdMatchService householdMatchService;


    @Test
    void 세대_매칭_요청_목록_조회() throws Exception {
        HouseholdMatchListRes res = HouseholdMatchListRes.builder()
                .content(List.of()).page(0).size(20).totalElements(0L)
                .totalPages(0).hasNext(false).build();

        given(householdMatchService.getMatchRequestList(any(), any())).willReturn(res);

        mockMvc.perform(get("/api/admin/household-match-requests")
                        .header("X-User-Id", "100").header("X-User-Role", "MANAGER")
                        .header("X-Complex-Id", "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-match-request-list",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 세대 매칭").summary("세대 매칭 요청 목록 조회").build())));
    }

    @Test
    void 세대_매칭_일괄_승인() throws Exception {
        Map<String, Object> req = Map.of("matchRequestIds", List.of(1L, 2L));

        HouseholdMatchApproveRes item = HouseholdMatchApproveRes.builder()
                .matchRequestId(1L).matchStatus("APPROVED")
                .processedAt(LocalDateTime.of(2026, 1, 5, 10, 0)).build();

        given(householdMatchService.approveMatchRequests(any(), any())).willReturn(List.of(item));

        mockMvc.perform(patch("/api/admin/household-match-requests/approve-bulk")
                        .header("X-User-Id", "100").header("X-User-Role", "MANAGER")
                        .header("X-Complex-Id", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-match-request-approve-bulk",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 세대 매칭").summary("세대 매칭 일괄 승인").build())));
    }

    @Test
    void 세대_매칭_단건_승인() throws Exception {
        HouseholdMatchApproveRes res = HouseholdMatchApproveRes.builder()
                .matchRequestId(1L).matchStatus("APPROVED")
                .processedAt(LocalDateTime.of(2026, 1, 5, 10, 0)).build();

        given(householdMatchService.approveMatchRequest(any(), eq(1L))).willReturn(res);

        mockMvc.perform(patch("/api/admin/household-match-requests/{matchRequestId}/approve", 1L)
                        .header("X-User-Id", "100").header("X-User-Role", "MANAGER")
                        .header("X-Complex-Id", "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-match-request-approve",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 세대 매칭").summary("세대 매칭 단건 승인").build())));
    }

    @Test
    void 세대_매칭_거절() throws Exception {
        Map<String, String> req = Map.of("reason", "정보 불일치");

        HouseholdMatchRejectRes res = HouseholdMatchRejectRes.builder()
                .matchRequestId(1L).matchStatus("REJECTED").reason("정보 불일치")
                .processedAt(LocalDateTime.of(2026, 1, 5, 10, 0)).build();

        given(householdMatchService.rejectMatchRequest(any(), eq(1L), any())).willReturn(res);

        mockMvc.perform(patch("/api/admin/household-match-requests/{matchRequestId}/reject", 1L)
                        .header("X-User-Id", "100").header("X-User-Role", "MANAGER")
                        .header("X-Complex-Id", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-match-request-reject",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 세대 매칭").summary("세대 매칭 거절").build())));
    }
}
