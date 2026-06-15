package com.apten.facilityreservation.docs;

import com.apten.facilityreservation.application.controller.InternalFacilityFeeController;
import com.apten.facilityreservation.application.service.FacilityFeeService;
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
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(controllers = InternalFacilityFeeController.class)
class InternalFacilityFeeControllerDocsTest extends FacilityDocsTestBase {

    @MockBean
    private FacilityFeeService facilityFeeService;

    @Test
    void 시설_이용_비용_산정() throws Exception {
        given(facilityFeeService.calculateFacilityFees(any())).willReturn(null);
        mockMvc.perform(post("/internal/facility-fees/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("complexId", 10L, "billYear", 2026, "billMonth", 1))))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("internal-facility-fee-calculate",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Internal - 시설 비용").summary("시설 이용 비용 산정").build())));
    }

    @Test
    void 시설_이용_비용_발행() throws Exception {
        given(facilityFeeService.publishFacilityFees(any())).willReturn(null);
        mockMvc.perform(post("/internal/facility-fees/publish")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("complexId", 10L, "billYear", 2026, "billMonth", 1))))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("internal-facility-fee-publish",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Internal - 시설 비용").summary("시설 이용 비용 발행").build())));
    }
}
