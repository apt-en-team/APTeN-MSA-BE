package com.apten.apartmentcomplex.application.controller;

import com.apten.apartmentcomplex.application.model.response.ApartmentComplexPublicRes;
import com.apten.apartmentcomplex.application.service.ApartmentComplexService;
import com.apten.common.response.ResultResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// 입주민과 비회원용 단지 목록 API 진입점
// 회원가입이나 서비스 진입 전에 선택 가능한 단지 목록을 이 컨트롤러가 내려준다
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/apartment-complexes")
public class ResidentApartmentComplexController {

    // 단지 응용 서비스
    private final ApartmentComplexService apartmentComplexService;

    //공개 단지 목록 조회 API-209
    @GetMapping
    public ResultResponse<List<ApartmentComplexPublicRes>> getAvailableApartmentComplexes(
            @RequestParam(required = false) String keyword
    ) {
        return ResultResponse.success(
                "단지 목록 조회 성공",
                apartmentComplexService.getAvailableApartmentComplexes(keyword)
        );
    }
}
