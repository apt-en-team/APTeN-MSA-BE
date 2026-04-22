package com.apten.facilityreservation.domain.entity;

import com.apten.common.entity.BaseEntity;
import com.apten.facilityreservation.domain.enums.TempHoldStatus;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 좌석 임시선점 엔티티
// 좌석형 시설의 중복 선택 충돌을 방지하는 기준 테이블이다
@Entity
@Table(
        name = "reservation_temp_hold",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_reservation_hold_slot",
                columnNames = {"facility_id", "seat_id", "reservation_date", "start_time", "end_time", "hold_status"}
        )
)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationTempHold extends BaseEntity {

    // 임시선점 내부 PK
    @Id
    @Tsid
    @Column(name = "id", nullable = false)
    private Long id;

    // 소속 단지 ID
    @Column(name = "complex_id", nullable = false)
    private Long complexId;

    // 사용자 ID
    @Column(name = "user_id", nullable = false)
    private Long userId;

    // 시설 ID
    @Column(name = "facility_id", nullable = false)
    private Long facilityId;

    // 좌석 ID
    @Column(name = "seat_id", nullable = false)
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

    // 임시선점 상태
    @Enumerated(EnumType.STRING)
    @Column(name = "hold_status", nullable = false, length = 20)
    private TempHoldStatus holdStatus;

    // 만료 시각
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;
}
