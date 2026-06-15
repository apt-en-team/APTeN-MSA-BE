package com.apten.parkingvehicle.docs;

import com.apten.parkingvehicle.application.controller.InternalParkingLogController;
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
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(controllers = InternalParkingLogController.class)
class InternalParkingLogControllerDocsTest extends ParkingDocsTestBase {

    @MockBean
    private ParkingService parkingService;

    @Test
    void 내부_입출차_등록() throws Exception {
        Map<String, Object> req = Map.of("licensePlate", "12가3456", "type", "IN", "complexId", 10L);
        given(parkingService.createParkingLogByGateRecognition(any())).willReturn(null);

        mockMvc.perform(post("/internal/parking-logs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andDo(MockMvcRestDocumentationWrapper.document("internal-parking-log-create",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Internal - 입출차").summary("내부 입출차 등록").build())));
    }
}
