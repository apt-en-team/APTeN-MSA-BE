package com.apten.parkingvehicle.docs;

import com.apten.parkingvehicle.application.controller.AdminVehicleController;
import com.apten.parkingvehicle.application.service.VehicleService;
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
@WebMvcTest(controllers = AdminVehicleController.class)
class AdminVehicleControllerDocsTest extends ParkingDocsTestBase {

    @MockBean
    private VehicleService vehicleService;

    @Test
    void 차량_승인() throws Exception {
        given(vehicleService.approveVehicle(any(), any(), any(), any())).willReturn(null);

        mockMvc.perform(patch("/api/admin/vehicles/{vehicleId}/approve", 1L)
                        .header("X-User-Role", "MANAGER").header("X-Complex-Id", "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-vehicle-approve",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 차량").summary("차량 승인").build())));
    }

    @Test
    void 차량_거절() throws Exception {
        Map<String, String> req = Map.of("reason", "정보 불일치");
        given(vehicleService.rejectVehicle(any(), any(), any(), any(), any())).willReturn(null);

        mockMvc.perform(patch("/api/admin/vehicles/{vehicleId}/reject", 1L)
                        .header("X-User-Role", "MANAGER").header("X-Complex-Id", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-vehicle-reject",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 차량").summary("차량 거절").build())));
    }

    @Test
    void 차량_목록_조회() throws Exception {
        given(vehicleService.getAdminVehicleList(any(), any(), any(), any())).willReturn(null);

        mockMvc.perform(get("/api/admin/vehicles")
                        .header("X-User-Role", "MANAGER").header("X-Complex-Id", "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-vehicle-list",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 차량").summary("차량 목록 조회").build())));
    }
}
