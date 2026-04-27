package com.apten.facilityreservation.domain.entity;

import com.apten.common.entity.BaseEntity;
import com.apten.facilityreservation.domain.enums.GxReservationCancelReason;
import com.apten.facilityreservation.domain.enums.GxReservationStatus;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// GX 예약 엔티티이다.
// 대기와 승인, 거절, 취소 상태를 이 테이블이 관리한다.
@Entity
@Table(
        name = "gx_reservation",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_gx_reservation_program_user", columnNames = {"program_id", "user_id"})
        },
        indexes = {
                @Index(name = "idx_gx_reservation_user_id", columnList = "user_id"),
                @Index(name = "idx_gx_reservation_program_id", columnList = "program_id"),
                @Index(name = "idx_gx_reservation_status", columnList = "status"),
                @Index(name = "idx_gx_reservation_wait_no", columnList = "wait_no")
        }
)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GxReservation extends BaseEntity {

    // GX 예약 내부 PK이다.
    @Id
    @Tsid
    @Column(name = "id", nullable = false)
    private Long id;

    // 소속 단지 ID이다.
    @Column(name = "complex_id", nullable = false)
    private Long complexId;

    // 프로그램 ID이다.
    @Column(name = "program_id", nullable = false)
    private Long programId;

    // 사용자 ID이다.
    @Column(name = "user_id", nullable = false)
    private Long userId;

    // 예약 상태이다.
    @Builder.Default
    @Column(name = "status", nullable = false, length = 20)
    private GxReservationStatus status = GxReservationStatus.WAITING;

    // 대기 순번이다.
    @Column(name = "wait_no")
    private Integer waitNo;

    // 승인 시각이다.
    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    // 거절 사유이다.
    @Column(name = "reject_reason", length = 255)
    private String rejectReason;

    // 취소 사유이다.
    @Column(name = "cancel_reason", length = 20)
    private GxReservationCancelReason cancelReason;

    // 취소 시각이다.
    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    // 예약을 승인 상태로 변경한다.
    public void approve() {
        this.status = GxReservationStatus.CONFIRMED;
        this.approvedAt = LocalDateTime.now();
    }

    // 예약을 거절 상태로 변경한다.
    public void reject(String rejectReason) {
        this.status = GxReservationStatus.REJECTED;
        this.rejectReason = rejectReason;
    }

    // 예약을 취소 상태로 변경한다.
    public void cancel(GxReservationCancelReason cancelReason) {
        this.status = GxReservationStatus.CANCELLED;
        this.cancelReason = cancelReason;
        this.cancelledAt = LocalDateTime.now();
    }
}
