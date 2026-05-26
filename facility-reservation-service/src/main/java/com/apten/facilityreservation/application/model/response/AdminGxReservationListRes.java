package com.apten.facilityreservation.application.model.response;

import com.apten.facilityreservation.domain.enums.GxReservationCancelReason;
import com.apten.facilityreservation.domain.enums.GxReservationStatus;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 관리자 GX 프로그램 신청자 목록 응답 DTO이다.
@Getter
@Builder
public class AdminGxReservationListRes {

    private Long gxReservationId;

    private Long userId;

    private Long householdId;

    // 캐시 테이블 기반이므로 Kafka 미동기 시 null일 수 있다.
    private String residentName;

    private String dong;

    private String ho;

    private String unit;

    private GxReservationStatus status;

    private String statusName;

    // WAITING 상태에만 유효한 대기 순번이다.
    private Integer waitNo;

    private LocalDateTime approvedAt;

    private String rejectReason;

    private GxReservationCancelReason cancelReason;

    private LocalDateTime cancelledAt;

    private LocalDateTime createdAt;
}
