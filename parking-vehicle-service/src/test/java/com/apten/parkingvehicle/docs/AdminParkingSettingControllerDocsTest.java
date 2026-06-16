package com.apten.parkingvehicle.docs;

import com.apten.parkingvehicle.application.controller.AdminParkingSettingController;
import com.apten.parkingvehicle.application.service.ParkingSettingService;
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
@WebMvcTest(controllers = AdminParkingSettingController.class)
class AdminParkingSettingControllerDocsTest extends ParkingDocsTestBase {

    @MockBean
    private ParkingSettingService parkingSettingService;

    @Test
    void 주차_설정_조회() throws Exception {
        given(parkingSettingService.getParkingSetting(any(), any(), any())).willReturn(null);

        mockMvc.perform(get("/api/admin/parking/setting")
                        .header("X-User-Role", "MANAGER").header("X-Complex-Id", "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-parking-setting-get",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 주차 설정").summary("주차 설정 조회").build())));
    }

    @Test
    void 주차_설정_수정() throws Exception {
        Map<String, Object> req = Map.of("maxVehiclesPerHousehold", 2);
        given(parkingSettingService.updateParkingSetting(any(), any(), any(), any())).willReturn(null);

        mockMvc.perform(patch("/api/admin/parking/setting")
                        .header("X-User-Role", "MANAGER").header("X-Complex-Id", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-parking-setting-update",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 주차 설정").summary("주차 설정 수정").build())));
    }
}
