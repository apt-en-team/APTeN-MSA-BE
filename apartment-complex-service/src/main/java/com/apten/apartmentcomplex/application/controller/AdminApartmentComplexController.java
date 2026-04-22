package com.apten.apartmentcomplex.application.controller;

import com.apten.apartmentcomplex.application.model.request.ApartmentComplexPatchReq;
import com.apten.apartmentcomplex.application.model.request.ApartmentComplexPostReq;
import com.apten.apartmentcomplex.application.model.request.ApartmentComplexSearchReq;
import com.apten.apartmentcomplex.application.model.request.ComplexAdminPostReq;
import com.apten.apartmentcomplex.application.model.response.ApartmentComplexGetDetailRes;
import com.apten.apartmentcomplex.application.model.response.ApartmentComplexGetPageRes;
import com.apten.apartmentcomplex.application.model.response.ApartmentComplexPatchRes;
import com.apten.apartmentcomplex.application.model.response.ApartmentComplexPostRes;
import com.apten.apartmentcomplex.application.model.response.ComplexAdminPostRes;
import com.apten.apartmentcomplex.application.service.ApartmentComplexService;
import com.apten.common.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

// 관리자 단지 API 진입점
// 단지 등록, 조회, 수정, 관리자 소속 지정을 이 컨트롤러가 받는다
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/apartment-complexes")
public class AdminApartmentComplexController {

    // 단지 응용 서비스
    private final ApartmentComplexService apartmentComplexService;

    // 단지 등록 API
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResultResponse<ApartmentComplexPostRes> createApartmentComplex(@RequestBody ApartmentComplexPostReq request) {
        return ResultResponse.success("단지 등록 성공", apartmentComplexService.createApartmentComplex(request));
    }

    // 관리자 단지 목록 조회 API
    @GetMapping
    public ResultResponse<ApartmentComplexGetPageRes> getApartmentComplexList(
            @ModelAttribute ApartmentComplexSearchReq request
    ) {
        return ResultResponse.success("단지 목록 조회 성공", apartmentComplexService.getApartmentComplexList(request));
    }

    // 관리자 단지 상세 조회 API
    @GetMapping("/{apartmentComplexUid}")
    public ResultResponse<ApartmentComplexGetDetailRes> getApartmentComplexDetail(
            @PathVariable String apartmentComplexUid
    ) {
        return ResultResponse.success(
                "단지 상세 조회 성공",
                apartmentComplexService.getApartmentComplexDetail(apartmentComplexUid)
        );
    }

    // 단지 수정 API
    @PatchMapping("/{apartmentComplexUid}")
    public ResultResponse<ApartmentComplexPatchRes> updateApartmentComplex(
            @PathVariable String apartmentComplexUid,
            @RequestBody ApartmentComplexPatchReq request
    ) {
        return ResultResponse.success(
                "단지 수정 성공",
                apartmentComplexService.updateApartmentComplex(apartmentComplexUid, request)
        );
    }

    // 관리자 단지 소속 지정 API
    @PostMapping("/{apartmentComplexUid}/admins")
    @ResponseStatus(HttpStatus.CREATED)
    public ResultResponse<ComplexAdminPostRes> assignAdminToComplex(
            @PathVariable String apartmentComplexUid,
            @RequestBody ComplexAdminPostReq request
    ) {
        return ResultResponse.success(
                "단지 관리자 지정 성공",
                apartmentComplexService.assignAdminToComplex(apartmentComplexUid, request)
        );
    }
}
