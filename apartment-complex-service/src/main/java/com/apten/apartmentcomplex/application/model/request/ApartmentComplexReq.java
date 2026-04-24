package com.apten.apartmentcomplex.application.model.request;

import com.apten.apartmentcomplex.domain.enums.ApartmentComplexStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 단지 등록/수정 요청 DTO
// 단지 기본 정보를 받을 때 사용한다
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApartmentComplexReq {
    private String name;
    private String address;
    private String detailAddress;
    private String zipcode;
    private ApartmentComplexStatus status;
    private String description;
}
