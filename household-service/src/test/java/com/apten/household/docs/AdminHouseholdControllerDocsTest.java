package com.apten.household.docs;

import com.apten.household.application.controller.AdminHouseholdController;
import com.apten.household.application.model.response.*;
import com.apten.household.application.service.HouseholdService;
import com.apten.household.domain.enums.HouseholdMemberRole;
import com.apten.household.domain.enums.HouseholdStatus;
import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceDocumentation;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// 관리자 세대 API 문서화 테스트
@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(controllers = AdminHouseholdController.class)
class AdminHouseholdControllerDocsTest extends HouseholdDocsTestBase {

    @MockBean
    private HouseholdService householdService;


    @Test
    void 세대_등록() throws Exception {
        Map<String, Object> req = Map.of("building", "101", "unit", "101", "typeId", 1L);

        HouseholdCreateRes res = HouseholdCreateRes.builder()
                .householdId(1L).complexId(10L).building("101").unit("101")
                .status(HouseholdStatus.VACANT).build();

        given(householdService.createHousehold(any(), any())).willReturn(res);

        mockMvc.perform(post("/api/admin/households")
                        .header("X-User-Id", "100").header("X-User-Role", "MANAGER")
                        .header("X-Complex-Id", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-household-create",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 세대").summary("세대 등록").build())));
    }

    @Test
    void 세대_일괄_등록() throws Exception {
        Map<String, Object> req = Map.of("building", "101", "floorStart", 1, "floorEnd", 10,
                "lineStart", 1, "lineEnd", 4, "typeId", 1L);

        HouseholdBulkCreateRes res = HouseholdBulkCreateRes.builder()
                .complexId(10L).building("101").floorStart(1).floorEnd(10).build();

        given(householdService.createHouseholdsBulk(any(), any())).willReturn(res);

        mockMvc.perform(post("/api/admin/households/bulk")
                        .header("X-User-Id", "100").header("X-User-Role", "MANAGER")
                        .header("X-Complex-Id", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-household-bulk-create",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 세대").summary("세대 일괄 등록").build())));
    }

    @Test
    void 세대_목록_조회() throws Exception {
        HouseholdListRes.Item item = HouseholdListRes.Item.builder()
                .householdId(1L).complexId(10L).building("101").unit("101")
                .typeId(1L).typeName("33평").status(HouseholdStatus.OCCUPIED).build();

        HouseholdListRes res = HouseholdListRes.builder()
                .content(List.of(item)).page(0).size(20).totalElements(1L)
                .totalPages(1).hasNext(false).build();

        given(householdService.getHouseholdList(any(), any())).willReturn(res);

        mockMvc.perform(get("/api/admin/households")
                        .header("X-User-Id", "100").header("X-User-Role", "MANAGER")
                        .header("X-Complex-Id", "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-household-list",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 세대").summary("세대 목록 조회").build())));
    }

    @Test
    void 세대_상세_조회() throws Exception {
        HouseholdDetailRes res = HouseholdDetailRes.builder()
                .householdId(1L).complexId(10L).building("101").unit("101")
                .typeId(1L).typeName("33평").status(HouseholdStatus.OCCUPIED)
                .memberCount(2L).createdAt(LocalDateTime.of(2026, 1, 1, 10, 0)).build();

        given(householdService.getHouseholdDetail(any(), eq(1L))).willReturn(res);

        mockMvc.perform(get("/api/admin/households/{householdId}", 1L)
                        .header("X-User-Id", "100").header("X-User-Role", "MANAGER")
                        .header("X-Complex-Id", "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-household-detail",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 세대").summary("세대 상세 조회").build())));
    }

    @Test
    void 세대_정보_수정() throws Exception {
        Map<String, Object> req = Map.of("typeId", 2L);

        HouseholdPatchRes res = HouseholdPatchRes.builder()
                .householdId(1L).building("101").unit("101")
                .typeId(2L).status(HouseholdStatus.OCCUPIED).build();

        given(householdService.updateHousehold(any(), eq(1L), any())).willReturn(res);

        mockMvc.perform(patch("/api/admin/households/{householdId}", 1L)
                        .header("X-User-Id", "100").header("X-User-Role", "MANAGER")
                        .header("X-Complex-Id", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-household-update",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 세대").summary("세대 정보 수정").build())));
    }

    @Test
    void 세대_삭제() throws Exception {
        willDoNothing().given(householdService).deleteHousehold(any(), eq(1L));

        mockMvc.perform(delete("/api/admin/households/{householdId}", 1L)
                        .header("X-User-Id", "100").header("X-User-Role", "MANAGER")
                        .header("X-Complex-Id", "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-household-delete",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 세대").summary("세대 삭제").build())));
    }

    @Test
    void 세대_상태_변경() throws Exception {
        Map<String, String> req = Map.of("status", "OCCUPIED", "reason", "입주 처리");

        HouseholdStatusPatchRes res = HouseholdStatusPatchRes.builder()
                .householdId(1L).status(HouseholdStatus.OCCUPIED)
                .changedAt(LocalDateTime.of(2026, 1, 5, 10, 0)).build();

        given(householdService.changeHouseholdStatus(any(), eq(1L), any())).willReturn(res);

        mockMvc.perform(patch("/api/admin/households/{householdId}/status", 1L)
                        .header("X-User-Id", "100").header("X-User-Role", "MANAGER")
                        .header("X-Complex-Id", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-household-status-change",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 세대").summary("세대 상태 변경").build())));
    }

    @Test
    void 세대_이력_조회() throws Exception {
        HouseholdHistoryRes item = HouseholdHistoryRes.builder()
                .historyId(1L).fromStatus("VACANT").toStatus("OCCUPIED")
                .reason("입주").changedAt(LocalDateTime.of(2026, 1, 1, 10, 0)).build();

        given(householdService.getHouseholdHistory(any(), eq(1L))).willReturn(List.of(item));

        mockMvc.perform(get("/api/admin/households/{householdId}/history", 1L)
                        .header("X-User-Id", "100").header("X-User-Role", "MANAGER")
                        .header("X-Complex-Id", "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-household-history",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 세대").summary("세대 이력 조회").build())));
    }

    @Test
    void 세대원_등록() throws Exception {
        Map<String, Object> req = Map.of("userId", 200L, "role", "세대원");

        HouseholdMemberPostRes res = HouseholdMemberPostRes.builder()
                .householdMemberId(10L).householdId(1L).userId(200L)
                .role(HouseholdMemberRole.MEMBER).isActive(true).build();

        given(householdService.addHouseholdMember(any(), eq(1L), any())).willReturn(res);

        mockMvc.perform(post("/api/admin/households/{householdId}/members", 1L)
                        .header("X-User-Id", "100").header("X-User-Role", "MANAGER")
                        .header("X-Complex-Id", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-household-member-add",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 세대원").summary("세대원 등록").build())));
    }

    @Test
    void 세대원_목록_조회() throws Exception {
        HouseholdMemberListRes item = HouseholdMemberListRes.builder()
                .householdMemberId(10L).userId(200L)
                .role(HouseholdMemberRole.HEAD).name("홍길동").build();

        given(householdService.getHouseholdMembers(any(), eq(1L))).willReturn(List.of(item));

        mockMvc.perform(get("/api/admin/households/{householdId}/members", 1L)
                        .header("X-User-Id", "100").header("X-User-Role", "MANAGER")
                        .header("X-Complex-Id", "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-household-member-list",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 세대원").summary("세대원 목록 조회").build())));
    }

    @Test
    void 세대원_수정() throws Exception {
        Map<String, Object> req = Map.of("role", "세대주", "isActive", true);

        HouseholdMemberPatchRes res = HouseholdMemberPatchRes.builder()
                .householdMemberId(10L).role(HouseholdMemberRole.HEAD)
                .isActive(true).updatedAt(LocalDateTime.of(2026, 1, 5, 10, 0)).build();

        given(householdService.updateHouseholdMember(any(), eq(10L), any())).willReturn(res);

        mockMvc.perform(patch("/api/admin/household-members/{householdMemberId}", 10L)
                        .header("X-User-Id", "100").header("X-User-Role", "MANAGER")
                        .header("X-Complex-Id", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-household-member-update",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 세대원").summary("세대원 수정").build())));
    }

    @Test
    void 세대원_삭제() throws Exception {
        HouseholdMemberDeleteRes res = HouseholdMemberDeleteRes.builder()
                .householdMemberId(10L).message("세대원 삭제 완료")
                .deletedAt(LocalDateTime.of(2026, 1, 5, 10, 0)).build();

        given(householdService.deleteHouseholdMember(any(), eq(10L))).willReturn(res);

        mockMvc.perform(delete("/api/admin/household-members/{householdMemberId}", 10L)
                        .header("X-User-Id", "100").header("X-User-Role", "MANAGER")
                        .header("X-Complex-Id", "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-household-member-delete",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 세대원").summary("세대원 삭제").build())));
    }

    @Test
    void 세대주_변경() throws Exception {
        Map<String, Object> req = Map.of("newHeadUserId", 201L);

        HouseholdHeadPatchRes res = HouseholdHeadPatchRes.builder()
                .householdId(1L).headUserId(201L)
                .updatedAt(LocalDateTime.of(2026, 1, 5, 10, 0)).build();

        given(householdService.changeHouseholdHead(any(), eq(1L), any())).willReturn(res);

        mockMvc.perform(patch("/api/admin/households/{householdId}/head", 1L)
                        .header("X-User-Id", "100").header("X-User-Role", "MANAGER")
                        .header("X-Complex-Id", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-household-head-change",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 세대").summary("세대주 변경").build())));
    }

    @Test
    void 전_세대원_이벤트_재발행() throws Exception {
        HouseholdMemberRepublishRes res = HouseholdMemberRepublishRes.builder()
                .republishedCount(100L).message("재발행 완료").build();

        given(householdService.republishAllHouseholdMembers()).willReturn(res);

        mockMvc.perform(post("/api/admin/household-members/republish")
                        .header("X-User-Id", "100").header("X-User-Role", "MASTER"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-household-member-republish",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 세대원").summary("전 세대원 이벤트 재발행").build())));
    }

    @Test
    void 전_세대_이벤트_재발행() throws Exception {
        HouseholdRepublishRes res = HouseholdRepublishRes.builder()
                .republishedCount(50L).message("재발행 완료").build();

        given(householdService.republishAllHouseholds()).willReturn(res);

        mockMvc.perform(post("/api/admin/households/republish")
                        .header("X-User-Id", "100").header("X-User-Role", "MASTER"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-household-republish",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 세대").summary("전 세대 이벤트 재발행").build())));
    }
}
