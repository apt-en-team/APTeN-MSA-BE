package com.apten.parkingvehicle.docs;

import com.apten.parkingvehicle.application.controller.AdminVisitorFeeController;
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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(controllers = AdminVisitorFeeController.class)
class AdminVisitorFeeControllerDocsTest extends ParkingDocsTestBase {

    @MockBean
    private ParkingFeeCalculationService parkingFeeCalculationService;

    @Test
    void 방문차량_월별_요금_목록_조회() throws Exception {
        given(parkingFeeCalculationService.listAdminVisitorMonthlyFees(any(), anyInt(), anyInt(), any(), any(), any())).willReturn(null);

        mockMvc.perform(get("/api/admin/visitor-vehicles/fees/monthly")
                        .header("X-User-Role", "MANAGER").header("X-Complex-Id", "10")
                        .param("yearMonth", "2026-01"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-visitor-fee-monthly",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 방문차량 요금").summary("방문차량 월별 요금 목록 조회").build())));
    }
}
