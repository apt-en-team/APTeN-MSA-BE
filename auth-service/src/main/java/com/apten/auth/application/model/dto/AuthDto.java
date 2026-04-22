package com.apten.auth.application.model.dto;

import lombok.Builder;
import lombok.Getter;

// auth-service 내부 계층 간 전달에 사용할 최소 DTO
// 컨트롤러 응답 모델과 엔티티를 바로 섞지 않기 위한 중간 전달 객체다
@Getter
@Builder
public class AuthDto {
}
