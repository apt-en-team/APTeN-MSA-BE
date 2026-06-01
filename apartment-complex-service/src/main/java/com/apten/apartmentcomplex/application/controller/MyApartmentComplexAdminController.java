package com.apten.apartmentcomplex.application.controller;

import com.apten.apartmentcomplex.application.model.request.ComplexAdminPatchReq;
import com.apten.apartmentcomplex.application.model.request.ComplexAdminPostReq;
import com.apten.apartmentcomplex.application.model.response.ApartmentComplexGetDetailRes;
import com.apten.apartmentcomplex.application.model.response.ComplexAdminDeleteRes;
import com.apten.apartmentcomplex.application.model.response.ComplexAdminGetRes;
import com.apten.apartmentcomplex.application.model.response.ComplexAdminPatchRes;
import com.apten.apartmentcomplex.application.model.response.ComplexAdminPostRes;
import com.apten.apartmentcomplex.application.service.ApartmentComplexService;
import com.apten.common.constants.HeaderConstants;
import com.apten.common.response.ResultResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

// 관리자 내 단지 관리 API
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/apartment-complex")
public class MyApartmentComplexAdminController {

    private final ApartmentComplexService apartmentComplexService;

    // 내 단지 조회 (관리자 컨텍스트 기준)
    @GetMapping("/me")
    public ResultResponse<ApartmentComplexGetDetailRes> getMyApartmentComplex(
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId
    ) {
        return ResultResponse.success(
                "내 단지 정보 조회 성공",
                apartmentComplexService.getMyApartmentComplex(complexId, selectedComplexId, userRole)
        );
    }

    // 내 단지 관리자 목록 조회
    @GetMapping("/admins")
    public ResultResponse<List<ComplexAdminGetRes>> getMyComplexAdminList(
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId
    ) {
        return ResultResponse.success(
                "내 단지 관리자 목록 조회 성공",
                apartmentComplexService.getMyComplexAdminList(complexId, selectedComplexId, userRole)
        );
    }

    // 내 단지 관리자 등록 (MASTER/MANAGER)
    @PostMapping("/admins")
    @ResponseStatus(HttpStatus.CREATED)
    public ResultResponse<ComplexAdminPostRes> assignAdminToMyComplex(
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId,
            @RequestBody ComplexAdminPostReq req
    ) {
        return ResultResponse.success(
                "내 단지 관리자 지정 성공",
                apartmentComplexService.assignAdminToMyComplex(complexId, selectedComplexId, userRole, req)
        );
    }

    // 내 단지 관리자 수정 (MASTER/MANAGER)
    @PatchMapping("/admins/{userId}")
    public ResultResponse<ComplexAdminPatchRes> updateMyComplexAdmin(
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId,
            @PathVariable Long userId,
            @RequestBody ComplexAdminPatchReq req
    ) {
        return ResultResponse.success(
                "내 단지 관리자 수정 성공",
                apartmentComplexService.updateMyComplexAdmin(complexId, selectedComplexId, userRole, userId, req)
        );
    }

    // 내 단지 관리자 해제 (MASTER/MANAGER)
    @DeleteMapping("/admins/{userId}")
    public ResultResponse<ComplexAdminDeleteRes> unassignAdminFromMyComplex(
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId,
            @RequestHeader(value = HeaderConstants.X_SELECTED_COMPLEX_ID, required = false) Long selectedComplexId,
            @PathVariable Long userId
    ) {
        return ResultResponse.success(
                "내 단지 관리자 해제 성공",
                apartmentComplexService.unassignAdminFromMyComplex(complexId, selectedComplexId, userRole, userId)
        );
    }
}
