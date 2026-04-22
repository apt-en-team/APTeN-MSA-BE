package com.apten.facilityreservation.domain.entity;

import com.apten.common.entity.BaseEntity;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 시설 운영 차단 시간 엔티티
// 점검이나 행사 시간대 예약 차단 정보를 이 테이블이 관리한다
@Entity
@Table(name = "facility_block_time")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacilityBlockTime extends BaseEntity {

    // 차단 시간 내부 PK
    @Id
    @Tsid
    @Column(name = "id", nullable = false)
    private Long id;

    // 시설 ID
    @Column(name = "facility_id", nullable = false)
    private Long facilityId;

    // 차단일
    @Column(name = "block_date", nullable = false)
    private LocalDate blockDate;

    // 시작 시각
    @Column(name = "start_time")
    private LocalTime startTime;

    // 종료 시각
    @Column(name = "end_time")
    private LocalTime endTime;

    // 사유
    @Column(name = "reason", length = 255)
    private String reason;

    // 활성 여부
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;
}
