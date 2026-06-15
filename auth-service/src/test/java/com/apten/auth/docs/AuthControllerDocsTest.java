package com.apten.auth.docs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.apten.auth.application.controller.AuthController;
import com.apten.auth.application.model.response.*;
import com.apten.auth.application.service.AuthService;
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

class AuthControllerDocsTest extends AuthDocsTestBase {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocs) {
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .apply(MockMvcRestDocumentation.documentationConfiguration(restDocs))
                .build();
    }

    @Test
    void 이메일_로그인() throws Exception {
        given(authService.login(any())).willReturn(
                AuthLoginPostRes.builder()
                        .accessToken("eyJhbGciOiJIUzI1NiJ9.access").refreshToken("eyJhbGciOiJIUzI1NiJ9.refresh")
                        .userId(1L).userUid("uid-001").name("홍길동")
                        .role("USER").status("ACTIVE").building("101").unit("101").complexId(10L).build()
        );

        Map<String, Object> req = Map.of("email", "user@test.com", "password", "Password1!");
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("auth-login",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Auth").summary("이메일 로그인").build())));
    }

    @Test
    void 로그아웃() throws Exception {
        given(authService.logout(any())).willReturn(
                AuthLogoutPostRes.builder().message("로그아웃 성공").build()
        );

        // epages 라이브러리가 Authorization 헤더를 JWT로 파싱함 — 유효한 3-part 형식 사용
        mockMvc.perform(post("/api/auth/logout")
                        .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("auth-logout",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Auth").summary("로그아웃").build())));
    }

    @Test
    void 이메일_회원가입() throws Exception {
        given(authService.register(any())).willReturn(
                AuthRegisterPostRes.builder()
                        .complexId(10L).userId(1L).userUid("uid-001").email("user@test.com")
                        .name("홍길동").role("USER").status("ACTIVE").createdAt(LocalDateTime.now()).build()
        );

        Map<String, Object> req = Map.of(
                "apartmentComplexUid", "complex-uid-001", "email", "user@test.com",
                "password", "Password1!", "name", "홍길동", "phone", "010-1234-5678",
                "birthDate", "1990-01-01", "dong", "101", "ho", "101", "authCode", "123456"
        );
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andDo(MockMvcRestDocumentationWrapper.document("auth-register",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Auth").summary("이메일 회원가입").build())));
    }

    @Test
    void 소셜_회원가입() throws Exception {
        given(authService.socialSignup(any())).willReturn(
                AuthSocialSignupPostRes.builder()
                        .complexId(10L).userId(2L).userUid("uid-002").email("social@test.com")
                        .name("김소셜").role("USER").status("ACTIVE").createdAt(LocalDateTime.now()).build()
        );

        Map<String, Object> req = Map.of(
                "provider", "구글", "providerUserId", "google-uid-123",
                "apartmentComplexUid", "complex-uid-001", "email", "social@test.com",
                "name", "김소셜", "phone", "010-9999-8888", "birthDate", "1995-06-15",
                "dong", "102", "ho", "201", "authCode", "654321"
        );
        mockMvc.perform(post("/api/auth/social/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andDo(MockMvcRestDocumentationWrapper.document("auth-social-signup",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Auth").summary("소셜 회원가입 추가 정보 입력").build())));
    }

    @Test
    void 토큰_재발급() throws Exception {
        given(authService.refreshToken(any())).willReturn(
                AuthTokenRefreshPostRes.builder()
                        .accessToken("eyJhbGciOiJIUzI1NiJ9.new-access")
                        .refreshToken("eyJhbGciOiJIUzI1NiJ9.new-refresh").build()
        );

        Map<String, Object> req = Map.of("refreshToken", "eyJhbGciOiJIUzI1NiJ9.refresh");
        mockMvc.perform(post("/api/auth/token/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("auth-token-refresh",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Auth").summary("토큰 재발급").build())));
    }

    @Test
    void 비밀번호_재설정_메일_발송() throws Exception {
        given(authService.sendPasswordResetMail(any())).willReturn(
                AuthPasswordForgotPostRes.builder().message("재설정 메일 발송 완료").build()
        );

        Map<String, Object> req = Map.of("email", "user@test.com");
        mockMvc.perform(post("/api/auth/password/forgot")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("auth-password-forgot",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Auth").summary("비밀번호 재설정 메일 발송").build())));
    }

    @Test
    void 비밀번호_재설정() throws Exception {
        given(authService.resetPassword(any())).willReturn(
                AuthPasswordResetPostRes.builder().message("비밀번호 재설정 완료").build()
        );

        Map<String, Object> req = Map.of("token", "reset-token-abc", "newPassword", "NewPass1!");
        mockMvc.perform(post("/api/auth/password/reset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("auth-password-reset",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Auth").summary("비밀번호 재설정").build())));
    }

    @Test
    void SMS_인증번호_발송() throws Exception {
        given(authService.sendSmsCode(any())).willReturn(
                AuthSmsSendPostRes.builder().message("인증번호 발송 완료").build()
        );

        Map<String, Object> req = Map.of("name", "홍길동", "phone", "010-1234-5678", "birthDate", "1990-01-01");
        mockMvc.perform(post("/api/auth/sms/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("auth-sms-send",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Auth").summary("SMS 인증번호 발송").build())));
    }

    @Test
    void SMS_인증번호_검증() throws Exception {
        given(authService.verifySmsCode(any())).willReturn(
                AuthSmsVerifyPostRes.builder().verified(true).build()
        );

        Map<String, Object> req = Map.of("phone", "010-1234-5678", "code", "123456");
        mockMvc.perform(post("/api/auth/sms/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("auth-sms-verify",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Auth").summary("SMS 인증번호 검증").build())));
    }

    @Test
    void 이메일_중복_확인() throws Exception {
        given(authService.checkEmailDuplicate(any())).willReturn(
                AuthCheckEmailRes.builder().isDuplicate(false).build()
        );

        mockMvc.perform(get("/api/auth/check-email").param("email", "user@test.com"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("auth-check-email",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Auth").summary("이메일 중복 확인").build())));
    }
}
