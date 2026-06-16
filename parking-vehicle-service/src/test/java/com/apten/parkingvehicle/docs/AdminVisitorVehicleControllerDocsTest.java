package com.apten.parkingvehicle.docs;

import com.apten.parkingvehicle.application.controller.AdminVisitorVehicleController;
import com.apten.parkingvehicle.application.service.VisitorVehicleService;
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
@WebMvcTest(controllers = AdminVisitorVehicleController.class)
class AdminVisitorVehicleControllerDocsTest extends ParkingDocsTestBase {

    @MockBean
    private VisitorVehicleService visitorVehicleService;

    @Test
    void 관리자_방문차량_등록() throws Exception {
        Map<String, Object> req = Map.of("licensePlate", "12가3456", "householdId", 1L);
        given(visitorVehicleService.createAdminVisitorVehicle(any(), any(), any(), any(), any())).willReturn(null);

        mockMvc.perform(post("/api/admin/visitor-vehicles")
                        .header("X-User-Id", "100").header("X-User-Role", "MANAGER").header("X-Complex-Id", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-visitor-vehicle-create",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 방문차량").summary("관리자 방문차량 등록").build())));
    }

    @Test
    void 관리자_방문차량_목록_조회() throws Exception {
        given(visitorVehicleService.getAdminVisitorVehicleList(any(), any(), any(), any())).willReturn(null);

        mockMvc.perform(get("/api/admin/visitor-vehicles")
                        .header("X-User-Role", "MANAGER").header("X-Complex-Id", "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-visitor-vehicle-list",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 방문차량").summary("관리자 방문차량 목록 조회").build())));
    }

    @Test
    void 관리자_방문차량_통계_조회() throws Exception {
        given(visitorVehicleService.getAdminVisitorVehicleStats(any(), any(), any())).willReturn(null);

        mockMvc.perform(get("/api/admin/visitor-vehicle-stats")
                        .header("X-User-Role", "MANAGER").header("X-Complex-Id", "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-visitor-vehicle-stats",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 방문차량").summary("관리자 방문차량 통계 조회").build())));
    }

    @Test
    void 관리자_방문차량_상세_조회() throws Exception {
        given(visitorVehicleService.getAdminVisitorVehicleDetail(any(), any(), any(), any())).willReturn(null);

        mockMvc.perform(get("/api/admin/visitor-vehicles/{visitorVehicleId}", 1L)
                        .header("X-User-Role", "MANAGER").header("X-Complex-Id", "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-visitor-vehicle-detail",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 방문차량").summary("관리자 방문차량 상세 조회").build())));
    }
}
