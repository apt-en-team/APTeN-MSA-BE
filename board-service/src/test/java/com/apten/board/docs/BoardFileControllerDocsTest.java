package com.apten.board.docs;

import com.apten.board.application.controller.BoardFileController;
import com.apten.board.application.model.response.FileUploadRes;
import com.apten.board.application.service.BoardFileService;
import com.apten.board.domain.repository.BoardFileRepository;
import com.apten.board.domain.repository.NoticeFileRepository;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// 게시판 파일 API 문서화 테스트
@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(controllers = BoardFileController.class)
@TestPropertySource(properties = "file.upload-dir=/tmp/apten-test")
class BoardFileControllerDocsTest {

    private MockMvc mockMvc;

    @MockBean
    @SuppressWarnings("unused")
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @MockBean
    private BoardFileService boardFileService;

    @MockBean
    private BoardFileRepository boardFileRepository;

    @MockBean
    private NoticeFileRepository noticeFileRepository;

    @BeforeEach
    void setUp(WebApplicationContext ctx, RestDocumentationContextProvider restDocs) {
        new ObjectMapper().registerModule(new JavaTimeModule());
        mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
                .apply(MockMvcRestDocumentation.documentationConfiguration(restDocs))
                .build();
    }

    @Test
    void 게시글_첨부파일_업로드() throws Exception {
        FileUploadRes res = FileUploadRes.builder()
                .files(List.of()).uploadedAt(LocalDateTime.of(2026, 1, 1, 10, 0)).build();

        given(boardFileService.uploadBoardFiles(any())).willReturn(res);

        MockMultipartFile file = new MockMultipartFile(
                "files", "test.jpg", MediaType.IMAGE_JPEG_VALUE, "test image".getBytes());

        mockMvc.perform(multipart("/api/files")
                        .file(file)
                        .param("postId", "1"))
                .andExpect(status().isCreated())
                .andDo(MockMvcRestDocumentationWrapper.document("board-file-upload",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Board - 파일").summary("게시글 첨부파일 업로드").build())));
    }

    @Test
    void 공지_첨부파일_업로드() throws Exception {
        FileUploadRes res = FileUploadRes.builder()
                .files(List.of()).uploadedAt(LocalDateTime.of(2026, 1, 1, 10, 0)).build();

        given(boardFileService.uploadNoticeFiles(anyLong(), anyList())).willReturn(res);

        MockMultipartFile file = new MockMultipartFile(
                "files", "notice.pdf", MediaType.APPLICATION_PDF_VALUE, "test pdf".getBytes());

        mockMvc.perform(multipart("/api/files/notices")
                        .file(file)
                        .param("noticeId", "1"))
                .andExpect(status().isCreated())
                .andDo(MockMvcRestDocumentationWrapper.document("notice-file-upload",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Board - 파일").summary("공지 첨부파일 업로드").build())));
    }
}
