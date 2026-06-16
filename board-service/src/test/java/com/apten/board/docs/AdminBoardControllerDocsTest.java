package com.apten.board.docs;

import com.apten.board.application.controller.AdminBoardController;
import com.apten.board.application.model.response.*;
import com.apten.board.application.service.CommentService;
import com.apten.board.application.service.FreeBoardService;
import com.apten.board.domain.enums.BoardCategory;
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

// 관리자 게시판 API 문서화 테스트
@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(controllers = AdminBoardController.class)
class AdminBoardControllerDocsTest {

    private MockMvc mockMvc;

    @MockBean
    @SuppressWarnings("unused")
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @MockBean
    private FreeBoardService freeBoardService;

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
    void 관리자_댓글_목록_조회() throws Exception {
        CommentListRes item = CommentListRes.builder()
                .commentId(10L).postId(1L).userId(100L).writerName("홍길동").userRole("RESIDENT")
                .content("댓글 내용").createdAt(LocalDateTime.of(2026, 1, 1, 10, 0))
                .updatedAt(LocalDateTime.of(2026, 1, 1, 10, 0)).build();

        PageResponse<CommentListRes> res = PageResponse.<CommentListRes>builder()
                .content(List.of(item)).page(0).size(100).totalElements(1).totalPages(1).hasNext(false).build();

        given(commentService.getCommentList(eq(1L), any())).willReturn(res);

        mockMvc.perform(get("/api/admin/boards/posts/{postId}/comments", 1L))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-board-comment-list",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 게시판").summary("관리자 댓글 목록 조회").build())));
    }

    @Test
    void 관리자_댓글_작성() throws Exception {
        Map<String, String> req = Map.of("content", "관리자 댓글입니다.");

        CommentCreateRes res = CommentCreateRes.builder()
                .commentId(10L).postId(1L).content("관리자 댓글입니다.")
                .createdAt(LocalDateTime.of(2026, 1, 1, 10, 0)).build();

        given(commentService.createComment(eq(1L), any())).willReturn(res);

        mockMvc.perform(post("/api/admin/boards/posts/{postId}/comments", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-board-comment-create",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 게시판").summary("관리자 댓글 작성").build())));
    }

    @Test
    void 게시글_강제_삭제() throws Exception {
        AdminPostDeleteRes res = AdminPostDeleteRes.builder()
                .postId(1L).deletedAt(LocalDateTime.of(2026, 1, 3, 10, 0)).build();

        given(freeBoardService.forceDeletePost(eq(1L))).willReturn(res);

        mockMvc.perform(delete("/api/admin/boards/posts/{postId}", 1L))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-board-post-force-delete",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 게시판").summary("게시글 강제 삭제").build())));
    }

    @Test
    void 댓글_강제_삭제() throws Exception {
        AdminCommentDeleteRes res = AdminCommentDeleteRes.builder()
                .commentId(10L).deletedAt(LocalDateTime.of(2026, 1, 3, 10, 0)).build();

        given(commentService.forceDeleteComment(eq(10L))).willReturn(res);

        mockMvc.perform(delete("/api/admin/boards/comments/{commentId}", 10L))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-board-comment-force-delete",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 게시판").summary("댓글 강제 삭제").build())));
    }

    @Test
    void 관리자_게시글_상세_조회() throws Exception {
        PostDetailRes res = PostDetailRes.builder()
                .postId(1L).complexId(100L).userId(200L).writerName("홍길동")
                .category(BoardCategory.FREE).title("게시글 제목").content("게시글 내용")
                .viewCount(10).likeCount(3).liked(false)
                .createdAt(LocalDateTime.of(2026, 1, 1, 10, 0))
                .updatedAt(LocalDateTime.of(2026, 1, 2, 10, 0))
                .isDeleted(false).files(List.of()).build();

        given(freeBoardService.getAdminPostDetail(eq(1L))).willReturn(res);

        mockMvc.perform(get("/api/admin/boards/posts/{postId}", 1L))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-board-post-detail",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 게시판").summary("관리자 게시글 상세 조회").build())));
    }

    @Test
    void 관리자_게시글_목록_조회() throws Exception {
        PostListRes item = PostListRes.builder()
                .postId(1L).userId(200L).writerName("홍길동").category(BoardCategory.FREE)
                .title("게시글 제목").preview("내용 미리보기").viewCount(10).likeCount(3)
                .commentCount(2L).isDeleted(false).createdAt(LocalDateTime.of(2026, 1, 1, 10, 0))
                .hasFile(false).build();

        PageResponse<PostListRes> res = PageResponse.<PostListRes>builder()
                .content(List.of(item)).page(0).size(10).totalElements(1).totalPages(1).hasNext(false).build();

        given(freeBoardService.getAdminPostList(any())).willReturn(res);

        mockMvc.perform(get("/api/admin/boards/posts").param("page", "0").param("size", "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-board-post-list",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 게시판").summary("관리자 게시글 목록 조회").build())));
    }

    @Test
    void 게시판_통계_조회() throws Exception {
        BoardStatisticsRes res = BoardStatisticsRes.builder()
                .postCount(100L).commentCount(300L).noticeCount(20L).voteCount(5L).deletedPostCount(3L).build();

        given(freeBoardService.getBoardStatistics(any())).willReturn(res);

        mockMvc.perform(get("/api/admin/boards/statistics"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-board-statistics",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 게시판").summary("게시판 통계 조회").build())));
    }
}
