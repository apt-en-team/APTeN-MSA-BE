package com.apten.household.docs;

import com.apten.household.application.controller.AdminHouseholdTypeController;
import com.apten.household.application.model.response.BuildingLineTypeRes;
import com.apten.household.application.model.response.HouseholdTypeRes;
import com.apten.household.application.service.HouseholdTypeService;
import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceDocumentation;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// 관리자 세대 유형 API 문서화 테스트
@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(controllers = AdminHouseholdTypeController.class)
class AdminHouseholdTypeControllerDocsTest extends HouseholdDocsTestBase {

    @MockBean
    private HouseholdTypeService householdTypeService;


    @Test
    void 평형_등록() throws Exception {
        Map<String, Object> req = Map.of("typeCode", "33", "typeName", "33평", "exclusiveAreaM2", 84.2);

        HouseholdTypeRes res = HouseholdTypeRes.builder()
                .typeId(1L).typeCode("33").typeName("33평")
                .exclusiveAreaM2(BigDecimal.valueOf(84.2)).build();

        given(householdTypeService.createHouseholdType(any(), any())).willReturn(res);

        mockMvc.perform(post("/api/admin/household-types")
                        .header("X-User-Id", "100").header("X-User-Role", "MANAGER")
                        .header("X-Complex-Id", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-household-type-create",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 세대 유형").summary("평형 등록").build())));
    }

    @Test
    void 평형_목록_조회() throws Exception {
        HouseholdTypeRes item = HouseholdTypeRes.builder()
                .typeId(1L).typeCode("33").typeName("33평")
                .exclusiveAreaM2(BigDecimal.valueOf(84.2)).build();

        given(householdTypeService.getHouseholdTypes()).willReturn(List.of(item));

        mockMvc.perform(get("/api/admin/household-types")
                        .header("X-User-Id", "100").header("X-User-Role", "MANAGER")
                        .header("X-Complex-Id", "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-household-type-list",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 세대 유형").summary("평형 목록 조회").build())));
    }

    @Test
    void 평형_수정() throws Exception {
        Map<String, Object> req = Map.of("typeName", "33평형", "exclusiveAreaM2", 85.0);

        HouseholdTypeRes res = HouseholdTypeRes.builder()
                .typeId(1L).typeCode("33").typeName("33평형")
                .exclusiveAreaM2(BigDecimal.valueOf(85.0)).build();

        given(householdTypeService.updateHouseholdType(any(), eq(1L), any())).willReturn(res);

        mockMvc.perform(patch("/api/admin/household-types/{typeId}", 1L)
                        .header("X-User-Id", "100").header("X-User-Role", "MANAGER")
                        .header("X-Complex-Id", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-household-type-update",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 세대 유형").summary("평형 수정").build())));
    }

    @Test
    void 평형_삭제() throws Exception {
        HouseholdTypeRes res = HouseholdTypeRes.builder()
                .typeId(1L).typeCode("33").typeName("33평").build();

        given(householdTypeService.deleteHouseholdType(any(), eq(1L))).willReturn(res);

        mockMvc.perform(delete("/api/admin/household-types/{typeId}", 1L)
                        .header("X-User-Id", "100").header("X-User-Role", "MANAGER")
                        .header("X-Complex-Id", "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-household-type-delete",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 세대 유형").summary("평형 삭제").build())));
    }

    @Test
    void 동_라인_평형_등록() throws Exception {
        Map<String, Object> req = Map.of("building", "101", "lineStart", 1, "lineEnd", 4, "typeId", 1L);

        BuildingLineTypeRes res = BuildingLineTypeRes.builder()
                .lineTypeId(1L).complexId(10L).building("101").lineStart(1).lineEnd(4).build();

        given(householdTypeService.createBuildingLineType(any(), any())).willReturn(res);

        mockMvc.perform(post("/api/admin/building-line-types")
                        .header("X-User-Id", "100").header("X-User-Role", "MANAGER")
                        .header("X-Complex-Id", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-building-line-type-create",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 동 라인 유형").summary("동 라인 평형 등록").build())));
    }

    @Test
    void 동_라인_평형_목록_조회() throws Exception {
        BuildingLineTypeRes item = BuildingLineTypeRes.builder()
                .lineTypeId(1L).complexId(10L).building("101").lineStart(1).lineEnd(4).build();

        given(householdTypeService.getBuildingLineTypes(any(), any())).willReturn(List.of(item));

        mockMvc.perform(get("/api/admin/building-line-types")
                        .header("X-User-Id", "100").header("X-User-Role", "MANAGER")
                        .header("X-Complex-Id", "10")
                        .param("building", "101"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-building-line-type-list",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 동 라인 유형").summary("동 라인 평형 목록 조회").build())));
    }

    @Test
    void 동호수_평형_조회() throws Exception {
        BuildingLineTypeRes res = BuildingLineTypeRes.builder()
                .lineTypeId(1L).complexId(10L).building("101").lineStart(1).lineEnd(4).build();

        given(householdTypeService.resolveBuildingLineType(any(), any(), any())).willReturn(res);

        mockMvc.perform(get("/api/admin/building-line-types/resolve")
                        .header("X-User-Id", "100").header("X-User-Role", "MANAGER")
                        .header("X-Complex-Id", "10")
                        .param("building", "101").param("unit", "101"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-building-line-type-resolve",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 동 라인 유형").summary("동호수 평형 조회").build())));
    }

    @Test
    void 동_라인_평형_수정() throws Exception {
        Map<String, Object> req = Map.of("lineStart", 1, "lineEnd", 5, "typeId", 2L);

        BuildingLineTypeRes res = BuildingLineTypeRes.builder()
                .lineTypeId(1L).complexId(10L).building("101").lineStart(1).lineEnd(5).build();

        given(householdTypeService.updateBuildingLineType(any(), eq(1L), any())).willReturn(res);

        mockMvc.perform(patch("/api/admin/building-line-types/{lineTypeId}", 1L)
                        .header("X-User-Id", "100").header("X-User-Role", "MANAGER")
                        .header("X-Complex-Id", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-building-line-type-update",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 동 라인 유형").summary("동 라인 평형 수정").build())));
    }

    @Test
    void 동_라인_평형_삭제() throws Exception {
        BuildingLineTypeRes res = BuildingLineTypeRes.builder()
                .lineTypeId(1L).complexId(10L).building("101").lineStart(1).lineEnd(4).build();

        given(householdTypeService.deleteBuildingLineType(any(), eq(1L))).willReturn(res);

        mockMvc.perform(delete("/api/admin/building-line-types/{lineTypeId}", 1L)
                        .header("X-User-Id", "100").header("X-User-Role", "MANAGER")
                        .header("X-Complex-Id", "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-building-line-type-delete",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 동 라인 유형").summary("동 라인 평형 삭제").build())));
    }
}
