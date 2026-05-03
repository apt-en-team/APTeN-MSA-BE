package com.apten.auth.application.controller;

import com.apten.auth.application.model.request.UserDeleteReq;
import com.apten.auth.application.model.request.UserPasswordPatchReq;
import com.apten.auth.application.model.response.UserDeleteRes;
import com.apten.auth.application.model.response.UserPasswordPatchRes;
import com.apten.auth.application.service.UserAccountService;
import com.apten.common.response.ResultResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

// 내 계정 관리 API 진입점
// 현재 로그인 사용자의 비밀번호 변경과 탈퇴 요청을 이 컨트롤러가 받는다
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/me")
public class UserMeController {

    // 계정 응용 서비스
    private final UserAccountService userAccountService;

    // 내 비밀번호 변경 API
    // Gateway가 JWT에서 추출한 userId를 X-User-Id 헤더로 전달한다
    @PatchMapping("/password")
    public ResultResponse<UserPasswordPatchRes> changePassword(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody UserPasswordPatchReq request
    ) {
        return ResultResponse.success("비밀번호 변경 성공", userAccountService.changePassword(userId, request));
    }

    // 회원 탈퇴 API
    @DeleteMapping
    public ResultResponse<UserDeleteRes> deleteMyAccount(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody UserDeleteReq request
    ) {
        return ResultResponse.success("회원 탈퇴 성공", userAccountService.deleteMyAccount(userId, request));
    }
}
