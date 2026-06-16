package com.apten.board.docs;

import com.apten.board.application.controller.CommentController;
import com.apten.board.application.model.response.*;
import com.apten.board.application.service.CommentService;
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

// 댓글 API 문서화 테스트
@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(controllers = CommentController.class)
class CommentControllerDocsTest {

    private MockMvc mockMvc;

    @MockBean
    @SuppressWarnings("unused")
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @MockBean
    private CommentService commentService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp(WebApplicationContext ctx, RestDocumentationContextProvider restDocs) {
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
                .apply(MockMvcRestDocumentation.documentationConfiguration(restDocs))
                .build();
    }

    @Test
    void 댓글_작성() throws Exception {
        Map<String, String> req = Map.of("content", "댓글 내용입니다.");

        CommentCreateRes res = CommentCreateRes.builder()
                .commentId(10L).postId(1L).content("댓글 내용입니다.")
                .createdAt(LocalDateTime.of(2026, 1, 1, 10, 0)).build();

        given(commentService.createComment(eq(1L), any())).willReturn(res);

        mockMvc.perform(post("/api/boards/posts/{postId}/comments", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andDo(MockMvcRestDocumentationWrapper.document("board-comment-create",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Board - 댓글").summary("댓글 작성").build())));
    }

    @Test
    void 댓글_수정() throws Exception {
        Map<String, String> req = Map.of("content", "수정된 댓글");

        CommentPatchRes res = CommentPatchRes.builder()
                .commentId(10L).content("수정된 댓글")
                .updatedAt(LocalDateTime.of(2026, 1, 2, 10, 0)).build();

        given(commentService.updateComment(eq(10L), any())).willReturn(res);

        mockMvc.perform(patch("/api/boards/comments/{commentId}", 10L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("board-comment-update",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Board - 댓글").summary("댓글 수정").build())));
    }

    @Test
    void 댓글_삭제() throws Exception {
        CommentDeleteRes res = CommentDeleteRes.builder()
                .commentId(10L).deletedAt(LocalDateTime.of(2026, 1, 3, 10, 0)).build();

        given(commentService.deleteComment(eq(10L))).willReturn(res);

        mockMvc.perform(delete("/api/boards/comments/{commentId}", 10L))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("board-comment-delete",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Board - 댓글").summary("댓글 삭제").build())));
    }

    @Test
    void 내_댓글_조회() throws Exception {
        MyCommentListRes item = MyCommentListRes.builder()
                .commentId(10L).postId(1L).postTitle("게시글 제목").content("댓글 내용")
                .createdAt(LocalDateTime.of(2026, 1, 1, 10, 0)).build();

        PageResponse<MyCommentListRes> res = PageResponse.<MyCommentListRes>builder()
                .content(List.of(item)).page(0).size(20).totalElements(1).totalPages(1).hasNext(false).build();

        given(commentService.getMyCommentList(any())).willReturn(res);

        mockMvc.perform(get("/api/boards/my-comments").param("page", "0").param("size", "20"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("board-my-comment-list",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Board - 댓글").summary("내 댓글 조회").build())));
    }

    @Test
    void 댓글_목록_조회() throws Exception {
        CommentListRes item = CommentListRes.builder()
                .commentId(10L).postId(1L).userId(100L).writerName("홍길동")
                .userRole("RESIDENT").content("댓글 내용")
                .createdAt(LocalDateTime.of(2026, 1, 1, 10, 0))
                .updatedAt(LocalDateTime.of(2026, 1, 1, 10, 0)).build();

        PageResponse<CommentListRes> res = PageResponse.<CommentListRes>builder()
                .content(List.of(item)).page(0).size(20).totalElements(1).totalPages(1).hasNext(false).build();

        given(commentService.getCommentList(eq(1L), any())).willReturn(res);

        mockMvc.perform(get("/api/boards/posts/{postId}/comments", 1L)
                        .param("page", "0").param("size", "20"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("board-comment-list",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Board - 댓글").summary("댓글 목록 조회").build())));
    }
}
