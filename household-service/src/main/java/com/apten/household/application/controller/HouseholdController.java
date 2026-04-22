package com.apten.household.application.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// household-service의 HTTP 요청 진입점을 잡아두는 기본 컨트롤러
// 세대 관리 API는 이후 이 계층에서 공통 응답 규칙에 맞춰 확장한다
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/households")
public class HouseholdController {
}
