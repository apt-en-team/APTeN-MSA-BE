package com.apten.household.docs;

import com.apten.household.application.controller.AdminBillPolicyController;
import com.apten.household.application.model.response.ComplexPolicyPutRes;
import com.apten.household.application.service.ComplexPolicyService;
import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceDocumentation;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;

import java.math.BigDecimal;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// 관리비 정책 API 문서화 테스트
@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(controllers = AdminBillPolicyController.class)
class AdminBillPolicyControllerDocsTest extends HouseholdDocsTestBase {

    @MockBean
    private ComplexPolicyService complexPolicyService;


    @Test
    void 관리비_정책_조회() throws Exception {
        ComplexPolicyPutRes res = ComplexPolicyPutRes.builder()
                .complexId(10L).baseFee(BigDecimal.valueOf(50000))
                .dueDay(15).sendDay(1).homeDisplayEndDay(20).build();

        given(complexPolicyService.getComplexPolicy(any())).willReturn(res);

        mockMvc.perform(get("/api/admin/household-bill-policies/basic")
                        .header("X-User-Id", "100")
                        .header("X-User-Role", "MANAGER")
                        .header("X-Complex-Id", "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-bill-policy-get",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 관리비 정책").summary("관리비 정책 조회").build())));
    }

    @Test
    void 관리비_정책_수정() throws Exception {
        Map<String, Object> req = Map.of("baseFee", 60000, "dueDay", 20, "sendDay", 1, "homeDisplayEndDay", 25);

        ComplexPolicyPutRes res = ComplexPolicyPutRes.builder()
                .complexId(10L).baseFee(BigDecimal.valueOf(60000))
                .dueDay(20).sendDay(1).homeDisplayEndDay(25).build();

        given(complexPolicyService.updateComplexPolicy(any(), any())).willReturn(res);

        mockMvc.perform(put("/api/admin/household-bill-policies/basic")
                        .header("X-User-Id", "100")
                        .header("X-User-Role", "MANAGER")
                        .header("X-Complex-Id", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-bill-policy-update",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 관리비 정책").summary("관리비 정책 수정").build())));
    }
}
