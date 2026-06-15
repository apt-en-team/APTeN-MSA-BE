package com.apten.household.docs;

import com.apten.household.application.controller.InternalHouseholdController;
import com.apten.household.application.model.request.HouseholdMatchPostReq;
import com.apten.household.application.model.response.*;
import com.apten.household.application.service.HouseholdBillService;
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
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// 내부 세대 연동 API 문서화 테스트
@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(controllers = InternalHouseholdController.class)
class InternalHouseholdControllerDocsTest extends HouseholdDocsTestBase {

    @MockBean
    private HouseholdMatchService householdMatchService;

    @MockBean
    private HouseholdBillService householdBillService;

    @Test
    void 세대_매칭_요청_생성() throws Exception {
        Map<String, Object> req = Map.of("userId", 200L, "complexId", 10L, "building", "101", "unit", "101");

        HouseholdMatchPostRes res = HouseholdMatchPostRes.builder()
                .matchRequestId(1L).matchedHouseholdId(100L)
                .processType("AUTO").matchStatus("APPROVED")
                .createdAt(LocalDateTime.of(2026, 1, 5, 10, 0)).build();

        given(householdMatchService.createMatchRequest(any(HouseholdMatchPostReq.class))).willReturn(res);

        mockMvc.perform(post("/internal/household-match-requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andDo(MockMvcRestDocumentationWrapper.document("internal-household-match-create",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Internal - 세대").summary("세대 매칭 요청 생성").build())));
    }

    @Test
    void 차량_비용_반영() throws Exception {
        Map<String, Object> req = Map.of("complexId", 10L, "billYear", 2026, "billMonth", 1,
                "householdId", 100L, "vehicleFee", 30000);

        VehicleFeeReflectRes res = VehicleFeeReflectRes.builder()
                .complexId(10L).billYear(2026).billMonth(1)
                .affectedHouseholdCount(1).reflectedAt(LocalDateTime.of(2026, 1, 5, 10, 0)).build();

        given(householdBillService.reflectVehicleFee(any())).willReturn(res);

        mockMvc.perform(post("/internal/household-bills/vehicle-fees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("internal-vehicle-fee-reflect",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Internal - 세대").summary("차량 비용 반영").build())));
    }

    @Test
    void 시설_비용_반영() throws Exception {
        Map<String, Object> req = Map.of("complexId", 10L, "billYear", 2026, "billMonth", 1,
                "householdId", 100L, "facilityFee", 20000);

        FacilityFeeReflectRes res = FacilityFeeReflectRes.builder()
                .complexId(10L).billYear(2026).billMonth(1)
                .affectedHouseholdCount(1).reflectedAt(LocalDateTime.of(2026, 1, 5, 10, 0)).build();

        given(householdBillService.reflectFacilityFee(any())).willReturn(res);

        mockMvc.perform(post("/internal/household-bills/facility-fees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("internal-facility-fee-reflect",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Internal - 세대").summary("시설 비용 반영").build())));
    }

    @Test
    void 방문차량_비용_반영() throws Exception {
        Map<String, Object> req = Map.of("complexId", 10L, "billYear", 2026, "billMonth", 1,
                "householdId", 100L, "visitorFee", 5000);

        VisitorFeeReflectRes res = VisitorFeeReflectRes.builder()
                .complexId(10L).billYear(2026).billMonth(1)
                .affectedHouseholdCount(1).build();

        given(householdBillService.reflectVisitorFee(any())).willReturn(res);

        mockMvc.perform(post("/internal/household-bills/visitor-fees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("internal-visitor-fee-reflect",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Internal - 세대").summary("방문차량 비용 반영").build())));
    }
}
