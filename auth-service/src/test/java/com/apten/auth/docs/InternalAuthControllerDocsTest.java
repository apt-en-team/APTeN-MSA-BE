package com.apten.auth.docs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.apten.auth.application.controller.InternalAuthController;
import com.apten.auth.application.model.response.InternalAdminCreateRes;
import com.apten.auth.application.model.response.InternalAdminDeleteRes;
import com.apten.auth.application.model.response.InternalAdminPatchRes;
import com.apten.auth.application.service.AdminService;
import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceDocumentation;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import java.time.LocalDateTime;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class InternalAuthControllerDocsTest extends AuthDocsTestBase {

    @Mock
    private AdminService adminService;

    @InjectMocks
    private InternalAuthController internalAuthController;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocs) {
        mockMvc = MockMvcBuilders.standaloneSetup(internalAuthController)
                .apply(MockMvcRestDocumentation.documentationConfiguration(restDocs))
                .build();
    }

    @Test
    void 내부_관리자_생성() throws Exception {
        given(adminService.createAdminInternal(any())).willReturn(
                InternalAdminCreateRes.builder()
                        .userId(20L).email("internal@test.com").name("내부관리자")
                        .phone("010-0000-1111").role("ADMIN").adminRole("MANAGER")
                        .createdAt(LocalDateTime.now()).build()
        );

        Map<String, Object> req = Map.of(
                "email", "internal@test.com", "password", "Internal1!",
                "name", "내부관리자", "phone", "010-0000-1111",
                "complexId", 1, "adminRole", "MANAGER"
        );
        mockMvc.perform(post("/internal/auth/admins")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andDo(MockMvcRestDocumentationWrapper.document("internal-auth-admin-create",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Internal Auth").summary("내부 관리자 생성").build())));
    }

    @Test
    void 내부_관리자_수정() throws Exception {
        given(adminService.updateAdminInternal(anyLong(), any())).willReturn(
                InternalAdminPatchRes.builder()
                        .userId(20L).adminRole("ADMIN").status("ACTIVE")
                        .updatedAt(LocalDateTime.now()).build()
        );

        Map<String, Object> req = Map.of("name", "수정된이름", "adminRole", "ADMIN");
        mockMvc.perform(patch("/internal/auth/admins/{userId}", 20L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("internal-auth-admin-patch",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Internal Auth").summary("내부 관리자 수정").build())));
    }

    @Test
    void 내부_관리자_삭제() throws Exception {
        given(adminService.deleteAdminInternal(anyLong())).willReturn(
                InternalAdminDeleteRes.builder()
                        .userId(20L).status("INACTIVE").isDeleted(true)
                        .deletedAt(LocalDateTime.now()).build()
        );

        mockMvc.perform(patch("/internal/auth/admins/{userId}/delete", 20L))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("internal-auth-admin-delete",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Internal Auth").summary("내부 관리자 삭제").build())));
    }
}
