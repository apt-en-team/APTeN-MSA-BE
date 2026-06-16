package com.apten.apartmentcomplex.docs;

import com.apten.apartmentcomplex.application.controller.MyApartmentComplexAdminController;
import com.apten.apartmentcomplex.application.model.request.ComplexAdminPatchReq;
import com.apten.apartmentcomplex.application.model.request.ComplexAdminPostReq;
import com.apten.apartmentcomplex.application.model.response.*;
import com.apten.apartmentcomplex.application.service.ApartmentComplexService;
import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceDocumentation;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// 관리자 내 단지 API 문서화 테스트
@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(controllers = MyApartmentComplexAdminController.class)
class MyApartmentComplexAdminControllerDocsTest {

    private MockMvc mockMvc;

    @MockBean
    @SuppressWarnings("unused")
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @MockBean
    private ApartmentComplexService apartmentComplexService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp(WebApplicationContext ctx, RestDocumentationContextProvider restDocs) {
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
                .apply(MockMvcRestDocumentation.documentationConfiguration(restDocs))
                .build();
    }

    @Test
    void 내_단지_조회() throws Exception {
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

        given(apartmentComplexService.getMyApartmentComplex(any(), any(), anyString())).willReturn(res);

        mockMvc.perform(get("/api/admin/apartment-complex/me")
                        .header("X-User-Role", "MANAGER")
                        .header("X-Complex-Id", "100")
                        .header("X-Selected-Complex-Id", "100"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-my-complex-get",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 내 단지")
                                .summary("내 단지 조회")
                                .build())));
    }

    @Test
    void 내_단지_관리자_목록_조회() throws Exception {
        ComplexAdminGetRes admin = ComplexAdminGetRes.builder()
                .userId(300L)
                .name("김관리")
                .email("admin@apten.com")
                .phone("010-9999-0000")
                .adminRole("MANAGER")
                .adminRoleName("관리자")
                .isActive(true)
                .assignedAt(LocalDateTime.of(2026, 1, 1, 10, 0))
                .unassignedAt(null)
                .build();

        given(apartmentComplexService.getMyComplexAdminList(any(), any(), anyString())).willReturn(List.of(admin));

        mockMvc.perform(get("/api/admin/apartment-complex/admins")
                        .header("X-User-Role", "MANAGER")
                        .header("X-Complex-Id", "100")
                        .header("X-Selected-Complex-Id", "100"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-my-complex-admin-list",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 내 단지")
                                .summary("내 단지 관리자 목록 조회")
                                .build())));
    }

    @Test
    void 내_단지_관리자_등록() throws Exception {
        ComplexAdminPostReq req = ComplexAdminPostReq.builder()
                .email("newadmin@apten.com")
                .password("pass1234!")
                .name("이관리")
                .phone("010-5555-6666")
                .adminRole("MANAGER")
                .build();

        ComplexAdminPostRes res = ComplexAdminPostRes.builder()
                .code("COMPLEX-001")
                .userId(301L)
                .name("이관리")
                .email("newadmin@apten.com")
                .phone("010-5555-6666")
                .adminRole("MANAGER")
                .adminRoleName("관리자")
                .isActive(true)
                .assignedAt(LocalDateTime.of(2026, 1, 5, 10, 0))
                .build();

        given(apartmentComplexService.assignAdminToMyComplex(any(), any(), anyString(), any())).willReturn(res);

        mockMvc.perform(post("/api/admin/apartment-complex/admins")
                        .header("X-User-Role", "MANAGER")
                        .header("X-Complex-Id", "100")
                        .header("X-Selected-Complex-Id", "100")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-my-complex-admin-create",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 내 단지")
                                .summary("내 단지 관리자 등록")
                                .build())));
    }

    @Test
    void 내_단지_관리자_수정() throws Exception {
        ComplexAdminPatchReq req = ComplexAdminPatchReq.builder()
                .name("이관리 수정")
                .phone("010-5555-7777")
                .adminRole("MANAGER")
                .isActive(true)
                .build();

        ComplexAdminPatchRes res = ComplexAdminPatchRes.builder()
                .userId(301L)
                .name("이관리 수정")
                .email("newadmin@apten.com")
                .phone("010-5555-7777")
                .adminRole("MANAGER")
                .adminRoleName("관리자")
                .isActive(true)
                .updatedAt(LocalDateTime.of(2026, 1, 6, 10, 0))
                .build();

        given(apartmentComplexService.updateMyComplexAdmin(any(), any(), anyString(), eq(301L), any())).willReturn(res);

        mockMvc.perform(patch("/api/admin/apartment-complex/admins/{userId}", 301L)
                        .header("X-User-Role", "MANAGER")
                        .header("X-Complex-Id", "100")
                        .header("X-Selected-Complex-Id", "100")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-my-complex-admin-update",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 내 단지")
                                .summary("내 단지 관리자 수정")
                                .build())));
    }

    @Test
    void 내_단지_관리자_해제() throws Exception {
        ComplexAdminDeleteRes res = ComplexAdminDeleteRes.builder()
                .code("COMPLEX-001")
                .userId(301L)
                .isActive(false)
                .unassignedAt(LocalDateTime.of(2026, 1, 7, 10, 0))
                .deletedAt(LocalDateTime.of(2026, 1, 7, 10, 0))
                .build();

        given(apartmentComplexService.unassignAdminFromMyComplex(any(), any(), anyString(), eq(301L))).willReturn(res);

        mockMvc.perform(delete("/api/admin/apartment-complex/admins/{userId}", 301L)
                        .header("X-User-Role", "MANAGER")
                        .header("X-Complex-Id", "100")
                        .header("X-Selected-Complex-Id", "100"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-my-complex-admin-delete",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 내 단지")
                                .summary("내 단지 관리자 해제")
                                .build())));
    }
}
