package com.apten.board.docs;

import com.apten.board.application.controller.AdminNoticeController;
import com.apten.board.application.model.response.*;
import com.apten.board.application.service.NoticeService;
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

// 관리자 공지 API 문서화 테스트
@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(controllers = AdminNoticeController.class)
class AdminNoticeControllerDocsTest {

    private MockMvc mockMvc;

    @MockBean
    @SuppressWarnings("unused")
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @MockBean
    private NoticeService noticeService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp(WebApplicationContext ctx, RestDocumentationContextProvider restDocs) {
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
                .apply(MockMvcRestDocumentation.documentationConfiguration(restDocs))
                .build();
    }

    @Test
    void 공지_작성() throws Exception {
        Map<String, String> req = Map.of("title", "공지사항 제목", "content", "공지 내용입니다.");

        NoticeCreateRes res = NoticeCreateRes.builder()
                .noticeId(1L).title("공지사항 제목")
                .createdAt(LocalDateTime.of(2026, 1, 1, 10, 0)).build();

        given(noticeService.createNotice(any())).willReturn(res);

        mockMvc.perform(post("/api/admin/notices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-notice-create",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 공지").summary("공지 작성").build())));
    }

    @Test
    void 공지_수정() throws Exception {
        Map<String, String> req = Map.of("title", "수정된 공지", "content", "수정된 내용");

        NoticePatchRes res = NoticePatchRes.builder()
                .noticeId(1L).title("수정된 공지").content("수정된 내용")
                .updatedAt(LocalDateTime.of(2026, 1, 2, 10, 0)).build();

        given(noticeService.updateNotice(eq(1L), any())).willReturn(res);

        mockMvc.perform(patch("/api/admin/notices/{noticeId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-notice-update",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 공지").summary("공지 수정").build())));
    }

    @Test
    void 공지_삭제() throws Exception {
        NoticeDeleteRes res = NoticeDeleteRes.builder()
                .noticeId(1L).deletedAt(LocalDateTime.of(2026, 1, 3, 10, 0)).build();

        given(noticeService.deleteNotice(eq(1L))).willReturn(res);

        mockMvc.perform(delete("/api/admin/notices/{noticeId}", 1L))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-notice-delete",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 공지").summary("공지 삭제").build())));
    }

    @Test
    void 관리자_공지_목록_조회() throws Exception {
        NoticeListRes item = NoticeListRes.builder()
                .noticeId(1L).title("공지사항").writerName("관리자")
                .createdAt(LocalDateTime.of(2026, 1, 1, 10, 0))
                .hasFile(false).isDeleted(false).build();

        PageResponse<NoticeListRes> res = PageResponse.<NoticeListRes>builder()
                .content(List.of(item)).page(0).size(20).totalElements(1).totalPages(1).hasNext(false).build();

        given(noticeService.getAdminNoticeList(any())).willReturn(res);

        mockMvc.perform(get("/api/admin/notices").param("page", "0").param("size", "20"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-notice-list",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 공지").summary("관리자 공지 목록 조회").build())));
    }

    @Test
    void 관리자_공지_상세_조회() throws Exception {
        NoticeDetailRes res = NoticeDetailRes.builder()
                .noticeId(1L).complexId(100L).userId(200L).writerName("관리자")
                .title("공지사항").content("공지 내용")
                .createdAt(LocalDateTime.of(2026, 1, 1, 10, 0))
                .updatedAt(LocalDateTime.of(2026, 1, 2, 10, 0))
                .isDeleted(false).files(List.of()).build();

        given(noticeService.getAdminNoticeDetail(eq(1L))).willReturn(res);

        mockMvc.perform(get("/api/admin/notices/{noticeId}", 1L))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-notice-detail",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 공지").summary("관리자 공지 상세 조회").build())));
    }
}
