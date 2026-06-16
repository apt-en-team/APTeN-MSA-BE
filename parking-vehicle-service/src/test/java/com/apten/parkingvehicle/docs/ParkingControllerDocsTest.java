package com.apten.parkingvehicle.docs;

import com.apten.parkingvehicle.application.controller.ParkingController;
import com.apten.parkingvehicle.application.service.ParkingService;
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
@WebMvcTest(controllers = ParkingController.class)
class ParkingControllerDocsTest extends ParkingDocsTestBase {

    @MockBean
    private ParkingService parkingService;

    @Test
    void 입출차_기록_조회() throws Exception {
        given(parkingService.getParkingLogList(any(), any(), any(), any())).willReturn(null);

        mockMvc.perform(get("/api/admin/parking-logs")
                        .header("X-User-Role", "MANAGER").header("X-Complex-Id", "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-parking-log-list",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 주차 관리").summary("입출차 기록 조회").build())));
    }

    @Test
    void 입출차_기록_통계_요약_조회() throws Exception {
        given(parkingService.getParkingLogSummary(any(), any(), any())).willReturn(null);

        mockMvc.perform(get("/api/admin/parking-logs/summary")
                        .header("X-User-Role", "MANAGER").header("X-Complex-Id", "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-parking-log-summary",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 주차 관리").summary("입출차 기록 통계 요약 조회").build())));
    }

    @Test
    void 주차_현황_조회() throws Exception {
        given(parkingService.getParkingStatus(any(), any(), any())).willReturn(null);

        mockMvc.perform(get("/api/admin/parking/status")
                        .header("X-User-Role", "MANAGER").header("X-Complex-Id", "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-parking-status",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 주차 관리").summary("주차 현황 조회").build())));
    }

    @Test
    void 주차_구역_목록_조회() throws Exception {
        given(parkingService.getParkingZoneList(any(), any(), any(), any())).willReturn(null);

        mockMvc.perform(get("/api/admin/parking/zones")
                        .header("X-User-Role", "MANAGER").header("X-Complex-Id", "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-parking-zone-list",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 주차 관리").summary("주차 구역 목록 조회").build())));
    }

    @Test
    void 주차_구역_등록() throws Exception {
        Map<String, Object> req = Map.of("zoneName", "A구역", "capacity", 50);
        given(parkingService.createParkingZone(any(), any(), any(), any())).willReturn(null);

        mockMvc.perform(post("/api/admin/parking/zones")
                        .header("X-User-Role", "MANAGER").header("X-Complex-Id", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-parking-zone-create",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 주차 관리").summary("주차 구역 등록").build())));
    }

    @Test
    void 주차_구역_수정() throws Exception {
        Map<String, Object> req = Map.of("capacity", 60);
        given(parkingService.updateParkingZone(any(), any(), any(), any(), any())).willReturn(null);

        mockMvc.perform(patch("/api/admin/parking/zones/{zoneId}", 1L)
                        .header("X-User-Role", "MANAGER").header("X-Complex-Id", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-parking-zone-update",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 주차 관리").summary("주차 구역 수정").build())));
    }

    @Test
    void 주차_구역_삭제() throws Exception {
        willDoNothing().given(parkingService).deleteParkingZone(any(), any(), any(), any());

        mockMvc.perform(delete("/api/admin/parking/zones/{zoneId}", 1L)
                        .header("X-User-Role", "MANAGER").header("X-Complex-Id", "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-parking-zone-delete",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 주차 관리").summary("주차 구역 삭제").build())));
    }

    @Test
    void 주차_통계_조회() throws Exception {
        given(parkingService.getParkingStatistics(any(), any(), any(), any())).willReturn(null);

        mockMvc.perform(get("/api/admin/parking/statistics")
                        .header("X-User-Role", "MANAGER").header("X-Complex-Id", "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-parking-statistics",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 주차 관리").summary("주차 통계 조회").build())));
    }

    @Test
    void 입출차_등록() throws Exception {
        Map<String, Object> req = Map.of("licensePlate", "12가3456", "type", "IN");
        given(parkingService.createParkingLog(any(), any(), any(), any())).willReturn(null);

        mockMvc.perform(post("/api/admin/parking-logs")
                        .header("X-User-Role", "MANAGER").header("X-Complex-Id", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-parking-log-create",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 주차 관리").summary("입출차 등록").build())));
    }

    @Test
    void 입주민_주차_현황_조회() throws Exception {
        given(parkingService.getResidentParkingStatus(any(), any())).willReturn(null);

        mockMvc.perform(get("/api/parking/status")
                        .header("X-User-Role", "USER").header("X-Complex-Id", "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("resident-parking-status",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Resident - 주차 현황").summary("입주민 주차 현황 조회").build())));
    }

    @Test
    void 입주민_자리_목록_조회() throws Exception {
        given(parkingService.getResidentParkingSpots(any(), any(), any())).willReturn(null);

        mockMvc.perform(get("/api/parking/zones/{zoneId}/spots", 1L)
                        .header("X-User-Role", "USER").header("X-Complex-Id", "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("resident-parking-spots",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Resident - 주차 현황").summary("입주민 자리 목록 조회").build())));
    }
}
