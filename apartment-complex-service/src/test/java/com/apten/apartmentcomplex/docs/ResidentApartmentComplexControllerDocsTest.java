package com.apten.apartmentcomplex.docs;

import com.apten.apartmentcomplex.application.controller.ResidentApartmentComplexController;
import com.apten.apartmentcomplex.application.model.response.ApartmentComplexPublicRes;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// 공개 단지 목록 API 문서화 테스트
@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(controllers = ResidentApartmentComplexController.class)
class ResidentApartmentComplexControllerDocsTest {

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
    void 공개_단지_목록_조회() throws Exception {
        List<ApartmentComplexPublicRes> res = List.of(
                ApartmentComplexPublicRes.builder()
                        .complexId(100L)
                        .code("COMPLEX-001")
                        .name("행복아파트")
                        .address("서울시 강남구 테헤란로 123")
                        .build(),
                ApartmentComplexPublicRes.builder()
                        .complexId(101L)
                        .code("COMPLEX-002")
                        .name("미래아파트")
                        .address("서울시 서초구 반포대로 456")
                        .build()
        );

        // keyword 없이 전체 조회 케이스
        given(apartmentComplexService.getAvailableApartmentComplexes(isNull())).willReturn(res);

        mockMvc.perform(get("/api/apartment-complexes"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("resident-complex-list",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Resident - 단지 목록")
                                .summary("공개 단지 목록 조회 (회원가입 화면용)")
                                .build())));
    }

    @Test
    void 공개_단지_목록_키워드_검색() throws Exception {
        List<ApartmentComplexPublicRes> res = List.of(
                ApartmentComplexPublicRes.builder()
                        .complexId(100L)
                        .code("COMPLEX-001")
                        .name("행복아파트")
                        .address("서울시 강남구 테헤란로 123")
                        .build()
        );

        given(apartmentComplexService.getAvailableApartmentComplexes(anyString())).willReturn(res);

        mockMvc.perform(get("/api/apartment-complexes")
                        .param("keyword", "행복"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("resident-complex-list-keyword",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Resident - 단지 목록")
                                .summary("공개 단지 목록 키워드 검색")
                                .build())));
    }
}
