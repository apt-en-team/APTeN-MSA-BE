package com.apten.apartmentcomplex.application.controller;

import com.apten.apartmentcomplex.application.model.request.ApartmentComplexReq;
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
import lombok.extern.slf4j.Slf4j;
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

// 관리자 단지 API 진입점 권한 MASTER만 접근 가능함
// 단지 등록, 조회, 수정, 관리자 소속 지정을 이 컨트롤러가 받는다
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/admin/apartment-complexes")
public class AdminApartmentComplexController {

    // 단지 응용 서비스
    private final ApartmentComplexService apartmentComplexService;

    // 단지 등록 API-201
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResultResponse<ApartmentComplexPostRes> createApartmentComplex(@RequestBody ApartmentComplexReq req) {
        log.info("------------OK");
        return ResultResponse.success("단지 등록 성공", apartmentComplexService.createApartmentComplex(req));
    }

    // 관리자 단지 목록 조회 API-202
    @GetMapping
    public ResultResponse<ApartmentComplexGetPageRes> getApartmentComplexList(
            @ModelAttribute ApartmentComplexSearchReq req
    ) {
        return ResultResponse.success("단지 목록 조회 성공", apartmentComplexService.getApartmentComplexList(req));
    }

    // 관리자 단지 상세 조회 API-203
    @GetMapping("/{ComplexUid}")
    public ResultResponse<ApartmentComplexGetDetailRes> getApartmentComplexDetail(
            @PathVariable String ComplexUid
    ) {
        return ResultResponse.success(
                "단지 상세 조회 성공",
                apartmentComplexService.getApartmentComplexDetail(ComplexUid)
        );
    }

    // 단지 수정 API-204
    @PatchMapping("/{ComplexUid}")
    public ResultResponse<ApartmentComplexPatchRes> updateApartmentComplex(
            @PathVariable String ComplexUid,
            @RequestBody ApartmentComplexReq req
    ) {
        return ResultResponse.success(
                "단지 수정 성공",
                apartmentComplexService.updateApartmentComplex(ComplexUid, req)
        );
    }

    // 관리자 단지 소속 지정 API-205
    @PostMapping("/{ComplexUid}/admins")
    @ResponseStatus(HttpStatus.CREATED)
    public ResultResponse<ComplexAdminPostRes> assignAdminToComplex(
            @PathVariable String ComplexUid,
            @RequestBody ComplexAdminPostReq req
    ) {
        return ResultResponse.success(
                "단지 관리자 지정 성공",
                apartmentComplexService.assignAdminToComplex(ComplexUid, req)
        );
    }
}
