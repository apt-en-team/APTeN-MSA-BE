package com.apten.household.docs;

import com.apten.household.application.controller.AdminExpectedResidentController;
import com.apten.household.application.model.response.ExpectedResidentListRes;
import com.apten.household.application.model.response.ExpectedResidentRes;
import com.apten.household.application.service.ExpectedResidentService;
import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceDocumentation;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// 관리자 입주민 명부 API 문서화 테스트
@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(controllers = AdminExpectedResidentController.class)
class AdminExpectedResidentControllerDocsTest extends HouseholdDocsTestBase {

    @MockBean
    private ExpectedResidentService expectedResidentService;


    @Test
    void 입주민_명부_등록() throws Exception {
        Map<String, Object> req = Map.of("householdId", 1L, "name", "홍길동", "phone", "010-1234-5678");

        ExpectedResidentRes res = ExpectedResidentRes.builder()
                .expectedResidentId(1L).complexId(10L).householdId(1L)
                .building("101").unit("101").build();

        given(expectedResidentService.registerExpectedResident(any(), any())).willReturn(res);

        mockMvc.perform(post("/api/admin/expected-residents")
                        .header("X-User-Id", "100").header("X-User-Role", "MANAGER")
                        .header("X-Complex-Id", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-expected-resident-create",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 입주민 명부").summary("입주민 명부 등록").build())));
    }

    @Test
    void 입주민_명부_목록_조회() throws Exception {
        ExpectedResidentRes item = ExpectedResidentRes.builder()
                .expectedResidentId(1L).complexId(10L).householdId(1L)
                .building("101").unit("101").build();

        ExpectedResidentListRes res = ExpectedResidentListRes.builder()
                .content(List.of(item)).page(0).size(20).totalElements(1).totalPages(1).hasNext(false).build();

        given(expectedResidentService.getExpectedResidentList(any(), any())).willReturn(res);

        mockMvc.perform(get("/api/admin/expected-residents")
                        .header("X-User-Id", "100").header("X-User-Role", "MANAGER")
                        .header("X-Complex-Id", "10")
                        .param("page", "0").param("size", "20"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-expected-resident-list",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 입주민 명부").summary("입주민 명부 목록 조회").build())));
    }

    @Test
    void 입주민_명부_수정() throws Exception {
        Map<String, String> req = Map.of("name", "김철수", "phone", "010-9999-8888");

        ExpectedResidentRes res = ExpectedResidentRes.builder()
                .expectedResidentId(1L).complexId(10L).householdId(1L)
                .building("101").unit("101").build();

        given(expectedResidentService.updateExpectedResident(any(), eq(1L), any())).willReturn(res);

        mockMvc.perform(patch("/api/admin/expected-residents/{expectedResidentId}", 1L)
                        .header("X-User-Id", "100").header("X-User-Role", "MANAGER")
                        .header("X-Complex-Id", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-expected-resident-update",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 입주민 명부").summary("입주민 명부 수정").build())));
    }

    @Test
    void 입주민_명부_비활성화() throws Exception {
        ExpectedResidentRes res = ExpectedResidentRes.builder()
                .expectedResidentId(1L).complexId(10L).householdId(1L)
                .building("101").unit("101").build();

        given(expectedResidentService.disableExpectedResident(any(), eq(1L))).willReturn(res);

        mockMvc.perform(patch("/api/admin/expected-residents/{expectedResidentId}/disable", 1L)
                        .header("X-User-Id", "100").header("X-User-Role", "MANAGER")
                        .header("X-Complex-Id", "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-expected-resident-disable",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 입주민 명부").summary("입주민 명부 비활성화").build())));
    }
}
