package com.apten.auth.application.controller;

import com.apten.auth.application.model.request.InternalAdminCreateReq;
import com.apten.auth.application.model.request.InternalAdminPatchReq;
import com.apten.auth.application.model.response.InternalAdminCreateRes;
import com.apten.auth.application.model.response.InternalAdminDeleteRes;
import com.apten.auth.application.model.response.InternalAdminPatchRes;
import com.apten.auth.application.service.AdminService;
import com.apten.common.response.ResultResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    // 단지 서비스 내부 연동용 관리자 서비스
    private final AdminService adminService;

    // 단지 서비스가 최초 MANAGER 또는 ADMIN을 생성할 때 사용하는 내부 API
    @PostMapping("/admins")
    @ResponseStatus(HttpStatus.CREATED)
    public ResultResponse<InternalAdminCreateRes> createAdminInternal(@Valid @RequestBody InternalAdminCreateReq request) {
        return ResultResponse.success("내부 관리자 생성 성공", adminService.createAdminInternal(request));
    }

    // 단지 서비스가 관리자 정보를 수정할 때 사용하는 내부 API
    @PatchMapping("/admins/{userId}")
    public ResultResponse<InternalAdminPatchRes> updateAdminInternal(
            @PathVariable Long userId,
            @RequestBody InternalAdminPatchReq request
    ) {
        return ResultResponse.success("내부 관리자 수정 성공", adminService.updateAdminInternal(userId, request));
    }

    // 단지 서비스가 관리자 계정을 비활성할 때 사용하는 내부 API
    @PatchMapping("/admins/{userId}/delete")
    public ResultResponse<InternalAdminDeleteRes> deleteAdminInternal(@PathVariable Long userId) {
        return ResultResponse.success("내부 관리자 삭제 성공", adminService.deleteAdminInternal(userId));
    }
}