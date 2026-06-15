package com.apten.parkingvehicle.docs;

import com.apten.parkingvehicle.application.controller.VisitorFeeController;
import com.apten.parkingvehicle.application.service.ParkingFeeCalculationService;
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
@WebMvcTest(controllers = VisitorFeeController.class)
class VisitorFeeControllerDocsTest extends ParkingDocsTestBase {

    @MockBean
    private ParkingFeeCalculationService parkingFeeCalculationService;

    @Test
    void 방문차량_월별_요금_조회() throws Exception {
        given(parkingFeeCalculationService.getResidentVisitorMonthlyFee(any(), any(), any(), any())).willReturn(null);

        mockMvc.perform(get("/api/visitor-vehicles/fees/monthly")
                        .header("X-User-Id", "100").header("X-User-Role", "USER").header("X-Complex-Id", "10")
                        .param("yearMonth", "2026-01"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("resident-visitor-fee-monthly",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Resident - 방문차량 요금").summary("방문차량 월별 요금 조회").build())));
    }
}
