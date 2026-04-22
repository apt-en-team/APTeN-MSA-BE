package com.apten.facilityreservation.domain.entity;

import com.apten.common.entity.BaseEntity;
import com.apten.facilityreservation.domain.enums.ReservationCancelReason;
import com.apten.facilityreservation.domain.enums.ReservationStatus;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 일반 시설 예약 엔티티
// 좌석형과 정원형 예약 상태를 이 테이블이 관리한다
@Entity
@Table(name = "reservation")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Reservation extends BaseEntity {

    // 예약 내부 PK
    @Id
    @Tsid
    @Column(name = "id", nullable = false)
    private Long id;

    // 소속 단지 ID
    @Column(name = "complex_id", nullable = false)
    private Long complexId;

    // 예약 사용자 ID
    @Column(name = "user_id", nullable = false)
    private Long userId;

    // 시설 ID
    @Column(name = "facility_id", nullable = false)
    private Long facilityId;

    // 좌석 ID
    @Column(name = "seat_id")
    private Long seatId;

    // 예약일
    @Column(name = "reservation_date", nullable = false)
    private LocalDate reservationDate;

    // 시작 시각
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    // 종료 시각
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    // 예약 상태
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ReservationStatus status;

    // 취소 사유
    @Enumerated(EnumType.STRING)
    @Column(name = "cancel_reason", length = 20)
    private ReservationCancelReason cancelReason;

    // 취소 시각
    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    // 완료 시각
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
}
