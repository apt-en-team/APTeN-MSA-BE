package com.apten.auth.application.service;

import com.apten.auth.application.model.request.AuthBlacklistPostReq;
import com.apten.auth.application.model.response.AuthBlacklistPostRes;
import org.springframework.stereotype.Service;

// 인증 보조 서비스
// 내부 호출로 들어오는 블랙리스트 등록 기능을 이 서비스가 받는다
@Service
public class AuthSupportService {

    // Access Token 블랙리스트 등록 서비스
    public AuthBlacklistPostRes blacklistAccessToken(AuthBlacklistPostReq request) {
        // TODO: Access Token 블랙리스트 저장 처리
        return AuthBlacklistPostRes.builder()
                .message("블랙리스트 등록 완료")
                .build();
    }
}
