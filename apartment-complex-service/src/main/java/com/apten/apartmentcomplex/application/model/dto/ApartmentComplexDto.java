package com.apten.apartmentcomplex.application.model.dto;

import com.apten.apartmentcomplex.domain.enums.ApartmentComplexStatus;
import lombok.Builder;
import lombok.Getter;

// apartment-complex-service 내부 계층 간 전달에 사용할 최소 DTO
// 요청 모델과 엔티티를 직접 섞지 않기 위한 중간 전달 객체다
@Getter
@Builder
public class ApartmentComplexDto {

    // 단지 식별자
    private final Long id;

    // 단지 이름
    private final String name;

    // 단지 상태
    private final ApartmentComplexStatus status;
}
