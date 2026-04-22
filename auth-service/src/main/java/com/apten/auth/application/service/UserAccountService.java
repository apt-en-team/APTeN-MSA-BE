package com.apten.auth.application.service;

import com.apten.auth.application.model.request.UserDeleteReq;
import com.apten.auth.application.model.request.UserPasswordPatchReq;
import com.apten.auth.application.model.response.UserDeleteRes;
import com.apten.auth.application.model.response.UserPasswordPatchRes;
import org.springframework.stereotype.Service;

// 로그인 사용자 계정 관리 서비스
// 비밀번호 변경과 회원 탈퇴 시그니처를 이 서비스에 모아둔다
@Service
public class UserAccountService {

    // 내 비밀번호 변경 서비스
    public UserPasswordPatchRes changePassword(UserPasswordPatchReq request) {
        // TODO: 현재 비밀번호 확인 후 새 비밀번호 변경 로직 구현
        return UserPasswordPatchRes.builder()
                .message("비밀번호 변경 완료")
                .build();
    }

    // 회원 탈퇴 서비스
    public UserDeleteRes deleteMyAccount(UserDeleteReq request) {
        // TODO: 회원 소프트 삭제 로직 구현
        return UserDeleteRes.builder()
                .message("회원 탈퇴 완료")
                .build();
    }
}
