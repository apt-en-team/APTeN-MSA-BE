package com.apten.parkingvehicle.docs;

import com.apten.parkingvehicle.application.controller.SensorMockController;
import com.apten.parkingvehicle.application.service.SensorMockService;
import com.apten.parkingvehicle.domain.enums.SensorStatus;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(controllers = SensorMockController.class)
class SensorMockControllerDocsTest extends ParkingDocsTestBase {

    @MockBean
    private SensorMockService sensorMockService;

    @Test
    void 센서_초기화() throws Exception {
        Map<String, Object> req = Map.of("sensorCode", "SENSOR-001", "zoneId", 1L, "spotNumber", "A-01", "complexId", 10L);
        willDoNothing().given(sensorMockService).initSensor(any());

        mockMvc.perform(post("/api/admin/parking/sensors/mock/init")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("sensor-mock-init",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - Mock 센서").summary("Mock 센서 단건 초기화").build())));
    }

    @Test
    void 센서_일괄_초기화() throws Exception {
        Map<String, Object> req = Map.of("complexId", 10L, "zoneId", 1L, "spotCount", 5);
        willDoNothing().given(sensorMockService).initSensorBulk(any());

        mockMvc.perform(post("/api/admin/parking/sensors/mock/bulk-init")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("sensor-mock-bulk-init",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - Mock 센서").summary("Mock 센서 일괄 초기화").build())));
    }

    @Test
    void 센서_상태_토글() throws Exception {
        given(sensorMockService.toggleSensor(anyString())).willReturn(SensorStatus.OCCUPIED);

        mockMvc.perform(post("/api/admin/parking/sensors/mock/{sensorCode}/toggle", "SENSOR-001"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("sensor-mock-toggle",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - Mock 센서").summary("Mock 센서 상태 토글").build())));
    }

    @Test
    void 센서_Hash_조회() throws Exception {
        given(sensorMockService.getSensor(anyString())).willReturn(Map.of("status", "VACANT", "spotNumber", "A-01"));

        mockMvc.perform(get("/api/admin/parking/sensors/mock/{sensorCode}", "SENSOR-001"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("sensor-mock-get",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - Mock 센서").summary("Mock 센서 Hash 조회").build())));
    }
}
