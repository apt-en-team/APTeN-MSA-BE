package com.apten.parkingvehicle.docs;

import com.apten.parkingvehicle.application.controller.AdminVehiclePolicyController;
import com.apten.parkingvehicle.application.service.ParkingPolicyService;
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
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(controllers = AdminVehiclePolicyController.class)
class AdminVehiclePolicyControllerDocsTest extends ParkingDocsTestBase {

    @MockBean
    private ParkingPolicyService parkingPolicyService;

    @Test
    void 차량_정책_수정() throws Exception {
        Map<String, Object> req = Map.of("maxVehicles", 3);
        given(parkingPolicyService.updateVehiclePolicy(any(), any(), any(), any())).willReturn(null);

        mockMvc.perform(put("/api/admin/vehicle-policies")
                        .header("X-User-Role", "MANAGER").header("X-Complex-Id", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-vehicle-policy-update",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 차량 정책").summary("차량 정책 수정").build())));
    }

    @Test
    void 차량_정책_조회() throws Exception {
        given(parkingPolicyService.getVehiclePolicies(any(), any(), any())).willReturn(null);

        mockMvc.perform(get("/api/admin/vehicle-policies")
                        .header("X-User-Role", "MANAGER").header("X-Complex-Id", "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-vehicle-policy-get",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 차량 정책").summary("차량 정책 조회").build())));
    }
}
