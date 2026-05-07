package com.apten.apartmentcomplex.application.controller;

import com.apten.apartmentcomplex.application.model.response.AddressSearchPageRes;
import com.apten.apartmentcomplex.application.service.AddressSearchService;
import com.apten.common.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// 주소 검색 API를 제공하는 컨트롤러이다.
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/master/apartment-complexes/address")
public class AddressSearchController {

    private final AddressSearchService addressSearchService;

    //주소 검색 API-211
    @GetMapping("/search")
    public ResultResponse<AddressSearchPageRes> searchAddress(
            @RequestParam String keyword,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        return ResultResponse.success("주소 검색 성공", addressSearchService.searchAddress(keyword, page, size));
    }
}
