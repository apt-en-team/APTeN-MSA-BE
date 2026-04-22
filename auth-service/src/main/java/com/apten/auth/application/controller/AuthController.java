package com.apten.auth.application.controller;

import com.apten.auth.application.model.response.AuthTokenResponse;
import com.apten.auth.application.service.AuthService;
import com.apten.common.constants.SecurityConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 인증 관련 HTTP 요청이 처음 들어오는 지점
// 클라이언트가 auth-service 응답 형식을 확인할 때 가장 먼저 보게 되는 컨트롤러
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    // 실제 토큰 응답 조합은 서비스 계층에 위임
    private final AuthService authService;

    // Authorization 헤더를 받아 auth-service가 어떤 토큰 응답을 줄지 확인하는 샘플 엔드포인트
    // 이후 실제 로그인 완료 흐름에서도 같은 DTO 형식을 반환하는 기준점이 된다
    @GetMapping("/tokens/sample")
    public ResponseEntity<AuthTokenResponse> sampleTokenResponse(
            @RequestHeader(name = SecurityConstants.AUTHORIZATION_HEADER, required = false) String authorizationHeader
    ) {
        return ResponseEntity.ok(authService.createSampleTokenResponse(authorizationHeader));
    }
}
