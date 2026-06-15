package com.apten.parkingvehicle.docs;

import com.apten.parkingvehicle.application.controller.VehicleController;
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
@WebMvcTest(controllers = VehicleController.class)
class VehicleControllerDocsTest extends ParkingDocsTestBase {

    @MockBean
    private VehicleService vehicleService;

    @Test
    void 차량_등록_신청() throws Exception {
        Map<String, Object> req = Map.of("licensePlate", "12가3456", "vehicleType", "일반 승용차");
        given(vehicleService.createVehicle(any(), any(), any(), any())).willReturn(null);

        mockMvc.perform(post("/api/vehicles")
                        .header("X-User-Id", "100").header("X-User-Role", "USER").header("X-Complex-Id", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andDo(MockMvcRestDocumentationWrapper.document("resident-vehicle-create",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Resident - 차량").summary("차량 등록 신청").build())));
    }

    @Test
    void 차량_수정() throws Exception {
        Map<String, Object> req = Map.of("licensePlate", "34나5678");
        given(vehicleService.updateVehicle(any(), any(), any(), any(), any())).willReturn(null);

        mockMvc.perform(patch("/api/vehicles/{vehicleId}", 1L)
                        .header("X-User-Id", "100").header("X-User-Role", "USER").header("X-Complex-Id", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("resident-vehicle-update",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Resident - 차량").summary("차량 수정").build())));
    }

    @Test
    void 차량_삭제() throws Exception {
        given(vehicleService.deleteVehicle(any(), any(), any(), any())).willReturn(null);

        mockMvc.perform(delete("/api/vehicles/{vehicleId}", 1L)
                        .header("X-User-Id", "100").header("X-User-Role", "USER").header("X-Complex-Id", "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("resident-vehicle-delete",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Resident - 차량").summary("차량 삭제").build())));
    }

    @Test
    void 내_차량_목록_조회() throws Exception {
        given(vehicleService.getMyVehicleList(any(), any(), any(), any())).willReturn(null);

        mockMvc.perform(get("/api/vehicles")
                        .header("X-User-Id", "100").header("X-User-Role", "USER").header("X-Complex-Id", "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("resident-vehicle-list",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Resident - 차량").summary("내 차량 목록 조회").build())));
    }

    @Test
    void 내_차량_상세_조회() throws Exception {
        given(vehicleService.getMyVehicleDetail(any(), any(), any(), any())).willReturn(null);

        mockMvc.perform(get("/api/vehicles/{vehicleId}", 1L)
                        .header("X-User-Id", "100").header("X-User-Role", "USER").header("X-Complex-Id", "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("resident-vehicle-detail",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Resident - 차량").summary("내 차량 상세 조회").build())));
    }

    @Test
    void 차량번호_중복_확인() throws Exception {
        given(vehicleService.checkDuplicateLicensePlate(any(), any(), any(), any())).willReturn(null);

        mockMvc.perform(get("/api/vehicles/check-license-plate")
                        .param("licensePlate", "12가3456")
                        .header("X-User-Id", "100").header("X-User-Role", "USER").header("X-Complex-Id", "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("resident-vehicle-license-check",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Resident - 차량").summary("차량번호 중복 확인").build())));
    }
}
