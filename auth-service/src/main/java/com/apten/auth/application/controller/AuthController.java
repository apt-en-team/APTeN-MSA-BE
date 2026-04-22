package com.apten.auth.application.controller;

import com.apten.auth.application.model.response.AuthTokenResponse;
import com.apten.auth.application.service.AuthService;
import com.apten.common.constants.SecurityConstants;
import com.apten.common.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 인증 관련 HTTP 요청이 처음 들어오는 지점
// 클라이언트가 auth-service 응답 형식과 로그인 흐름을 확인할 때 가장 먼저 보게 되는 컨트롤러다
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    // 실제 토큰 응답 조합은 서비스 계층에 위임
    private final AuthService authService;

    // Authorization 헤더를 받아 auth-service 토큰 응답 형식을 확인하는 최소 엔드포인트
    @GetMapping("/tokens/sample")
    public ResultResponse<AuthTokenResponse> sampleTokenResponse(
            @RequestHeader(name = SecurityConstants.AUTHORIZATION_HEADER, required = false) String authorizationHeader
    ) {
        return ResultResponse.success(
                "auth token response ready",
                authService.createSampleTokenResponse(authorizationHeader)
        );
    }

    // auth-service의 기본 application 계층과 응답 포맷이 정상인지 확인하는 최소 엔드포인트
    @GetMapping("/template")
    public ResultResponse<AuthTokenResponse> getAuthTemplate() {
        return ResultResponse.success("auth template ready", authService.getAuthTemplate());
    }
}
