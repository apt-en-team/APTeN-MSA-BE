package com.apten.auth.docs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.apten.auth.application.controller.UserMeController;
import com.apten.auth.application.model.response.UserDeleteRes;
import com.apten.auth.application.model.response.UserMeRes;
import com.apten.auth.application.model.response.UserPatchRes;
import com.apten.auth.application.model.response.UserPasswordPatchRes;
import com.apten.auth.application.service.UserAccountService;
import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceDocumentation;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import java.time.LocalDate;
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

class UserMeControllerDocsTest extends AuthDocsTestBase {

    @Mock
    private UserAccountService userAccountService;

    @InjectMocks
    private UserMeController userMeController;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocs) {
        mockMvc = MockMvcBuilders.standaloneSetup(userMeController)
                .apply(MockMvcRestDocumentation.documentationConfiguration(restDocs))
                .build();
    }

    @Test
    void 내_계정_정보_조회() throws Exception {
        given(userAccountService.getMyInfo(anyLong())).willReturn(
                UserMeRes.builder()
                        .userId(1L).email("user@test.com").name("홍길동").phone("010-1234-5678")
                        .birthDate(LocalDate.of(1990, 1, 1)).building("101").unit("101")
                        .role("USER").status("ACTIVE").signupType("EMAIL")
                        .complexId(10L).createdAt(LocalDateTime.now()).build()
        );

        mockMvc.perform(get("/api/users/me").header("X-User-Id", "1"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("user-me-get",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("User").summary("내 계정 정보 조회").build())));
    }

    @Test
    void 내_계정_정보_수정() throws Exception {
        given(userAccountService.updateMyInfo(anyLong(), any())).willReturn(
                UserPatchRes.builder().name("홍길동수정").phone("010-9999-8888").message("계정 정보 수정 성공").build()
        );

        Map<String, Object> req = Map.of("name", "홍길동수정", "phone", "010-9999-8888");
        mockMvc.perform(patch("/api/users/me")
                        .header("X-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("user-me-patch",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("User").summary("내 계정 정보 수정").build())));
    }

    @Test
    void 내_비밀번호_변경() throws Exception {
        given(userAccountService.changePassword(anyLong(), any())).willReturn(
                UserPasswordPatchRes.builder().message("비밀번호 변경 성공").build()
        );

        Map<String, Object> req = Map.of("currentPassword", "Current1!", "newPassword", "NewPass1!");
        mockMvc.perform(patch("/api/users/me/password")
                        .header("X-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("user-me-password-patch",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("User").summary("내 비밀번호 변경").build())));
    }

    @Test
    void 회원_탈퇴() throws Exception {
        given(userAccountService.deleteMyAccount(anyLong(), any())).willReturn(
                UserDeleteRes.builder().message("회원 탈퇴 성공").build()
        );

        Map<String, Object> req = Map.of("password", "Current1!");
        mockMvc.perform(delete("/api/users/me")
                        .header("X-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("user-me-delete",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("User").summary("회원 탈퇴").build())));
    }
}
