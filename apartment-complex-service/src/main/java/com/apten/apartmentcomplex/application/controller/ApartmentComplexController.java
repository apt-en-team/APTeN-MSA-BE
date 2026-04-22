package com.apten.apartmentcomplex.application.controller;

import com.apten.apartmentcomplex.application.model.response.ApartmentComplexBaseResponse;
import com.apten.apartmentcomplex.application.service.ApartmentComplexService;
import com.apten.common.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// apartment-complex-service의 HTTP 요청 진입점을 잡아두는 기본 컨트롤러
// 단지, 동, 호수 관련 API는 이후 이 계층에서 공통 응답 규칙에 맞춰 확장한다
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/apartment-complexes")
public class ApartmentComplexController {

    // 단지 응용 계층 진입점
    private final ApartmentComplexService apartmentComplexService;

    // 공통 응답 포맷과 기본 라우팅 연결이 정상인지 확인하는 최소 엔드포인트
    @GetMapping("/template")
    public ResultResponse<ApartmentComplexBaseResponse> getApartmentComplexTemplate() {
        return ResultResponse.success(
                "apartment complex template ready",
                apartmentComplexService.getApartmentComplexTemplate()
        );
    }
}
