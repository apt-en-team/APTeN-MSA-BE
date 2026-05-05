package com.apten.auth.application.controller;

import com.apten.auth.application.model.request.AdminCreateReq;
import com.apten.auth.application.model.request.AdminPatchReq;
import com.apten.auth.application.model.response.AdminCreateRes;
import com.apten.auth.application.model.response.AdminDeleteRes;
import com.apten.auth.application.model.response.AdminPatchRes;
import com.apten.auth.application.service.AdminService;
import com.apten.common.constants.HeaderConstants;
import com.apten.common.response.ResultResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
            @RequestHeader(HeaderConstants.X_USER_ROLE) String masterUserRole,
            @Valid @RequestBody AdminCreateReq request
    ) {
        return ResultResponse.success("MANAGER 계정 생성 성공", adminService.createManagerByMaster(masterUserId, masterUserRole, request));
    }

    //추가 MASTER가 ADMIN 계정을 생성한다.
    @PostMapping("/master/admins")
    @ResponseStatus(HttpStatus.CREATED)
    public ResultResponse<AdminCreateRes> createAdminByMaster(
            @RequestHeader(HeaderConstants.X_USER_ID) Long masterUserId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String masterUserRole,
            @Valid @RequestBody AdminCreateReq request
    ) {
        return ResultResponse.success("ADMIN 계정 생성 성공", adminService.createAdminByMaster(masterUserId, masterUserRole, request));
    }

    // MANAGER가 ADMIN 계정 생성 API
    // Gateway에서 MANAGER 권한 검증 후 X-User-Id 헤더로 MANAGER userId 전달
    @PostMapping("/manager/admins")
    @ResponseStatus(HttpStatus.CREATED)
    public ResultResponse<AdminCreateRes> createAdminByManager(
            @RequestHeader(HeaderConstants.X_USER_ID) Long managerUserId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String managerUserRole,
            @Valid @RequestBody AdminCreateReq request
    ) {
        return ResultResponse.success("ADMIN 계정 생성 성공", adminService.createAdminByManager(managerUserId, managerUserRole, request));
    }

    //추가 MASTER가 MANAGER 또는 ADMIN 계정을 수정한다.
    @PatchMapping("/master/admins/{userId}")
    public ResultResponse<AdminPatchRes> updateAdminByMaster(
            @RequestHeader(HeaderConstants.X_USER_ID) Long masterUserId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String masterUserRole,
            @PathVariable Long userId,
            @RequestBody AdminPatchReq request
    ) {
        return ResultResponse.success("관리자 계정 수정 성공", adminService.updateAdminByMaster(masterUserId, masterUserRole, userId, request));
    }

    //추가 MANAGER가 ADMIN 계정을 수정한다.
    @PatchMapping("/manager/admins/{userId}")
    public ResultResponse<AdminPatchRes> updateAdminByManager(
            @RequestHeader(HeaderConstants.X_USER_ID) Long managerUserId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String managerUserRole,
            @PathVariable Long userId,
            @RequestBody AdminPatchReq request
    ) {
        return ResultResponse.success("관리자 계정 수정 성공", adminService.updateAdminByManager(managerUserId, managerUserRole, userId, request));
    }

    //추가 MASTER가 MANAGER 또는 ADMIN 계정을 비활성한다.
    @DeleteMapping("/master/admins/{userId}")
    public ResultResponse<AdminDeleteRes> deleteAdminByMaster(
            @RequestHeader(HeaderConstants.X_USER_ID) Long masterUserId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String masterUserRole,
            @PathVariable Long userId
    ) {
        return ResultResponse.success("관리자 계정 비활성 성공", adminService.deleteAdminByMaster(masterUserId, masterUserRole, userId));
    }

    //추가 MANAGER가 ADMIN 계정을 비활성한다.
    @DeleteMapping("/manager/admins/{userId}")
    public ResultResponse<AdminDeleteRes> deleteAdminByManager(
            @RequestHeader(HeaderConstants.X_USER_ID) Long managerUserId,
            @RequestHeader(HeaderConstants.X_USER_ROLE) String managerUserRole,
            @PathVariable Long userId
    ) {
        return ResultResponse.success("관리자 계정 비활성 성공", adminService.deleteAdminByManager(managerUserId, managerUserRole, userId));
    }
}
