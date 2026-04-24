package com.apten.auth.application.controller;

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
import com.apten.auth.application.service.AuthService;
import com.apten.common.response.ResultResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

// 인증 API 진입점 — 로그인, 회원가입, 토큰, 비밀번호, SMS 인증 처리
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    // 인증 응용 서비스
    private final AuthService authService;

    // 이메일 로그인 API
    @PostMapping("/login")
    public ResultResponse<AuthLoginPostRes> login(@Valid @RequestBody AuthLoginPostReq request) {
        return ResultResponse.success("로그인 성공", authService.login(request));
    }

    // 로그아웃 API — AT 블랙리스트 등록을 위해 Authorization 헤더 필요
    @PostMapping("/logout")
    public ResultResponse<AuthLogoutPostRes> logout(
            @RequestHeader("Authorization") String authorizationHeader) {
        return ResultResponse.success("로그아웃 성공", authService.logout(authorizationHeader));
    }

    // 이메일 회원가입 API
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResultResponse<AuthRegisterPostRes> register(@Valid @RequestBody AuthRegisterPostReq request) {
        return ResultResponse.success("회원가입 성공", authService.register(request));
    }

    // 소셜 회원가입 추가정보 입력 API
    @PostMapping("/social/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public ResultResponse<AuthSocialSignupPostRes> socialSignup(
            @Valid @RequestBody AuthSocialSignupPostReq request) {
        return ResultResponse.success("소셜 회원가입 성공", authService.socialSignup(request));
    }

    // 토큰 재발급 API
    @PostMapping("/token/refresh")
    public ResultResponse<AuthTokenRefreshPostRes> refreshToken(
            @Valid @RequestBody AuthTokenRefreshPostReq request) {
        return ResultResponse.success("토큰 재발급 성공", authService.refreshToken(request));
    }

    // 비밀번호 재설정 메일 발송 API
    @PostMapping("/password/forgot")
    public ResultResponse<AuthPasswordForgotPostRes> sendPasswordResetMail(
            @Valid @RequestBody AuthPasswordForgotPostReq request) {
        return ResultResponse.success("비밀번호 재설정 메일 발송 성공", authService.sendPasswordResetMail(request));
    }

    // 비밀번호 재설정 API
    @PostMapping("/password/reset")
    public ResultResponse<AuthPasswordResetPostRes> resetPassword(
            @Valid @RequestBody AuthPasswordResetPostReq request) {
        return ResultResponse.success("비밀번호 재설정 성공", authService.resetPassword(request));
    }

    // SMS 인증번호 발송 API
    @PostMapping("/sms/send")
    public ResultResponse<AuthSmsSendPostRes> sendSmsCode(@Valid @RequestBody AuthSmsSendPostReq request) {
        return ResultResponse.success("SMS 인증번호 발송 성공", authService.sendSmsCode(request));
    }

    // SMS 인증번호 검증 API
    @PostMapping("/sms/verify")
    public ResultResponse<AuthSmsVerifyPostRes> verifySmsCode(
            @Valid @RequestBody AuthSmsVerifyPostReq request) {
        return ResultResponse.success("SMS 인증 성공", authService.verifySmsCode(request));
    }

    // 이메일 중복 확인 API
    @GetMapping("/check-email")
    public ResultResponse<AuthCheckEmailRes> checkEmailDuplicate(
            @ModelAttribute AuthCheckEmailReq request) {
        return ResultResponse.success("이메일 중복 확인 성공", authService.checkEmailDuplicate(request));
    }
}