package com.apten.apartmentcomplex.application.model.response;

import lombok.Builder;
import lombok.Getter;

// 마스터 단지 선택 응답 DTO
// 관리자 화면 이동에 필요한 최소 단지 정보를 내려준다
@Getter
@Builder
public class ApartmentComplexSelectRes {

    private final Long complexId;
    private final String code;
    private final String name;
    private final String status;
    private final String statusName;
    private final String adminPageUrl;
}
