package com.apten.apartmentcomplex.application.model.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 시설 정책 설정 응답 DTO
// 단지 시설 정책 저장 결과를 내려줄 때 사용한다
@Getter
@Builder
public class FacilityPolicyPutRes {
    // 단지 코드
    private String complexCode;
    // 시설 타입 코드
    private String facilityTypeCode;
    // 시설 타입 기본 요금
    private BigDecimal baseFee;
    // 시설 타입 기본 예약 단위
    private Integer slotMin;
    // 예약 시작 몇 시간 전까지 취소 가능
    private Integer cancelDeadlineHours;
    // GX 대기 허용 여부
    private Boolean gxWaitingEnabled;
    // 수정일시
    private LocalDateTime updatedAt;
}
