package com.apten.apartmentcomplex.docs;

import com.apten.apartmentcomplex.application.controller.ResidentMyApartmentComplexController;
import com.apten.apartmentcomplex.application.model.response.ApartmentComplexGetDetailRes;
import com.apten.apartmentcomplex.application.service.ApartmentComplexService;
import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceDocumentation;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// 입주민 내 단지 API 문서화 테스트
@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(controllers = ResidentMyApartmentComplexController.class)
class ResidentMyApartmentComplexControllerDocsTest {

    private MockMvc mockMvc;

    @MockBean
    @SuppressWarnings("unused")
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @MockBean
    private ApartmentComplexService apartmentComplexService;

    @BeforeEach
    void setUp(WebApplicationContext ctx, RestDocumentationContextProvider restDocs) {
        mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
                .apply(MockMvcRestDocumentation.documentationConfiguration(restDocs))
                .build();
    }

    @Test
    void 입주민_내_단지_조회() throws Exception {
        ApartmentComplexGetDetailRes res = ApartmentComplexGetDetailRes.builder()
                .complexId(100L)
                .code("COMPLEX-001")
                .name("행복아파트")
                .address("서울시 강남구 테헤란로 123")
                .zipCode("06234")
                .status("ACTIVE")
                .statusName("활성")
                .description("테스트 단지")
                .features(Map.of("FACILITY_RESERVATION", true))
                .parkingTypeCode("BASIC")
                .parkingTypeValue("기본")
                .createdAt(LocalDateTime.of(2026, 1, 1, 10, 0))
                .updatedAt(LocalDateTime.of(2026, 1, 2, 10, 0))
                .build();

        given(apartmentComplexService.getMyApartmentComplexForResident(anyString(), any())).willReturn(res);

        mockMvc.perform(get("/api/resident/apartment-complex/me")
                        .header("X-User-Role", "RESIDENT")
                        .header("X-Complex-Id", "100"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("resident-my-complex-get",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Resident - 내 단지")
                                .summary("입주민 내 단지 조회")
                                .build())));
    }
}
