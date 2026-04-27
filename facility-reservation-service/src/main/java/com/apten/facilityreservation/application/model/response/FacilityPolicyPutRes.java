package com.apten.facilityreservation.application.model.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 시설 정책 설정 응답 DTO이다.
// 저장된 시설 타입 기본 정책 결과를 관리자 화면에 돌려줄 때 사용한다.
@Getter
@Builder
public class FacilityPolicyPutRes {

    // 정책이 속한 단지 ID이다.
    private Long complexId;

    // 정책이 적용되는 시설 타입 코드이다.
    private String facilityTypeCode;

    // 저장된 시설 타입 기본 요금이다.
    private BigDecimal baseFee;

    // 저장된 시설 타입 기본 예약 단위이다.
    private Integer slotMin;

    // 저장된 예약 취소 가능 마감 시간이다.
    private Integer cancelDeadlineHours;

    // 저장된 GX 대기 허용 여부이다.
    private Boolean gxWaitingEnabled;

    // 저장된 정책 활성 여부이다.
    private Boolean isActive;

    // 정책 저장이 완료된 시각이다.
    private LocalDateTime updatedAt;
}
