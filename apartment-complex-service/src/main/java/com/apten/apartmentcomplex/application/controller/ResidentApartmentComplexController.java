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

// 공개 단지 목록 API
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/apartment-complexes")
public class ResidentApartmentComplexController {

    private final ApartmentComplexService apartmentComplexService;

    // 공개 단지 목록 조회 (활성 단지)
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
