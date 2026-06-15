package com.apten.board.docs;

import com.apten.board.application.controller.FreeBoardController;
import com.apten.board.application.model.response.*;
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

// 게시글 API 문서화 테스트
@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(controllers = FreeBoardController.class)
class FreeBoardControllerDocsTest {

    private MockMvc mockMvc;

    @MockBean
    @SuppressWarnings("unused")
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @MockBean
    private FreeBoardService freeBoardService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp(WebApplicationContext ctx, RestDocumentationContextProvider restDocs) {
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
                .apply(MockMvcRestDocumentation.documentationConfiguration(restDocs))
                .build();
    }

    @Test
    void 게시글_작성() throws Exception {
        Map<String, Object> req = Map.of(
                "category", "FREE",
                "title", "테스트 게시글",
                "content", "게시글 내용입니다.",
                "files", List.of()
        );

        PostCreateRes res = PostCreateRes.builder()
                .postId(1L).title("테스트 게시글")
                .createdAt(LocalDateTime.of(2026, 1, 1, 10, 0)).build();

        given(freeBoardService.createPost(any())).willReturn(res);

        mockMvc.perform(post("/api/boards/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andDo(MockMvcRestDocumentationWrapper.document("board-post-create",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Board - 게시글").summary("게시글 작성").build())));
    }

    @Test
    void 게시글_목록_조회() throws Exception {
        PostListRes item = PostListRes.builder()
                .postId(1L).userId(100L).writerName("홍길동")
                .category(BoardCategory.FREE).title("테스트 게시글").preview("내용 미리보기")
                .viewCount(10).likeCount(3).commentCount(2L).isDeleted(false)
                .createdAt(LocalDateTime.of(2026, 1, 1, 10, 0)).hasFile(false).thumbSavedName(null)
                .build();

        PageResponse<PostListRes> res = PageResponse.<PostListRes>builder()
                .content(List.of(item)).page(0).size(20).totalElements(1).totalPages(1).hasNext(false).build();

        given(freeBoardService.getPostList(any())).willReturn(res);

        mockMvc.perform(get("/api/boards/posts").param("page", "0").param("size", "20"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("board-post-list",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Board - 게시글").summary("게시글 목록 조회").build())));
    }

    @Test
    void 게시글_상세_조회() throws Exception {
        PostDetailRes res = PostDetailRes.builder()
                .postId(1L).complexId(100L).userId(200L).writerName("홍길동")
                .category(BoardCategory.FREE).title("테스트 게시글").content("게시글 내용")
                .viewCount(10).likeCount(3).liked(false)
                .createdAt(LocalDateTime.of(2026, 1, 1, 10, 0))
                .updatedAt(LocalDateTime.of(2026, 1, 2, 10, 0))
                .isDeleted(false).files(List.of()).build();

        given(freeBoardService.getPostDetail(eq(1L))).willReturn(res);

        mockMvc.perform(get("/api/boards/posts/{postId}", 1L))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("board-post-detail",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Board - 게시글").summary("게시글 상세 조회").build())));
    }

    @Test
    void 게시글_수정() throws Exception {
        Map<String, String> req = Map.of("title", "수정된 제목", "content", "수정된 내용");

        PostPatchRes res = PostPatchRes.builder()
                .postId(1L).title("수정된 제목").content("수정된 내용")
                .updatedAt(LocalDateTime.of(2026, 1, 3, 10, 0)).build();

        given(freeBoardService.updatePost(eq(1L), any())).willReturn(res);

        mockMvc.perform(patch("/api/boards/posts/{postId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("board-post-update",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Board - 게시글").summary("게시글 수정").build())));
    }

    @Test
    void 게시글_삭제() throws Exception {
        PostDeleteRes res = PostDeleteRes.builder()
                .postId(1L).deletedAt(LocalDateTime.of(2026, 1, 4, 10, 0)).build();

        given(freeBoardService.deletePost(eq(1L))).willReturn(res);

        mockMvc.perform(delete("/api/boards/posts/{postId}", 1L))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("board-post-delete",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Board - 게시글").summary("게시글 삭제").build())));
    }

    @Test
    void 좋아요_토글() throws Exception {
        PostLikeToggleRes res = PostLikeToggleRes.builder()
                .postId(1L).liked(true).likeCount(4).build();

        given(freeBoardService.togglePostLike(eq(1L))).willReturn(res);

        mockMvc.perform(post("/api/boards/posts/{postId}/likes/toggle", 1L))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("board-post-like-toggle",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Board - 게시글").summary("좋아요 등록/취소").build())));
    }

    @Test
    void 내_게시글_조회() throws Exception {
        MyPostListRes item = MyPostListRes.builder()
                .postId(1L).writerName("홍길동").title("내 게시글").preview("내용")
                .viewCount(5).likeCount(1).createdAt(LocalDateTime.of(2026, 1, 1, 10, 0))
                .thumbSavedName(null).hasFile(false).build();

        PageResponse<MyPostListRes> res = PageResponse.<MyPostListRes>builder()
                .content(List.of(item)).page(0).size(20).totalElements(1).totalPages(1).hasNext(false).build();

        given(freeBoardService.getMyPostList(any())).willReturn(res);

        mockMvc.perform(get("/api/boards/my-posts").param("page", "0").param("size", "20"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("board-my-post-list",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Board - 게시글").summary("내 게시글 조회").build())));
    }

    @Test
    void 인기글_조회() throws Exception {
        PopularPostListRes item = PopularPostListRes.builder()
                .postId(1L).title("인기글").writerName("홍길동")
                .commentCount(10).likeCount(20).viewCount(100)
                .createdAt(LocalDateTime.of(2026, 1, 1, 10, 0)).hasFile(false).build();

        given(freeBoardService.getPopularPostList(any())).willReturn(List.of(item));

        mockMvc.perform(get("/api/boards/posts/popular").param("size", "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("board-popular-post-list",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Board - 게시글").summary("인기글 조회").build())));
    }
}
