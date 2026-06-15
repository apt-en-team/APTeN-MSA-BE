package com.apten.apartmentcomplex.docs;

import com.apten.apartmentcomplex.application.controller.AdminApartmentComplexController;
import com.apten.apartmentcomplex.application.model.request.ApartmentComplexPatchReq;
import com.apten.apartmentcomplex.application.model.request.ApartmentComplexReq;
import com.apten.apartmentcomplex.application.model.request.ApartmentComplexStatusPatchReq;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// MASTER 단지 관리 API 문서화 테스트
@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(controllers = AdminApartmentComplexController.class)
class AdminApartmentComplexControllerDocsTest {

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
    void 단지_등록() throws Exception {
        ApartmentComplexReq req = ApartmentComplexReq.builder()
                .name("행복아파트")
                .address("서울시 강남구 테헤란로 123")
                .zipCode("06234")
                .description("테스트 단지입니다.")
                .features(Map.of("FACILITY_RESERVATION", true, "PARKING", false))
                .parkingType(com.apten.common.enums.ParkingType.BASIC)
                .managerEmail("manager@apten.com")
                .managerPassword("pass1234!")
                .managerName("홍길동")
                .managerPhone("010-1234-5678")
                .build();

        ApartmentComplexPostRes res = ApartmentComplexPostRes.builder()
                .complexId(100L)
                .code("COMPLEX-001")
                .name("행복아파트")
                .managerUserId(200L)
                .managerName("홍길동")
                .managerEmail("manager@apten.com")
                .managerPhone("010-1234-5678")
                .features(Map.of("FACILITY_RESERVATION", true, "PARKING", false))
                .parkingTypeCode("BASIC")
                .parkingTypeValue("기본")
                .createdAt(LocalDateTime.of(2026, 1, 1, 10, 0))
                .build();

        given(apartmentComplexService.createApartmentComplex(any())).willReturn(res);

        mockMvc.perform(post("/api/admin/master/apartment-complexes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-complex-create",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 단지 관리")
                                .summary("단지 등록")
                                .build())));
    }

    @Test
    void 단지_목록_조회() throws Exception {
        ApartmentComplexGetRes item = ApartmentComplexGetRes.builder()
                .code("COMPLEX-001")
                .name("행복아파트")
                .address("서울시 강남구 테헤란로 123")
                .status("ACTIVE")
                .statusName("활성")
                .description("테스트 단지")
                .features(Map.of("FACILITY_RESERVATION", true))
                .parkingTypeCode("BASIC")
                .parkingTypeValue("기본")
                .createdAt(LocalDateTime.of(2026, 1, 1, 10, 0))
                .build();

        ApartmentComplexGetPageRes res = ApartmentComplexGetPageRes.builder()
                .content(List.of(item))
                .page(0).size(10).totalElements(1).totalPages(1).hasNext(false)
                .build();

        given(apartmentComplexService.getApartmentComplexList(any())).willReturn(res);

        mockMvc.perform(get("/api/admin/master/apartment-complexes")
                        .param("keyword", "행복")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-complex-list",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 단지 관리")
                                .summary("단지 목록 조회")
                                .build())));
    }

    @Test
    void 단지_상세_조회() throws Exception {
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

        given(apartmentComplexService.getApartmentComplexDetail(eq("COMPLEX-001"))).willReturn(res);

        mockMvc.perform(get("/api/admin/master/apartment-complexes/{code}", "COMPLEX-001"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-complex-detail",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 단지 관리")
                                .summary("단지 상세 조회")
                                .build())));
    }

    @Test
    void 단지_수정() throws Exception {
        ApartmentComplexPatchReq req = ApartmentComplexPatchReq.builder()
                .name("행복아파트 수정")
                .description("수정된 단지 설명")
                .features(Map.of("FACILITY_RESERVATION", true))
                .parkingType(com.apten.common.enums.ParkingType.SENSOR)
                .build();

        ApartmentComplexPatchRes res = ApartmentComplexPatchRes.builder()
                .code("COMPLEX-001")
                .name("행복아파트 수정")
                .description("수정된 단지 설명")
                .parkingTypeCode("SENSOR")
                .parkingTypeValue("센서")
                .updatedAt(LocalDateTime.of(2026, 1, 3, 10, 0))
                .build();

        given(apartmentComplexService.updateApartmentComplex(eq("COMPLEX-001"), any())).willReturn(res);

        mockMvc.perform(patch("/api/admin/master/apartment-complexes/{code}", "COMPLEX-001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-complex-update",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 단지 관리")
                                .summary("단지 수정")
                                .build())));
    }

    @Test
    void 단지_상태_변경() throws Exception {
        ApartmentComplexStatusPatchReq req = new ApartmentComplexStatusPatchReq("INACTIVE");

        ApartmentComplexStatusPatchRes res = ApartmentComplexStatusPatchRes.builder()
                .code("COMPLEX-001")
                .status("INACTIVE")
                .statusName("비활성")
                .updatedAt(LocalDateTime.of(2026, 1, 4, 10, 0))
                .build();

        given(apartmentComplexService.changeApartmentComplexStatus(eq("COMPLEX-001"), any())).willReturn(res);

        mockMvc.perform(patch("/api/admin/master/apartment-complexes/{code}/status", "COMPLEX-001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-complex-status",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 단지 관리")
                                .summary("단지 상태 변경")
                                .build())));
    }

    @Test
    void 관리자_단지_소속_지정() throws Exception {
        ComplexAdminPostReq req = ComplexAdminPostReq.builder()
                .email("newadmin@apten.com")
                .password("pass1234!")
                .name("김관리")
                .phone("010-9999-0000")
                .adminRole("MANAGER")
                .build();

        ComplexAdminPostRes res = ComplexAdminPostRes.builder()
                .code("COMPLEX-001")
                .userId(300L)
                .name("김관리")
                .email("newadmin@apten.com")
                .phone("010-9999-0000")
                .adminRole("MANAGER")
                .adminRoleName("관리자")
                .isActive(true)
                .assignedAt(LocalDateTime.of(2026, 1, 5, 10, 0))
                .build();

        given(apartmentComplexService.assignAdminToComplex(eq("COMPLEX-001"), any())).willReturn(res);

        mockMvc.perform(post("/api/admin/master/apartment-complexes/{code}/admins", "COMPLEX-001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-complex-assign-admin",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 단지 관리")
                                .summary("관리자 단지 소속 지정")
                                .build())));
    }

    @Test
    void 단지_선택() throws Exception {
        ApartmentComplexSelectRes res = ApartmentComplexSelectRes.builder()
                .complexId(100L)
                .code("COMPLEX-001")
                .name("행복아파트")
                .status("ACTIVE")
                .statusName("활성")
                .features(Map.of("FACILITY_RESERVATION", true))
                .parkingTypeCode("BASIC")
                .parkingTypeValue("기본")
                .adminPageUrl("/admin/dashboard")
                .build();

        given(apartmentComplexService.selectApartmentComplex(eq("COMPLEX-001"))).willReturn(res);

        mockMvc.perform(get("/api/admin/master/apartment-complexes/{code}/select", "COMPLEX-001"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-complex-select",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 단지 관리")
                                .summary("단지 선택 정보 조회")
                                .build())));
    }
}
