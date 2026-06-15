package com.apten.parkingvehicle.docs;

import com.apten.parkingvehicle.application.controller.ParkingSseController;
import com.apten.parkingvehicle.application.service.SseSubscribeService;
import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceDocumentation;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(controllers = ParkingSseController.class)
class ParkingSseControllerDocsTest extends ParkingDocsTestBase {

    @MockBean
    private SseSubscribeService sseSubscribeService;

    @Test
    void 주차_SSE_구독() throws Exception {
        given(sseSubscribeService.subscribe(any(), any())).willReturn(new SseEmitter());

        mockMvc.perform(get("/api/parking/spots/sse")
                        .header("X-User-Role", "USER")
                        .header("X-Complex-Id", "10")
                        .accept(MediaType.TEXT_EVENT_STREAM_VALUE))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("parking-sse-subscribe",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Resident - 주차").summary("실시간 주차 현황 SSE 구독").build())));
    }
}
