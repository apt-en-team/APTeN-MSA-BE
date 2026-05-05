package com.apten.apartmentcomplex.application.model.request;

import com.fasterxml.jackson.annotation.JsonAlias;
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
    @JsonAlias("zipcode")
    private String zipCode;
    private String description;
    private String managerEmail;
    private String managerPassword;
    private String managerName;
    private String managerPhone;
}
