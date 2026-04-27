package com.apten.facilityreservation.domain.entity;

import com.apten.common.entity.BaseEntity;
import com.apten.facilityreservation.domain.enums.ReservationCancelReason;
import com.apten.facilityreservation.domain.enums.ReservationStatus;
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

// 일반 시설 예약 엔티티이다.
// 좌석형과 정원형 예약 상태를 이 테이블이 관리한다.
@Entity
@Table(
        name = "reservation",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_reservation_slot_status",
                        columnNames = {"facility_id", "seat_id", "reservation_date", "start_time", "end_time", "status"}
                )
        },
        indexes = {
                @Index(name = "idx_reservation_user_id", columnList = "user_id"),
                @Index(name = "idx_reservation_facility_id", columnList = "facility_id"),
                @Index(name = "idx_reservation_seat_id", columnList = "seat_id"),
                @Index(name = "idx_reservation_complex_id", columnList = "complex_id"),
                @Index(name = "idx_reservation_date", columnList = "reservation_date"),
                @Index(name = "idx_reservation_status", columnList = "status")
        }
)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Reservation extends BaseEntity {

    // 예약 내부 PK이다.
    @Id
    @Tsid
    @Column(name = "id", nullable = false)
    private Long id;

    // 소속 단지 ID이다.
    @Column(name = "complex_id", nullable = false)
    private Long complexId;

    // 예약 사용자 ID이다.
    @Column(name = "user_id", nullable = false)
    private Long userId;

    // 시설 ID이다.
    @Column(name = "facility_id", nullable = false)
    private Long facilityId;

    // 좌석 ID이다.
    @Column(name = "seat_id")
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

    // 예약 상태이다.
    @Builder.Default
    @Column(name = "status", nullable = false, length = 20)
    private ReservationStatus status = ReservationStatus.CONFIRMED;

    // 취소 사유이다.
    @Column(name = "cancel_reason", length = 20)
    private ReservationCancelReason cancelReason;

    // 취소 시각이다.
    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    // 완료 시각이다.
    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    // 예약을 취소 상태로 변경한다.
    public void cancel(ReservationCancelReason cancelReason) {
        this.status = ReservationStatus.CANCELLED;
        this.cancelReason = cancelReason;
        this.cancelledAt = LocalDateTime.now();
    }

    // 예약을 이용 완료 상태로 변경한다.
    public void complete() {
        this.status = ReservationStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }
}
