package com.apten.auth.docs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.apten.auth.application.controller.AdminController;
import com.apten.auth.application.model.response.AdminCreateRes;
import com.apten.auth.application.model.response.AdminDeleteRes;
import com.apten.auth.application.model.response.AdminPatchRes;
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

class AdminControllerDocsTest extends AuthDocsTestBase {

    @Mock
    private AdminService adminService;

    @InjectMocks
    private AdminController adminController;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocs) {
        mockMvc = MockMvcBuilders.standaloneSetup(adminController)
                .apply(MockMvcRestDocumentation.documentationConfiguration(restDocs))
                .build();
    }

    @Test
    void MASTER가_MANAGER_계정_생성() throws Exception {
        given(adminService.createManagerByMaster(any(), any(), any())).willReturn(
                AdminCreateRes.builder()
                        .userId(10L).email("manager@test.com").name("매니저").phone("010-1111-2222")
                        .role("MANAGER").complexId(1L).build()
        );

        Map<String, Object> req = Map.of(
                "email", "manager@test.com", "password", "Manager1!",
                "name", "매니저", "phone", "010-1111-2222", "complexId", 1
        );
        mockMvc.perform(post("/api/admin/master/managers")
                        .header("X-User-Id", "1").header("X-User-Role", "MASTER")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-master-create-manager",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin").summary("MASTER → MANAGER 계정 생성").build())));
    }

    @Test
    void MASTER가_ADMIN_계정_생성() throws Exception {
        given(adminService.createAdminByMaster(any(), any(), any())).willReturn(
                AdminCreateRes.builder()
                        .userId(11L).email("admin@test.com").name("어드민").phone("010-3333-4444")
                        .role("ADMIN").complexId(1L).build()
        );

        Map<String, Object> req = Map.of(
                "email", "admin@test.com", "password", "Admin123!",
                "name", "어드민", "phone", "010-3333-4444", "complexId", 1
        );
        mockMvc.perform(post("/api/admin/master/admins")
                        .header("X-User-Id", "1").header("X-User-Role", "MASTER")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-master-create-admin",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin").summary("MASTER → ADMIN 계정 생성").build())));
    }

    @Test
    void MANAGER가_ADMIN_계정_생성() throws Exception {
        given(adminService.createAdminByManager(any(), any(), any())).willReturn(
                AdminCreateRes.builder()
                        .userId(12L).email("admin2@test.com").name("어드민2").phone("010-5555-6666")
                        .role("ADMIN").complexId(2L).build()
        );

        Map<String, Object> req = Map.of(
                "email", "admin2@test.com", "password", "Admin456!",
                "name", "어드민2", "phone", "010-5555-6666", "complexId", 2
        );
        mockMvc.perform(post("/api/admin/manager/admins")
                        .header("X-User-Id", "10").header("X-User-Role", "MANAGER")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-manager-create-admin",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin").summary("MANAGER → ADMIN 계정 생성").build())));
    }

    @Test
    void MASTER가_관리자_계정_수정() throws Exception {
        given(adminService.updateAdminByMaster(any(), any(), anyLong(), any())).willReturn(
                AdminPatchRes.builder()
                        .userId(11L).email("admin@test.com").name("어드민수정").phone("010-7777-8888")
                        .role("ADMIN").complexId(1L).status("ACTIVE").build()
        );

        Map<String, Object> req = Map.of("name", "어드민수정", "phone", "010-7777-8888");
        mockMvc.perform(patch("/api/admin/master/admins/{userId}", 11L)
                        .header("X-User-Id", "1").header("X-User-Role", "MASTER")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-master-patch-admin",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin").summary("MASTER → 관리자 계정 수정").build())));
    }

    @Test
    void MANAGER가_ADMIN_계정_수정() throws Exception {
        given(adminService.updateAdminByManager(any(), any(), anyLong(), any())).willReturn(
                AdminPatchRes.builder()
                        .userId(12L).email("admin2@test.com").name("어드민수정2").phone("010-9999-0000")
                        .role("ADMIN").complexId(2L).status("ACTIVE").build()
        );

        Map<String, Object> req = Map.of("name", "어드민수정2", "phone", "010-9999-0000");
        mockMvc.perform(patch("/api/admin/manager/admins/{userId}", 12L)
                        .header("X-User-Id", "10").header("X-User-Role", "MANAGER")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-manager-patch-admin",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin").summary("MANAGER → ADMIN 계정 수정").build())));
    }

    @Test
    void MASTER가_관리자_계정_비활성() throws Exception {
        given(adminService.deleteAdminByMaster(any(), any(), anyLong())).willReturn(
                AdminDeleteRes.builder().userId(11L).status("INACTIVE").deletedAt(LocalDateTime.now()).build()
        );

        mockMvc.perform(delete("/api/admin/master/admins/{userId}", 11L)
                        .header("X-User-Id", "1").header("X-User-Role", "MASTER"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-master-delete-admin",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin").summary("MASTER → 관리자 계정 비활성").build())));
    }

    @Test
    void MANAGER가_ADMIN_계정_비활성() throws Exception {
        given(adminService.deleteAdminByManager(any(), any(), anyLong())).willReturn(
                AdminDeleteRes.builder().userId(12L).status("INACTIVE").deletedAt(LocalDateTime.now()).build()
        );

        mockMvc.perform(delete("/api/admin/manager/admins/{userId}", 12L)
                        .header("X-User-Id", "10").header("X-User-Role", "MANAGER"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-manager-delete-admin",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin").summary("MANAGER → ADMIN 계정 비활성").build())));
    }
}
