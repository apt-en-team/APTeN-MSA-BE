package com.apten.board.docs;

import com.apten.board.application.controller.VoteController;
import com.apten.board.application.model.response.*;
import com.apten.board.application.service.VoteService;
import com.apten.board.domain.enums.VoteChoice;
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

// 투표 조회 API 문서화 테스트
@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(controllers = VoteController.class)
class VoteControllerDocsTest {

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
    void 투표_참여() throws Exception {
        Map<String, String> req = Map.of("choice", "AGREE");

        VoteParticipationRes res = VoteParticipationRes.builder()
                .voteId(1L).householdId(50L).choice(VoteChoice.AGREE)
                .participatedAt(LocalDateTime.of(2026, 1, 5, 10, 0)).build();

        given(voteService.participateVote(eq(1L), any())).willReturn(res);

        mockMvc.perform(post("/api/votes/{voteId}/participations", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andDo(MockMvcRestDocumentationWrapper.document("vote-participate",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Vote - 투표").summary("투표 참여").build())));
    }

    @Test
    void 투표_목록_조회() throws Exception {
        VoteListRes item = VoteListRes.builder()
                .voteId(1L).title("주민 찬반 투표").description("단지 리모델링 관련")
                .startAt(LocalDateTime.of(2026, 1, 1, 0, 0))
                .endAt(LocalDateTime.of(2026, 1, 31, 23, 59))
                .status(VoteStatus.OPEN).agreeCount(10).disagreeCount(3).householdCount(50).build();

        PageResponse<VoteListRes> res = PageResponse.<VoteListRes>builder()
                .content(List.of(item)).page(0).size(20).totalElements(1).totalPages(1).hasNext(false).build();

        given(voteService.getVoteList(any())).willReturn(res);

        mockMvc.perform(get("/api/votes").param("page", "0").param("size", "20"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("vote-list",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Vote - 투표").summary("투표 목록 조회").build())));
    }

    @Test
    void 투표_상세_조회() throws Exception {
        VoteDetailRes res = VoteDetailRes.builder()
                .voteId(1L).noticeId(10L).complexId(100L)
                .title("주민 찬반 투표").description("단지 리모델링 관련")
                .startAt(LocalDateTime.of(2026, 1, 1, 0, 0))
                .endAt(LocalDateTime.of(2026, 1, 31, 23, 59))
                .status(VoteStatus.OPEN).agreeCount(10).disagreeCount(3).householdCount(50)
                .createdAt(LocalDateTime.of(2026, 1, 1, 10, 0))
                .updatedAt(LocalDateTime.of(2026, 1, 1, 10, 0))
                .isParticipated(false).myChoice(null).build();

        given(voteService.getVoteDetail(eq(1L))).willReturn(res);

        mockMvc.perform(get("/api/votes/{voteId}", 1L))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("vote-detail",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Vote - 투표").summary("투표 상세 조회").build())));
    }
}
