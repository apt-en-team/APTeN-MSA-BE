package com.apten.apartmentcomplex.application.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// apartment-complex-service의 HTTP 요청 진입점을 잡아두는 기본 컨트롤러
// 단지, 동, 호수 관련 API는 이후 이 계층에서 공통 응답 규칙에 맞춰 확장한다
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/apartment-complexes")
public class ApartmentComplexController {
}
