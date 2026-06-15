package com.apten.facilityreservation.docs;

import com.apten.facilityreservation.application.controller.ResidentFacilityController;
import com.apten.facilityreservation.application.service.FacilityService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(controllers = ResidentFacilityController.class)
class ResidentFacilityControllerDocsTest extends FacilityDocsTestBase {

    @MockBean
    private FacilityService facilityService;

    @Test
    void 입주민_시설_목록_조회() throws Exception {
        given(facilityService.getResidentFacilityList(any(), any())).willReturn(null);
        mockMvc.perform(get("/api/facilities")
                        .header("X-User-Id", "100").header("X-User-Role", "USER").header("X-Complex-Id", "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("resident-facility-list",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Resident - 시설").summary("입주민 시설 목록 조회").build())));
    }

    @Test
    void 입주민_시설_상세_조회() throws Exception {
        given(facilityService.getResidentFacilityDetail(any(), any())).willReturn(null);
        mockMvc.perform(get("/api/facilities/{facilityId}", 1L)
                        .header("X-User-Id", "100").header("X-User-Role", "USER").header("X-Complex-Id", "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("resident-facility-detail",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Resident - 시설").summary("입주민 시설 상세 조회").build())));
    }

    @Test
    void 입주민_좌석_상태_조회() throws Exception {
        given(facilityService.getResidentSeatStatus(any(), any(), any())).willReturn(null);
        mockMvc.perform(get("/api/facilities/{facilityId}/seat-status", 1L)
                        .header("X-User-Id", "100").header("X-User-Role", "USER").header("X-Complex-Id", "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("resident-facility-seat-status",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Resident - 시설").summary("입주민 좌석 상태 조회").build())));
    }
}
