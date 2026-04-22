package com.apten.apartmentcomplex.application.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 단지 수정 요청 DTO
// 관리자 수정 API가 변경할 단지 정보를 받을 때 사용한다
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApartmentComplexPatchReq {
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
