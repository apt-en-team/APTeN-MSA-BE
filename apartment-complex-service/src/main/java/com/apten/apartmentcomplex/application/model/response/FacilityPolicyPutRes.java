package com.apten.apartmentcomplex.application.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 시설 정책 설정 응답 DTO
// 단지 시설 정책 저장 결과를 내려줄 때 사용한다
@Getter
@Builder
public class FacilityPolicyPutRes {
    private final String apartmentComplexUid;
    private final Integer reservationSlotMin;
    private final Integer facilityCancelDeadlineHours;
    private final Boolean gxWaitingEnabled;
    private final LocalDateTime updatedAt;
}
