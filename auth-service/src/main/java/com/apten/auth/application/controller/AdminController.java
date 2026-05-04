package com.apten.auth.application.controller;

import com.apten.auth.application.model.request.AdminCreateReq;
import com.apten.auth.application.model.response.AdminCreateRes;
import com.apten.auth.application.service.AdminService;
import com.apten.common.constants.HeaderConstants;
import com.apten.common.response.ResultResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

// MANAGER / ADMIN 계정 생성 API 진입점
// MASTER → MANAGER 생성, MANAGER → ADMIN 생성 요청을 받는다
// Gateway에서 JWT 검증 후 X-User-Id, X-User-Role 헤더를 전달한다
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {

    // MANAGER / ADMIN 계정 생성 서비스
    private final AdminService adminService;

    // MASTER가 MANAGER 계정 생성 API
    // Gateway에서 MASTER 권한 검증 후 X-User-Id 헤더로 MASTER userId 전달
    @PostMapping("/master/managers")
    @ResponseStatus(HttpStatus.CREATED)
    public ResultResponse<AdminCreateRes> createManager(
            @RequestHeader(HeaderConstants.X_USER_ID) Long masterUserId,
            @Valid @RequestBody AdminCreateReq request
    ) {
        return ResultResponse.success("MANAGER 계정 생성 성공", adminService.createManager(masterUserId, request));
    }

    // MANAGER가 ADMIN 계정 생성 API
    // Gateway에서 MANAGER 권한 검증 후 X-User-Id 헤더로 MANAGER userId 전달
    @PostMapping("/manager/admins")
    @ResponseStatus(HttpStatus.CREATED)
    public ResultResponse<AdminCreateRes> createAdmin(
            @RequestHeader(HeaderConstants.X_USER_ID) Long managerUserId,
            @Valid @RequestBody AdminCreateReq request
    ) {
        return ResultResponse.success("ADMIN 계정 생성 성공", adminService.createAdmin(managerUserId, request));
    }
}