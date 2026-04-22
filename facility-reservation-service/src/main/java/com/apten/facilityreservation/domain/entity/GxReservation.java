package com.apten.facilityreservation.domain.entity;

import com.apten.common.entity.BaseEntity;
import com.apten.facilityreservation.domain.enums.GxCancelReason;
import com.apten.facilityreservation.domain.enums.GxReservationStatus;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// GX 예약 엔티티
// 대기와 승인, 거절, 취소 상태를 이 테이블이 관리한다
@Entity
@Table(name = "gx_reservation")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GxReservation extends BaseEntity {

    // GX 예약 내부 PK
    @Id
    @Tsid
    @Column(name = "id", nullable = false)
    private Long id;

    // 소속 단지 ID
    @Column(name = "complex_id", nullable = false)
    private Long complexId;

    // 프로그램 ID
    @Column(name = "program_id", nullable = false)
    private Long programId;

    // 사용자 ID
    @Column(name = "user_id", nullable = false)
    private Long userId;

    // 예약 상태
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private GxReservationStatus status;

    // 대기 순번
    @Column(name = "wait_no")
    private Integer waitNo;

    // 승인 시각
    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    // 거절 사유
    @Column(name = "reject_reason", length = 255)
    private String rejectReason;

    // 취소 사유
    @Enumerated(EnumType.STRING)
    @Column(name = "cancel_reason", length = 20)
    private GxCancelReason cancelReason;

    // 취소 시각
    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;
}
