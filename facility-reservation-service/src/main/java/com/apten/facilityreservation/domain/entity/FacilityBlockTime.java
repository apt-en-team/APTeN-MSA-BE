package com.apten.facilityreservation.domain.entity;

import com.apten.common.entity.BaseEntity;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 시설 운영 차단 시간 엔티티이다.
// 점검이나 행사 등 예약이 불가능한 시간대를 저장한다.
@Entity
@Table(
        name = "facility_block_time",
        indexes = {
                @Index(name = "idx_facility_block_time_facility_id", columnList = "facility_id"),
                @Index(name = "idx_facility_block_time_block_date", columnList = "block_date"),
                @Index(name = "idx_facility_block_time_start_end", columnList = "start_time,end_time"),
                @Index(name = "idx_facility_block_time_is_active", columnList = "is_active")
        }
)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacilityBlockTime extends BaseEntity {

    // 차단 시간 내부 PK이다.
    @Id
    @Tsid
    @Column(name = "id", nullable = false)
    private Long id;

    // 시설 ID이다.
    @Column(name = "facility_id", nullable = false)
    private Long facilityId;

    // null이면 시설 전체 차단, 값 있으면 해당 좌석만 차단
    @Column(name = "seat_id")
    private Long seatId;

    // 차단일이다.
    @Column(name = "block_date", nullable = false)
    private LocalDate blockDate;

    // 시작 시각이다.
    @Column(name = "start_time")
    private LocalTime startTime;

    // 종료 시각이다.
    @Column(name = "end_time")
    private LocalTime endTime;

    // 차단 사유이다.
    @Column(name = "reason", length = 255)
    private String reason;

    // 반복 차단 그룹 ID이다. null이면 단건 차단, 값 있으면 배치 생성 그룹
    @Column(name = "batch_id")
    private Long batchId;

    // 활성 여부이다.
    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    public void deactivate() {
        this.isActive = false;
    }
}
