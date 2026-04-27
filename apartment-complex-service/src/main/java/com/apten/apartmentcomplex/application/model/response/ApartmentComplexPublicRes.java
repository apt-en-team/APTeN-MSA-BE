package com.apten.apartmentcomplex.application.model.response;

import lombok.Builder;
import lombok.Getter;

// 공개 단지 목록 응답 DTO
// 회원가입 화면에서 선택 가능한 단지 정보를 담는다
@Getter
@Builder
public class ApartmentComplexPublicRes {
    private final String code;
    private final String name;
    private final String address;
}
