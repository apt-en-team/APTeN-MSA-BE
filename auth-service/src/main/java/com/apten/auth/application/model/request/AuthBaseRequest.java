package com.apten.auth.application.model.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

// auth-service 요청 객체의 공통 시작점으로 사용하는 최소 요청 DTO
// 이후 OAuth2 콜백 처리나 토큰 재발급 요청 모델을 만들 때 같은 위치 기준이 된다
@Getter
@NoArgsConstructor
public class AuthBaseRequest {
}
