package com.apten.apartmentcomplex.application.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 단지 등록 요청 DTO
// 관리자 등록 API가 단지 기본 정보를 받을 때 사용한다
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApartmentComplexPostReq {
    private String name;
    private String roadAddress;
    private String jibunAddress;
    private String detailAddress;
    private String zipcode;
    private Double latitude;
    private Double longitude;
    private Integer totalDongCount;
    private Integer totalHouseholdCount;
}
