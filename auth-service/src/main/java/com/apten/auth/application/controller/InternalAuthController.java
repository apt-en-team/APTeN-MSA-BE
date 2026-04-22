package com.apten.auth.application.controller;

import com.apten.auth.application.model.request.AuthBlacklistPostReq;
import com.apten.auth.application.model.response.AuthBlacklistPostRes;
import com.apten.auth.application.service.AuthSupportService;
import com.apten.common.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

// 내부 인증 지원 API 진입점
// 다른 서비스나 게이트웨이가 호출할 내부 인증 보조 기능을 이 컨트롤러가 받는다
@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/auth")
public class InternalAuthController {

    // 인증 보조 서비스
    private final AuthSupportService authSupportService;

    // Access Token 블랙리스트 등록 API
    @PostMapping("/blacklist")
    @ResponseStatus(HttpStatus.CREATED)
    public ResultResponse<AuthBlacklistPostRes> blacklistAccessToken(@RequestBody AuthBlacklistPostReq request) {
        return ResultResponse.success("블랙리스트 등록 성공", authSupportService.blacklistAccessToken(request));
    }
}
