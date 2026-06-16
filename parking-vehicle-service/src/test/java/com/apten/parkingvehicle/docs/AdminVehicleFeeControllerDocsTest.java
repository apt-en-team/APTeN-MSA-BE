package com.apten.parkingvehicle.docs;

import com.apten.parkingvehicle.application.controller.AdminVehicleFeeController;
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
@WebMvcTest(controllers = AdminVehicleFeeController.class)
class AdminVehicleFeeControllerDocsTest extends ParkingDocsTestBase {

    @MockBean
    private ParkingFeeCalculationService parkingFeeCalculationService;

    @Test
    void 차량_월별_요금_목록_조회() throws Exception {
        given(parkingFeeCalculationService.listAdminVehicleMonthlyFees(any(), anyInt(), anyInt(), any(), any(), any())).willReturn(null);

        mockMvc.perform(get("/api/admin/vehicles/fees/monthly")
                        .header("X-User-Role", "MANAGER").header("X-Complex-Id", "10")
                        .param("yearMonth", "2026-01"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-vehicle-fee-monthly",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 차량 요금").summary("차량 월별 요금 목록 조회").build())));
    }
}
