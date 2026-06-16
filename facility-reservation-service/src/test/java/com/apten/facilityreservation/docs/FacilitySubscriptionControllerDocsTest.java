package com.apten.facilityreservation.docs;

import com.apten.facilityreservation.application.controller.FacilitySubscriptionController;
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
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(controllers = FacilitySubscriptionController.class)
class FacilitySubscriptionControllerDocsTest extends FacilityDocsTestBase {

    @MockBean
    private FacilitySubscriptionService facilitySubscriptionService;

    @Test
    void 내_시설_구독_목록_조회() throws Exception {
        given(facilitySubscriptionService.getMySubscriptions(any(), any())).willReturn(null);
        mockMvc.perform(get("/api/facility-subscriptions")
                        .header("X-User-Id", "100").header("X-User-Role", "USER").header("X-Complex-Id", "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("resident-subscription-list",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Resident - 시설 구독").summary("내 시설 구독 목록 조회").build())));
    }

    @Test
    void 시설_구독_해지() throws Exception {
        given(facilitySubscriptionService.cancelSubscription(any(), any(), any())).willReturn(null);
        mockMvc.perform(post("/api/facility-subscriptions/{facilityId}/cancel", 1L)
                        .header("X-User-Id", "100").header("X-User-Role", "USER").header("X-Complex-Id", "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("resident-subscription-cancel",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Resident - 시설 구독").summary("시설 구독 해지").build())));
    }
}
