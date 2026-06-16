package com.apten.parkingvehicle.docs;

import com.apten.parkingvehicle.application.controller.AdminRegularVisitorVehicleController;
import com.apten.parkingvehicle.application.service.RegularVisitorVehicleService;
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
@WebMvcTest(controllers = AdminRegularVisitorVehicleController.class)
class AdminRegularVisitorVehicleControllerDocsTest extends ParkingDocsTestBase {

    @MockBean
    private RegularVisitorVehicleService regularVisitorVehicleService;

    @Test
    void 관리자_정기방문차량_목록_조회() throws Exception {
        given(regularVisitorVehicleService.getAdminRegularVisitorVehicleList(any(), any(), any(), any())).willReturn(null);

        mockMvc.perform(get("/api/admin/regular-visitor-vehicles")
                        .header("X-User-Role", "MANAGER").header("X-Complex-Id", "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-regular-visitor-vehicle-list",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 정기방문차량").summary("관리자 정기방문차량 목록 조회").build())));
    }

    @Test
    void 관리자_정기방문차량_등록() throws Exception {
        Map<String, Object> req = Map.of("licensePlate", "12가3456", "householdId", 1L);
        given(regularVisitorVehicleService.createAdminRegularVisitorVehicle(any(), any(), any(), any(), any())).willReturn(null);

        mockMvc.perform(post("/api/admin/regular-visitor-vehicles")
                        .header("X-User-Id", "100").header("X-User-Role", "MANAGER").header("X-Complex-Id", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-regular-visitor-vehicle-create",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 정기방문차량").summary("관리자 정기방문차량 등록").build())));
    }

    @Test
    void 관리자_정기방문차량_삭제() throws Exception {
        given(regularVisitorVehicleService.deleteRegularVisitorVehicleByAdmin(any(), any(), any(), any(), any())).willReturn(null);

        mockMvc.perform(delete("/api/admin/regular-visitor-vehicles/{regularVisitorVehicleId}", 1L)
                        .header("X-User-Id", "100").header("X-User-Role", "MANAGER").header("X-Complex-Id", "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-regular-visitor-vehicle-delete",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 정기방문차량").summary("관리자 정기방문차량 삭제").build())));
    }
}
