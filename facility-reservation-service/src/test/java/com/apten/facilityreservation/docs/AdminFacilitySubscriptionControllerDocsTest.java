package com.apten.facilityreservation.docs;

import com.apten.facilityreservation.application.controller.AdminFacilitySubscriptionController;
import com.apten.facilityreservation.application.service.FacilitySubscriptionService;
import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceDocumentation;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.RestDocumentationExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(controllers = AdminFacilitySubscriptionController.class)
class AdminFacilitySubscriptionControllerDocsTest extends FacilityDocsTestBase {

    @MockBean
    private FacilitySubscriptionService facilitySubscriptionService;

    @Test
    void 관리자_구독_목록_조회() throws Exception {
        given(facilitySubscriptionService.getAdminSubscriptionList(any(), any(), any())).willReturn(null);
        mockMvc.perform(get("/api/admin/facility-subscriptions")
                        .header("X-User-Id", "100").header("X-User-Role", "MANAGER").header("X-Complex-Id", "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-subscription-list",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 시설 구독").summary("관리자 구독 목록 조회").build())));
    }

    @Test
    void 관리자_세대별_구독_요약_목록_조회() throws Exception {
        given(facilitySubscriptionService.getHouseholdSubscriptionList(any())).willReturn(null);
        mockMvc.perform(get("/api/admin/facility-subscriptions/households")
                        .header("X-User-Id", "100").header("X-User-Role", "MANAGER").header("X-Complex-Id", "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-subscription-household-list",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 시설 구독").summary("세대별 구독 요약 목록 조회").build())));
    }

    @Test
    void 관리자_세대별_구독_상세_조회() throws Exception {
        given(facilitySubscriptionService.getHouseholdSubscriptionDetail(any(), any())).willReturn(null);
        mockMvc.perform(get("/api/admin/facility-subscriptions/households/{householdId}", 1L)
                        .header("X-User-Id", "100").header("X-User-Role", "MANAGER").header("X-Complex-Id", "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-subscription-household-detail",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 시설 구독").summary("세대별 구독 상세 조회").build())));
    }

    @Test
    void 관리자_구독_강제_해지() throws Exception {
        willDoNothing().given(facilitySubscriptionService).adminCancelSubscription(any(), any());
        mockMvc.perform(delete("/api/admin/facility-subscriptions/{subscriptionId}", 1L)
                        .header("X-User-Id", "100").header("X-User-Role", "MANAGER").header("X-Complex-Id", "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-subscription-cancel",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 시설 구독").summary("관리자 구독 강제 해지").build())));
    }
}
