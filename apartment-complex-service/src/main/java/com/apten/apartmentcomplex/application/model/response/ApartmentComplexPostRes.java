package com.apten.apartmentcomplex.application.model.response;

import java.time.LocalDateTime;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

// 단지 등록 응답 DTO
// 등록 완료 후 생성된 식별 정보와 시각을 내려줄 때 사용한다
@Getter
@Builder
public class ApartmentComplexPostRes {
    private final Long complexId;
    private final String code;
    private final String name;
    private final Long managerUserId;
    private final String managerName;
    private final String managerEmail;
    private final String managerPhone;
    private final Map<String, Boolean> features;
    // 주차 운영 타입 code
    private final String parkingTypeCode;
    // 주차 운영 타입 표시 value
    private final String parkingTypeValue;
    private final LocalDateTime createdAt;
}
