package com.apten.apartmentcomplex.application.model.response;

import com.apten.apartmentcomplex.domain.enums.ApartmentComplexStatus;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 관리자 단지 상세 응답 DTO
// 단지 상세 화면에서 필요한 전체 기본 정보를 담는다
@Getter
@Builder
public class ApartmentComplexGetDetailRes {
    private final Long apartmentComplexId;
    private final String apartmentComplexUid;
    private final String name;
    private final String roadAddress;
    private final String jibunAddress;
    private final String detailAddress;
    private final String zipcode;
    private final Double latitude;
    private final Double longitude;
    private final Integer totalDongCount;
    private final Integer totalHouseholdCount;
    private final ApartmentComplexStatus status;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
}
