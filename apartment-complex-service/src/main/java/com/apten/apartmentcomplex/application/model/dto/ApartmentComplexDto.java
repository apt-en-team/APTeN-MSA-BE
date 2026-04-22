package com.apten.apartmentcomplex.application.model.dto;

import com.apten.apartmentcomplex.domain.enums.ApartmentComplexStatus;
import lombok.Builder;
import lombok.Getter;

// 단지 내부 전달용 DTO
// 엔티티와 API 응답을 바로 섞지 않기 위한 중간 객체다
@Getter
@Builder
public class ApartmentComplexDto {
    private final Long id;
    private final String code;
    private final String name;
    private final String address;
    private final String addressDetail;
    private final String zipCode;
    private final ApartmentComplexStatus status;
    private final String description;
}
