package com.apten.apartmentcomplex.application.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 세대 유형 수정 응답 DTO
// 수정된 세대 유형 식별 정보와 시각을 내려줄 때 사용한다
@Getter
@Builder
public class HouseholdTypePatchRes {
    private final String householdTypeUid;
    private final String householdTypeName;
    private final LocalDateTime updatedAt;
}
