package com.apten.auth.application.controller;

import com.apten.auth.application.dto.AuthTokenResponse;
import com.apten.auth.application.service.AuthService;
import com.apten.common.constants.SecurityConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 인증 진입 컨트롤러
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    // 토큰 응답 형식 확인용 엔드포인트
    @GetMapping("/tokens/sample")
    public ResponseEntity<AuthTokenResponse> sampleTokenResponse(
            @RequestHeader(name = SecurityConstants.AUTHORIZATION_HEADER, required = false) String authorizationHeader
    ) {
        return ResponseEntity.ok(authService.createSampleTokenResponse(authorizationHeader));
    }
}
