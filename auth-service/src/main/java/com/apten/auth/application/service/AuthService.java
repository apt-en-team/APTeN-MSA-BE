package com.apten.auth.application.service;

import com.apten.auth.application.mapper.AuthUserMapper;
import com.apten.auth.application.model.request.AuthCheckEmailReq;
import com.apten.auth.application.model.request.AuthLoginPostReq;
import com.apten.auth.application.model.request.AuthPasswordForgotPostReq;
import com.apten.auth.application.model.request.AuthPasswordResetPostReq;
import com.apten.auth.application.model.request.AuthRegisterPostReq;
import com.apten.auth.application.model.request.AuthSmsSendPostReq;
import com.apten.auth.application.model.request.AuthSmsVerifyPostReq;
import com.apten.auth.application.model.request.AuthSocialSignupPostReq;
import com.apten.auth.application.model.request.AuthTokenRefreshPostReq;
import com.apten.auth.application.model.response.AuthCheckEmailRes;
import com.apten.auth.application.model.response.AuthLoginPostRes;
import com.apten.auth.application.model.response.AuthLogoutPostRes;
import com.apten.auth.application.model.response.AuthPasswordForgotPostRes;
import com.apten.auth.application.model.response.AuthPasswordResetPostRes;
import com.apten.auth.application.model.response.AuthRegisterPostRes;
import com.apten.auth.application.model.response.AuthSmsSendPostRes;
import com.apten.auth.application.model.response.AuthSmsVerifyPostRes;
import com.apten.auth.application.model.response.AuthSocialSignupPostRes;
import com.apten.auth.application.model.response.AuthTokenRefreshPostRes;
import com.apten.auth.domain.enums.UserRole;
import com.apten.auth.domain.enums.UserStatus;
import com.apten.auth.domain.repository.UserRepository;
import com.apten.auth.infrastructure.mapper.AuthQueryMapper;
import com.apten.auth.security.JwtTokenProvider;
import com.apten.auth.security.UserPrincipal;
import com.apten.common.security.UserContext;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

// 인증 응용 서비스
// 로그인과 회원가입, 토큰, 비밀번호, SMS 인증 시그니처를 이 서비스에 모아둔다
@Service
@RequiredArgsConstructor
public class AuthService {

    // 회원 기본 저장소
    private final UserRepository userRepository;

    // 인증 조회용 MyBatis 매퍼
    private final ObjectProvider<AuthQueryMapper> authQueryMapperProvider;

    // principal과 응답 변환기
    private final AuthUserMapper authUserMapper;

    // JWT 발급기
    private final JwtTokenProvider jwtTokenProvider;

    // 이메일 로그인 서비스
    public AuthLoginPostRes login(AuthLoginPostReq request) {
        // TODO: 이메일 로그인 로직 구현
        return AuthLoginPostRes.builder()
                .accessToken("access-token")
                .refreshToken("refresh-token")
                .userId(1L)
                .name(request.getEmail())
                .role("RESIDENT")
                .status("ACTIVE")
                .build();
    }

    // 로그아웃 서비스
    public AuthLogoutPostRes logout() {
        // TODO: Refresh Token 무효화 처리
        // TODO: Access Token 블랙리스트 등록 처리
        return AuthLogoutPostRes.builder()
                .message("로그아웃 완료")
                .build();
    }

    // 이메일 회원가입 서비스
    public AuthRegisterPostRes register(AuthRegisterPostReq request) {
        // TODO: 회원가입 저장 로직 구현
        // TODO: 회원가입 요청 이벤트 Kafka 발행
        return AuthRegisterPostRes.builder()
                .apartmentComplexUid(request.getApartmentComplexUid())
                .email(request.getEmail())
                .name(request.getName())
                .role("RESIDENT")
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .build();
    }

    // 소셜 회원가입 서비스
    public AuthSocialSignupPostRes socialSignup(AuthSocialSignupPostReq request) {
        // TODO: 소셜 회원가입 저장 로직 구현
        // TODO: 회원가입 요청 이벤트 Kafka 발행
        return AuthSocialSignupPostRes.builder()
                .email(request.getEmail())
                .name(request.getName())
                .role("RESIDENT")
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .build();
    }

    // 토큰 재발급 서비스
    public AuthTokenRefreshPostRes refreshToken(AuthTokenRefreshPostReq request) {
        // TODO: Refresh Token 검증 및 재발급 로직 구현
        return AuthTokenRefreshPostRes.builder()
                .accessToken("refreshed-access-token")
                .refreshToken(request.getRefreshToken())
                .build();
    }

    // 비밀번호 재설정 메일 발송 서비스
    public AuthPasswordForgotPostRes sendPasswordResetMail(AuthPasswordForgotPostReq request) {
        // TODO: 비밀번호 재설정 메일 발송 로직 구현
        return AuthPasswordForgotPostRes.builder()
                .message("비밀번호 재설정 메일 발송 완료")
                .build();
    }

    // 비밀번호 재설정 서비스
    public AuthPasswordResetPostRes resetPassword(AuthPasswordResetPostReq request) {
        // TODO: 비밀번호 재설정 로직 구현
        return AuthPasswordResetPostRes.builder()
                .message("비밀번호 재설정 완료")
                .build();
    }

    // SMS 인증번호 발송 서비스
    public AuthSmsSendPostRes sendSmsCode(AuthSmsSendPostReq request) {
        // TODO: SMS 인증번호 발송 로직 구현
        return AuthSmsSendPostRes.builder()
                .message("SMS 인증번호 발송 완료")
                .build();
    }

    // SMS 인증번호 검증 서비스
    public AuthSmsVerifyPostRes verifySmsCode(AuthSmsVerifyPostReq request) {
        // TODO: SMS 인증번호 검증 로직 구현
        return AuthSmsVerifyPostRes.builder()
                .verified(true)
                .build();
    }

    // 이메일 중복 확인 서비스
    public AuthCheckEmailRes checkEmailDuplicate(AuthCheckEmailReq request) {
        // TODO: 이메일 중복 확인 로직 구현
        return AuthCheckEmailRes.builder()
                .isDuplicate(false)
                .build();
    }

    // OAuth2 성공 핸들러가 호출하는 JWT 응답 생성 서비스
    public AuthLoginPostRes issueTokenResponse(UserPrincipal userPrincipal) {
        UserContext userContext = authUserMapper.toUserContext(userPrincipal);
        String accessToken = jwtTokenProvider.issueAccessToken(userContext);
        String refreshToken = jwtTokenProvider.issueRefreshToken(userPrincipal.getUserId());
        return authUserMapper.toLoginResponse(accessToken, refreshToken, userPrincipal);
    }

    // TODO: 내 계정 정보 조회는 API 명세 확정 후 추가
    // TODO: 내 계정 정보 수정은 API 명세 확정 후 추가
    // TODO: 회원 상태 변경 이벤트 수신 후 ACTIVE 또는 REJECTED 반영
}
