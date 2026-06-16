package com.apten.parkingvehicle.docs;

import com.apten.parkingvehicle.application.controller.InternalParkingFeeController;
import com.apten.parkingvehicle.application.service.ParkingFeeCalculationService;
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
@WebMvcTest(controllers = InternalParkingFeeController.class)
class InternalParkingFeeControllerDocsTest extends ParkingDocsTestBase {

    @MockBean
    private ParkingFeeCalculationService parkingFeeCalculationService;

    @Test
    void 차량_비용_산정() throws Exception {
        Map<String, Object> req = Map.of("complexId", 10L, "billYear", 2026, "billMonth", 1);
        given(parkingFeeCalculationService.calculateVehicleFees(any())).willReturn(null);

        mockMvc.perform(post("/internal/vehicle-fees/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("internal-vehicle-fee-calculate",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Internal - 주차 요금").summary("차량 비용 산정").build())));
    }

    @Test
    void 방문차량_비용_산정() throws Exception {
        Map<String, Object> req = Map.of("complexId", 10L, "billYear", 2026, "billMonth", 1);
        given(parkingFeeCalculationService.calculateVisitorFees(any())).willReturn(null);

        mockMvc.perform(post("/internal/visitor-fees/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("internal-visitor-fee-calculate",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Internal - 주차 요금").summary("방문차량 비용 산정").build())));
    }
}
