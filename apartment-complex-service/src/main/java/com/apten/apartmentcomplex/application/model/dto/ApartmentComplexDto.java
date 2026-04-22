package com.apten.apartmentcomplex.application.model.dto;

import lombok.Builder;
import lombok.Getter;

// apartment-complex-service 내부 계층 간 전달에 사용할 최소 DTO
// 요청 모델과 엔티티 사이를 느슨하게 잇는 중간 전달 객체다
@Getter
@Builder
public class ApartmentComplexDto {
}
