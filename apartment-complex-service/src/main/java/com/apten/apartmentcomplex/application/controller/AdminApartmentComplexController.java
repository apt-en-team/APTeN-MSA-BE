package com.apten.apartmentcomplex.application.controller;

import com.apten.apartmentcomplex.application.model.request.ApartmentComplexReq;
import com.apten.apartmentcomplex.application.model.request.ApartmentComplexSearchReq;
import com.apten.apartmentcomplex.application.model.request.ApartmentComplexStatusPatchReq;
import com.apten.apartmentcomplex.application.model.request.ComplexAdminPatchReq;
import com.apten.apartmentcomplex.application.model.request.ComplexAdminPostReq;
import com.apten.apartmentcomplex.application.model.response.ApartmentComplexGetDetailRes;
import com.apten.apartmentcomplex.application.model.response.ApartmentComplexGetPageRes;
import com.apten.apartmentcomplex.application.model.response.ApartmentComplexPatchRes;
import com.apten.apartmentcomplex.application.model.response.ApartmentComplexPostRes;
import com.apten.apartmentcomplex.application.model.response.ApartmentComplexStatusPatchRes;
import com.apten.apartmentcomplex.application.model.response.ComplexAdminDeleteRes;
import com.apten.apartmentcomplex.application.model.response.ComplexAdminGetRes;
import com.apten.apartmentcomplex.application.model.response.ComplexAdminPatchRes;
import com.apten.apartmentcomplex.application.model.response.ComplexAdminPostRes;
import com.apten.apartmentcomplex.application.service.ApartmentComplexService;
import com.apten.common.response.ResultResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

// 관리자 단지 API 진입점이다.
// 단지 등록과 조회, 수정, 상태 변경, 관리자 배정/해제/조회 요청을 이 컨트롤러가 받는다.
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/admin/master/apartment-complexes")
public class AdminApartmentComplexController {

    // 단지 응용 서비스
    private final ApartmentComplexService apartmentComplexService;

    //단지 등록 API-201
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResultResponse<ApartmentComplexPostRes> createApartmentComplex(@RequestBody ApartmentComplexReq req) {
        return ResultResponse.success("단지 등록 성공", apartmentComplexService.createApartmentComplex(req));
    }

    //단지 목록 조회 API-202
    @GetMapping
    public ResultResponse<ApartmentComplexGetPageRes> getApartmentComplexList(
            @ModelAttribute ApartmentComplexSearchReq req
    ) {
        log.info("keyword = [{}], page = {}, size = {}", req.getKeyword(), req.getPage(), req.getSize());
        return ResultResponse.success("단지 목록 조회 성공", apartmentComplexService.getApartmentComplexList(req));
    }

    //단지 상세 조회 API-203
    @GetMapping("/{code}")
    public ResultResponse<ApartmentComplexGetDetailRes> getApartmentComplexDetail(
            @PathVariable String code
    ) {
        return ResultResponse.success(
                "단지 상세 조회 성공",
                apartmentComplexService.getApartmentComplexDetail(code)
        );
    }

    //단지 수정 API-204
    @PatchMapping("/{code}")
    public ResultResponse<ApartmentComplexPatchRes> updateApartmentComplex(
            @PathVariable String code,
            @RequestBody ApartmentComplexReq req
    ) {
        return ResultResponse.success(
                "단지 수정 성공",
                apartmentComplexService.updateApartmentComplex(code, req)
        );
    }

    //단지 상태 변경 API-205
    @PatchMapping("/{code}/status")
    public ResultResponse<ApartmentComplexStatusPatchRes> changeApartmentComplexStatus(
            @PathVariable String code,
            @RequestBody ApartmentComplexStatusPatchReq req
    ) {
        return ResultResponse.success(
                "단지 상태 변경 성공",
                apartmentComplexService.changeApartmentComplexStatus(code, req)
        );
    }

    //관리자 단지 소속 지정 API-206
    @PostMapping("/{code}/admins")
    @ResponseStatus(HttpStatus.CREATED)
    public ResultResponse<ComplexAdminPostRes> assignAdminToComplex(
            @PathVariable String code,
            @RequestBody ComplexAdminPostReq req
    ) {
        return ResultResponse.success(
                "단지 관리자 지정 성공",
                apartmentComplexService.assignAdminToComplex(code, req)
        );
    }

    //관리자 단지 소속 조회 API-207
    @GetMapping("/{code}/admins")
    public ResultResponse<List<ComplexAdminGetRes>> getComplexAdminList(@PathVariable String code) {
        return ResultResponse.success(
                "단지 관리자 목록 조회 성공",
                apartmentComplexService.getComplexAdminList(code)
        );
    }

    //관리자 단지 소속 해제 API-208
    @DeleteMapping("/{code}/admins/{userId}")
    public ResultResponse<ComplexAdminDeleteRes> unassignAdminFromComplex(
            @PathVariable String code,
            @PathVariable Long userId
    ) {
        return ResultResponse.success(
                "단지 관리자 해제 성공",
                apartmentComplexService.unassignAdminFromComplex(code, userId)
        );
    }

    //관리자 권한 수정 API-212
    @PatchMapping("/{code}/admins/{userId}")
    public ResultResponse<ComplexAdminPatchRes> updateComplexAdmin(
            @PathVariable String code,
            @PathVariable Long userId,
            @RequestBody ComplexAdminPatchReq req
    ) {
        return ResultResponse.success(
                "단지 관리자 수정 성공",
                apartmentComplexService.updateComplexAdmin(code, userId, req)
        );
    }
}
