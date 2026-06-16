package com.apten.facilityreservation.docs;

import com.apten.facilityreservation.application.controller.AdminFacilityPolicyController;
import com.apten.facilityreservation.application.service.FacilityPolicyService;
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
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(controllers = AdminFacilityPolicyController.class)
class AdminFacilityPolicyControllerDocsTest extends FacilityDocsTestBase {

    @MockBean
    private FacilityPolicyService facilityPolicyService;

    @Test
    void 시설_예약_정책_설정() throws Exception {
        given(facilityPolicyService.updateFacilityPolicy(any(), any())).willReturn(null);
        mockMvc.perform(put("/api/admin/facility-policies")
                        .header("X-User-Id", "100").header("X-User-Role", "MANAGER").header("X-Complex-Id", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("maxReservationDays", 7))))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-facility-policy-update",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 시설 정책").summary("시설 예약 정책 설정").build())));
    }

    @Test
    void 시설_예약_정책_조회() throws Exception {
        given(facilityPolicyService.getFacilityPolicyList(any(), any())).willReturn(null);
        mockMvc.perform(get("/api/admin/facility-policies")
                        .header("X-User-Id", "100").header("X-User-Role", "MANAGER").header("X-Complex-Id", "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-facility-policy-list",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 시설 정책").summary("시설 예약 정책 조회").build())));
    }
}
