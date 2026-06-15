package com.apten.parkingvehicle.docs;

import com.apten.parkingvehicle.application.controller.VisitorVehicleController;
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
@WebMvcTest(controllers = VisitorVehicleController.class)
class VisitorVehicleControllerDocsTest extends ParkingDocsTestBase {

    @MockBean
    private VisitorVehicleService visitorVehicleService;

    @Test
    void 방문차량_등록() throws Exception {
        Map<String, Object> req = Map.of("licensePlate", "12가3456", "visitDate", "2026-01-10");
        given(visitorVehicleService.createVisitorVehicle(any(), any(), any(), any())).willReturn(null);

        mockMvc.perform(post("/api/visitor-vehicles")
                        .header("X-User-Id", "100").header("X-User-Role", "USER").header("X-Complex-Id", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andDo(MockMvcRestDocumentationWrapper.document("resident-visitor-vehicle-create",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Resident - 방문차량").summary("방문차량 등록").build())));
    }

    @Test
    void 내_방문차량_목록_조회() throws Exception {
        given(visitorVehicleService.getMyVisitorVehicleList(any(), any(), any(), any())).willReturn(null);

        mockMvc.perform(get("/api/visitor-vehicles")
                        .header("X-User-Id", "100").header("X-User-Role", "USER").header("X-Complex-Id", "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("resident-visitor-vehicle-list",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Resident - 방문차량").summary("내 방문차량 목록 조회").build())));
    }

    @Test
    void 방문차량_상세_조회() throws Exception {
        given(visitorVehicleService.getMyVisitorVehicleDetail(any(), any(), any(), any())).willReturn(null);

        mockMvc.perform(get("/api/visitor-vehicles/{visitorVehicleId}", 1L)
                        .header("X-User-Id", "100").header("X-User-Role", "USER").header("X-Complex-Id", "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("resident-visitor-vehicle-detail",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Resident - 방문차량").summary("방문차량 상세 조회").build())));
    }

    @Test
    void 방문차량_수정() throws Exception {
        Map<String, Object> req = Map.of("licensePlate", "34나5678");
        given(visitorVehicleService.updateVisitorVehicle(any(), any(), any(), any(), any())).willReturn(null);

        mockMvc.perform(patch("/api/visitor-vehicles/{visitorVehicleId}", 1L)
                        .header("X-User-Id", "100").header("X-User-Role", "USER").header("X-Complex-Id", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("resident-visitor-vehicle-update",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Resident - 방문차량").summary("방문차량 수정").build())));
    }

    @Test
    void 방문차량_취소() throws Exception {
        given(visitorVehicleService.cancelVisitorVehicle(any(), any(), any(), any())).willReturn(null);

        mockMvc.perform(patch("/api/visitor-vehicles/{visitorVehicleId}/cancel", 1L)
                        .header("X-User-Id", "100").header("X-User-Role", "USER").header("X-Complex-Id", "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("resident-visitor-vehicle-cancel",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Resident - 방문차량").summary("방문차량 취소").build())));
    }

    @Test
    void 방문차량_삭제() throws Exception {
        given(visitorVehicleService.deleteVisitorVehicle(any(), any(), any(), any())).willReturn(null);

        mockMvc.perform(delete("/api/visitor-vehicles/{visitorVehicleId}", 1L)
                        .header("X-User-Id", "100").header("X-User-Role", "USER").header("X-Complex-Id", "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("resident-visitor-vehicle-delete",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Resident - 방문차량").summary("방문차량 삭제").build())));
    }

    @Test
    void 방문차량_재등록() throws Exception {
        given(visitorVehicleService.reRegisterVisitorVehicle(any(), any(), any(), any(), any())).willReturn(null);

        Map<String, Object> reReq = Map.of("visitDate", "2026-07-01");
        mockMvc.perform(post("/api/visitor-vehicles/{visitorVehicleId}/re-register", 1L)
                        .header("X-User-Id", "100").header("X-User-Role", "USER").header("X-Complex-Id", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reReq)))
                .andExpect(status().isCreated())
                .andDo(MockMvcRestDocumentationWrapper.document("resident-visitor-vehicle-re-register",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Resident - 방문차량").summary("방문차량 재등록").build())));
    }
}
