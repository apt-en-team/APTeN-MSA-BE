package com.apten.parkingvehicle.docs;

import com.apten.parkingvehicle.application.controller.AdminParkingSensorController;
import com.apten.parkingvehicle.application.service.ParkingSensorService;
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
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(controllers = AdminParkingSensorController.class)
class AdminParkingSensorControllerDocsTest extends ParkingDocsTestBase {

    @MockBean
    private ParkingSensorService parkingSensorService;

    @Test
    void 센서_등록() throws Exception {
        Map<String, Object> req = Map.of("sensorCode", "SENSOR-001", "zoneId", 1L);
        given(parkingSensorService.createSensor(any(), any(), any(), any())).willReturn(null);

        mockMvc.perform(post("/api/admin/parking/sensors")
                        .header("X-User-Role", "MANAGER").header("X-Complex-Id", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-sensor-create",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 센서").summary("센서 등록").build())));
    }

    @Test
    void 센서_일괄_등록() throws Exception {
        Map<String, Object> req = Map.of("zoneId", 1L, "count", 10);
        given(parkingSensorService.createSensorsBulk(any(), any(), any(), any())).willReturn(null);

        mockMvc.perform(post("/api/admin/parking/sensors/bulk")
                        .header("X-User-Role", "MANAGER").header("X-Complex-Id", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-sensor-bulk-create",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 센서").summary("센서 일괄 등록").build())));
    }

    @Test
    void 센서_목록_조회() throws Exception {
        given(parkingSensorService.getSensorList(any(), any(), any(), any())).willReturn(null);

        mockMvc.perform(get("/api/admin/parking/sensors")
                        .header("X-User-Role", "MANAGER").header("X-Complex-Id", "10")
                        .param("zoneId", "1"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-sensor-list",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 센서").summary("센서 목록 조회").build())));
    }

    @Test
    void 센서_단건_조회() throws Exception {
        given(parkingSensorService.getSensor(any(), any(), any(), any())).willReturn(null);

        mockMvc.perform(get("/api/admin/parking/sensors/{sensorId}", 1L)
                        .header("X-User-Role", "MANAGER").header("X-Complex-Id", "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-sensor-get",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 센서").summary("센서 단건 조회").build())));
    }

    @Test
    void 센서_수정() throws Exception {
        Map<String, Object> req = Map.of("spotId", 5L);
        given(parkingSensorService.updateSensor(any(), any(), any(), any(), any())).willReturn(null);

        mockMvc.perform(patch("/api/admin/parking/sensors/{sensorId}", 1L)
                        .header("X-User-Role", "MANAGER").header("X-Complex-Id", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-sensor-update",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 센서").summary("센서 수정").build())));
    }

    @Test
    void 센서_삭제() throws Exception {
        willDoNothing().given(parkingSensorService).deleteSensor(any(), any(), any(), any());

        mockMvc.perform(delete("/api/admin/parking/sensors/{sensorId}", 1L)
                        .header("X-User-Role", "MANAGER").header("X-Complex-Id", "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-sensor-delete",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 센서").summary("센서 삭제").build())));
    }
}
