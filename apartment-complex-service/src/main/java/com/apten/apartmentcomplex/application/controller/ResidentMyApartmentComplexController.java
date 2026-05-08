package com.apten.apartmentcomplex.application.controller;

import com.apten.apartmentcomplex.application.model.response.ApartmentComplexGetDetailRes;
import com.apten.apartmentcomplex.application.service.ApartmentComplexService;
import com.apten.common.constants.HeaderConstants;
import com.apten.common.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

// 로그인한 입주민의 내 단지 정보를 조회하는 API 진입점이다.
@RestController
@RequiredArgsConstructor
public class ResidentMyApartmentComplexController {

    private final ApartmentComplexService apartmentComplexService;

    // 입주민 내 단지 조회 API이다.
    @GetMapping("/api/resident/apartment-complex/me")
    public ResultResponse<ApartmentComplexGetDetailRes> getMyApartmentComplexForResident(
            @RequestHeader(HeaderConstants.X_USER_ROLE) String userRole,
            @RequestHeader(value = HeaderConstants.X_COMPLEX_ID, required = false) Long complexId
    ) {
        return ResultResponse.success(
                "입주민 내 단지 조회 성공",
                apartmentComplexService.getMyApartmentComplexForResident(userRole, complexId)
        );
    }
}
