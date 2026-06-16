package com.apten.facilityreservation.docs;

import com.apten.facilityreservation.application.controller.ResidentGxProgramController;
import com.apten.facilityreservation.application.service.GxProgramService;
import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceDocumentation;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.RestDocumentationExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(controllers = ResidentGxProgramController.class)
class ResidentGxProgramControllerDocsTest extends FacilityDocsTestBase {

    @MockBean
    private GxProgramService gxProgramService;

    @Test
    void 입주민_GX_프로그램_목록_조회() throws Exception {
        given(gxProgramService.getResidentGxProgramList(any(), any())).willReturn(null);
        mockMvc.perform(get("/api/gx-programs")
                        .header("X-User-Id", "100").header("X-User-Role", "USER").header("X-Complex-Id", "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("resident-gx-program-list",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Resident - GX 프로그램").summary("입주민 GX 프로그램 목록 조회").build())));
    }

    @Test
    void 입주민_GX_프로그램_상세_조회() throws Exception {
        given(gxProgramService.getResidentGxProgramDetail(any(), any(), any())).willReturn(null);
        mockMvc.perform(get("/api/gx-programs/{programId}", 1L)
                        .header("X-User-Id", "100").header("X-User-Role", "USER").header("X-Complex-Id", "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("resident-gx-program-detail",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Resident - GX 프로그램").summary("입주민 GX 프로그램 상세 조회").build())));
    }
}
