package com.apten.board.docs;

import com.apten.board.application.controller.AdminVoteController;
import com.apten.board.application.model.response.*;
import com.apten.board.application.service.VoteService;
import com.apten.board.domain.enums.VoteStatus;
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

// 관리자 투표 API 문서화 테스트
@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(controllers = AdminVoteController.class)
class AdminVoteControllerDocsTest {

    private MockMvc mockMvc;

    @MockBean
    @SuppressWarnings("unused")
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @MockBean
    private VoteService voteService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp(WebApplicationContext ctx, RestDocumentationContextProvider restDocs) {
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
                .apply(MockMvcRestDocumentation.documentationConfiguration(restDocs))
                .build();
    }

    @Test
    void 투표_생성() throws Exception {
        Map<String, Object> req = Map.of(
                "noticeId", 10L,
                "title", "단지 리모델링 찬반투표",
                "description", "투표 설명",
                "startAt", "2026-01-01T00:00:00",
                "endAt", "2026-01-31T23:59:59"
        );

        VoteCreateRes res = VoteCreateRes.builder()
                .voteId(1L).noticeId(10L).title("단지 리모델링 찬반투표")
                .startAt(LocalDateTime.of(2026, 1, 1, 0, 0))
                .endAt(LocalDateTime.of(2026, 1, 31, 23, 59))
                .status(VoteStatus.READY)
                .createdAt(LocalDateTime.of(2026, 1, 1, 10, 0)).build();

        given(voteService.createVote(any())).willReturn(res);

        mockMvc.perform(post("/api/admin/votes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-vote-create",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 투표").summary("투표 생성").build())));
    }

    @Test
    void 투표_결과_조회() throws Exception {
        VoteResultRes res = VoteResultRes.builder()
                .voteId(1L).title("단지 리모델링 찬반투표").status(VoteStatus.OPEN)
                .agreeCount(10).disagreeCount(3).householdCount(50).build();

        given(voteService.getVoteResult(eq(1L))).willReturn(res);

        mockMvc.perform(get("/api/admin/votes/{voteId}/results", 1L))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-vote-result",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 투표").summary("투표 결과 조회").build())));
    }

    @Test
    void 관리자_투표_목록_조회() throws Exception {
        VoteListRes item = VoteListRes.builder()
                .voteId(1L).title("단지 리모델링 찬반투표").description("투표 설명")
                .startAt(LocalDateTime.of(2026, 1, 1, 0, 0))
                .endAt(LocalDateTime.of(2026, 1, 31, 23, 59))
                .status(VoteStatus.OPEN).agreeCount(10).disagreeCount(3).householdCount(50).build();

        VotePageRes.Summary summary = VotePageRes.Summary.builder()
                .total(5L).open(2L).ready(1L).closed(2L).build();

        VotePageRes res = VotePageRes.builder()
                .content(List.of(item)).page(0).size(20).totalElements(1).totalPages(1).hasNext(false)
                .summary(summary).build();

        given(voteService.getAdminVoteList(any())).willReturn(res);

        mockMvc.perform(get("/api/admin/votes").param("page", "0").param("size", "20"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-vote-list",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 투표").summary("관리자 투표 목록 조회").build())));
    }

    @Test
    void 관리자_투표_상세_조회() throws Exception {
        VoteDetailRes res = VoteDetailRes.builder()
                .voteId(1L).noticeId(10L).complexId(100L)
                .title("단지 리모델링 찬반투표").description("투표 설명")
                .startAt(LocalDateTime.of(2026, 1, 1, 0, 0))
                .endAt(LocalDateTime.of(2026, 1, 31, 23, 59))
                .status(VoteStatus.OPEN).agreeCount(10).disagreeCount(3).householdCount(50)
                .createdAt(LocalDateTime.of(2026, 1, 1, 10, 0))
                .updatedAt(LocalDateTime.of(2026, 1, 1, 10, 0))
                .isParticipated(false).myChoice(null).build();

        given(voteService.getAdminVoteDetail(eq(1L))).willReturn(res);

        mockMvc.perform(get("/api/admin/votes/{voteId}", 1L))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-vote-detail",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 투표").summary("관리자 투표 상세 조회").build())));
    }

    @Test
    void 투표_수정() throws Exception {
        Map<String, Object> req = Map.of("title", "수정된 투표 제목", "description", "수정된 설명");

        VotePatchRes res = VotePatchRes.builder()
                .voteId(1L).title("수정된 투표 제목")
                .startAt(LocalDateTime.of(2026, 1, 1, 0, 0))
                .endAt(LocalDateTime.of(2026, 1, 31, 23, 59))
                .updatedAt(LocalDateTime.of(2026, 1, 2, 10, 0)).build();

        given(voteService.updateVote(eq(1L), any())).willReturn(res);

        mockMvc.perform(patch("/api/admin/votes/{voteId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-vote-update",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 투표").summary("투표 수정").build())));
    }

    @Test
    void 투표_삭제() throws Exception {
        VoteDeleteRes res = VoteDeleteRes.builder()
                .voteId(1L).deletedAt(LocalDateTime.of(2026, 1, 3, 10, 0)).build();

        given(voteService.deleteVote(eq(1L))).willReturn(res);

        mockMvc.perform(delete("/api/admin/votes/{voteId}", 1L))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-vote-delete",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 투표").summary("투표 삭제").build())));
    }

    @Test
    void 투표_종료() throws Exception {
        VoteCloseRes res = VoteCloseRes.builder()
                .voteId(1L).status(VoteStatus.CLOSED)
                .closedAt(LocalDateTime.of(2026, 1, 15, 10, 0)).build();

        given(voteService.closeVote(eq(1L))).willReturn(res);

        mockMvc.perform(patch("/api/admin/votes/{voteId}/close", 1L))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-vote-close",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 투표").summary("투표 종료 처리").build())));
    }
}
