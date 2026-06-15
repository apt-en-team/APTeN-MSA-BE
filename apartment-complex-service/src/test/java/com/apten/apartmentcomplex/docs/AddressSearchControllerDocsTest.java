package com.apten.apartmentcomplex.docs;

import com.apten.apartmentcomplex.application.controller.AddressSearchController;
import com.apten.apartmentcomplex.application.model.response.AddressSearchPageRes;
import com.apten.apartmentcomplex.application.model.response.AddressSearchRes;
import com.apten.apartmentcomplex.application.service.AddressSearchService;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// 주소 검색 API 문서화 테스트
@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(controllers = AddressSearchController.class)
class AddressSearchControllerDocsTest {

    private MockMvc mockMvc;

    @MockBean
    @SuppressWarnings("unused")
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @MockBean
    private AddressSearchService addressSearchService;

    @BeforeEach
    void setUp(WebApplicationContext ctx, RestDocumentationContextProvider restDocs) {
        mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
                .apply(MockMvcRestDocumentation.documentationConfiguration(restDocs))
                .build();
    }

    @Test
    void 주소_검색() throws Exception {
        AddressSearchRes addressItem = new AddressSearchRes(
                "행복아파트",
                "서울시 강남구 테헤란로 123",
                "06234"
        );

        AddressSearchPageRes res = AddressSearchPageRes.builder()
                .content(List.of(addressItem))
                .page(1)
                .size(10)
                .totalElements(1)
                .totalPages(1)
                .hasNext(false)
                .build();

        given(addressSearchService.searchAddress(anyString(), any(), any())).willReturn(res);

        mockMvc.perform(get("/api/admin/master/apartment-complexes/address/search")
                        .param("keyword", "테헤란로")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-address-search",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 주소 검색")
                                .summary("행안부 주소 검색")
                                .build())));
    }
}
