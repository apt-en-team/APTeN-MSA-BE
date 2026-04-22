package com.apten.apartmentcomplex.application.model.response;

import com.apten.apartmentcomplex.domain.enums.ApartmentComplexStatus;
import lombok.Builder;
import lombok.Getter;

// apartment-complex-service 응답 모델의 공통 시작점으로 두는 최소 응답 DTO
// 공통 ResultResponse 안에 담길 단지 전용 응답 객체가 이 패키지에 모이게 된다
@Getter
@Builder
public class ApartmentComplexBaseResponse {

    // 단지 식별자
    private final Long id;

    // 단지 이름
    private final String name;

    // 단지 상태
    private final ApartmentComplexStatus status;
}
