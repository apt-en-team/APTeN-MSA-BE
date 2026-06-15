package com.apten.board.docs;

import com.apten.board.application.controller.NoticeController;
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
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// 공지 조회 API 문서화 테스트
@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(controllers = NoticeController.class)
class NoticeControllerDocsTest {

    private MockMvc mockMvc;

    @MockBean
    @SuppressWarnings("unused")
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @MockBean
    private NoticeService noticeService;

    @BeforeEach
    void setUp(WebApplicationContext ctx, RestDocumentationContextProvider restDocs) {
        new ObjectMapper().registerModule(new JavaTimeModule());
        mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
                .apply(MockMvcRestDocumentation.documentationConfiguration(restDocs))
                .build();
    }

    @Test
    void 공지_목록_조회() throws Exception {
        NoticeListRes item = NoticeListRes.builder()
                .noticeId(1L).title("단지 공지사항").writerName("관리자")
                .createdAt(LocalDateTime.of(2026, 1, 1, 10, 0))
                .hasFile(false).isDeleted(false).build();

        PageResponse<NoticeListRes> res = PageResponse.<NoticeListRes>builder()
                .content(List.of(item)).page(0).size(20).totalElements(1).totalPages(1).hasNext(false).build();

        given(noticeService.getNoticeList(any())).willReturn(res);

        mockMvc.perform(get("/api/notices").param("page", "0").param("size", "20"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("notice-list",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Notice - 공지").summary("공지 목록 조회").build())));
    }

    @Test
    void 공지_상세_조회() throws Exception {
        NoticeDetailRes res = NoticeDetailRes.builder()
                .noticeId(1L).complexId(100L).userId(200L).writerName("관리자")
                .title("단지 공지사항").content("공지 내용입니다.")
                .createdAt(LocalDateTime.of(2026, 1, 1, 10, 0))
                .updatedAt(LocalDateTime.of(2026, 1, 2, 10, 0))
                .isDeleted(false).files(List.of()).build();

        given(noticeService.getNoticeDetail(eq(1L))).willReturn(res);

        mockMvc.perform(get("/api/notices/{noticeId}", 1L))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("notice-detail",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Notice - 공지").summary("공지 상세 조회").build())));
    }
}
