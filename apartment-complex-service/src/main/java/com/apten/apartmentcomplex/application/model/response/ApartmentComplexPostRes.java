package com.apten.apartmentcomplex.application.model.response;

import java.time.LocalDateTime;
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
    private final LocalDateTime createdAt;
}
