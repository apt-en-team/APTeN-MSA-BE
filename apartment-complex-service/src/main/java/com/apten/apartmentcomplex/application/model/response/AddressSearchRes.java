package com.apten.apartmentcomplex.application.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 주소 검색 결과 응답 DTO이다.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressSearchRes {

    private String apartmentName;
    private String address;
    private String zipCode;
}
