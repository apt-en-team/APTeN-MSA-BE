package com.apten.apartmentcomplex.application.controller;

import com.apten.apartmentcomplex.application.model.request.HouseholdTypePatchReq;
import com.apten.apartmentcomplex.application.model.request.HouseholdTypePostReq;
import com.apten.apartmentcomplex.application.model.response.HouseholdTypeDeleteRes;
import com.apten.apartmentcomplex.application.model.response.HouseholdTypePatchRes;
import com.apten.apartmentcomplex.application.model.response.HouseholdTypePostRes;
import com.apten.apartmentcomplex.application.model.response.HouseholdTypeRes;
import com.apten.apartmentcomplex.application.service.HouseholdTypeService;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

// 관리자 세대 유형 API 진입점
// 단지별 세대 유형 등록과 조회, 수정, 삭제를 이 컨트롤러가 받는다
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminHouseholdTypeController {

    // 세대 유형 응용 서비스
    private final HouseholdTypeService householdTypeService;

    // 세대 유형 등록 API-210
    @PostMapping("/apartment-complexes/{apartmentComplexUid}/household-types")
    @ResponseStatus(HttpStatus.CREATED)
    public ResultResponse<HouseholdTypePostRes> createHouseholdType(
            @PathVariable String apartmentComplexUid,
            @RequestBody HouseholdTypePostReq request
    ) {
        return ResultResponse.success(
                "세대 유형 등록 성공",
                householdTypeService.createHouseholdType(apartmentComplexUid, request)
        );
    }

    // 세대 유형 목록 조회 API-211
    @GetMapping("/apartment-complexes/{apartmentComplexUid}/household-types")
    public ResultResponse<List<HouseholdTypeRes>> getHouseholdTypeList(@PathVariable String apartmentComplexUid) {
        return ResultResponse.success(
                "세대 유형 목록 조회 성공",
                householdTypeService.getHouseholdTypeList(apartmentComplexUid)
        );
    }

    // 세대 유형 수정 API-212
    @PatchMapping("/household-types/{householdTypeUid}")
    public ResultResponse<HouseholdTypePatchRes> updateHouseholdType(
            @PathVariable String householdTypeUid,
            @RequestBody HouseholdTypePatchReq request
    ) {
        return ResultResponse.success(
                "세대 유형 수정 성공",
                householdTypeService.updateHouseholdType(householdTypeUid, request)
        );
    }

    // 세대 유형 삭제 API-213
    @DeleteMapping("/household-types/{householdTypeUid}")
    public ResultResponse<HouseholdTypeDeleteRes> deleteHouseholdType(@PathVariable String householdTypeUid) {
        return ResultResponse.success(
                "세대 유형 삭제 성공",
                householdTypeService.deleteHouseholdType(householdTypeUid)
        );
    }
}
