package com.apten.facilityreservation.domain.entity;

import com.apten.common.entity.BaseEntity;
import com.apten.facilityreservation.domain.enums.ReservationHoldStatus;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 좌석 임시 선점 엔티티이다.
// 영화관 방식처럼 15분 동안 좌석을 선점하고 예약 확정까지 이어주는 핵심 테이블이다.
@Entity
@Table(
        name = "reservation_temp_hold",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_reservation_hold_slot",
                        columnNames = {"facility_id", "seat_id", "reservation_date", "start_time", "end_time"}
                )
        },
        indexes = {
                @Index(name = "idx_reservation_temp_hold_user_id", columnList = "user_id"),
                @Index(name = "idx_reservation_temp_hold_expires_at", columnList = "expires_at"),
                @Index(name = "idx_reservation_temp_hold_status", columnList = "hold_status")
        }
)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationTempHold extends BaseEntity {

    // 임시 선점 내부 PK이다.
    @Id
    @Tsid
    @Column(name = "id", nullable = false)
    private Long id;

    // 소속 단지 ID이다.
    @Column(name = "complex_id", nullable = false)
    private Long complexId;

    // 사용자 ID이다.
    @Column(name = "user_id", nullable = false)
    private Long userId;

    // 시설 ID이다.
    @Column(name = "facility_id", nullable = false)
    private Long facilityId;

    // 좌석 ID이다.
    @Column(name = "seat_id", nullable = false)
    private Long seatId;

    // 예약일이다.
    @Column(name = "reservation_date", nullable = false)
    private LocalDate reservationDate;

    // 시작 시각이다.
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    // 종료 시각이다.
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    // 임시 선점 상태이다.
    @Builder.Default
    @Column(name = "hold_status", nullable = false, length = 20)
    private ReservationHoldStatus holdStatus = ReservationHoldStatus.HOLDING;

    // 선점 만료 시각이다.
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    // 임시 선점을 만료 상태로 바꾼다.
    public void expire() {
        this.holdStatus = ReservationHoldStatus.EXPIRED;
    }

    // 임시 선점을 예약 확정 상태로 바꾼다.
    public void confirm() {
        this.holdStatus = ReservationHoldStatus.CONFIRMED;
    }

    // 임시 선점을 취소 상태로 바꾼다.
    public void cancel() {
        this.holdStatus = ReservationHoldStatus.CANCELLED;
    }
}
