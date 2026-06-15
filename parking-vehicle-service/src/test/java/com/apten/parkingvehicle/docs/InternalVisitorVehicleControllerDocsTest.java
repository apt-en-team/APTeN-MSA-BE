package com.apten.parkingvehicle.docs;

import com.apten.parkingvehicle.application.controller.InternalVisitorVehicleController;
import com.apten.parkingvehicle.application.service.VisitorVehicleService;
import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceDocumentation;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.RestDocumentationExtension;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(controllers = InternalVisitorVehicleController.class)
class InternalVisitorVehicleControllerDocsTest extends ParkingDocsTestBase {

    @MockBean
    private VisitorVehicleService visitorVehicleService;

    @Test
    void 방문차량_자동_만료() throws Exception {
        given(visitorVehicleService.expireVisitorVehicles()).willReturn(null);

        mockMvc.perform(post("/internal/visitor-vehicles/expire"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("internal-visitor-vehicle-expire",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Internal - 방문차량").summary("방문차량 자동 만료").build())));
    }
}
