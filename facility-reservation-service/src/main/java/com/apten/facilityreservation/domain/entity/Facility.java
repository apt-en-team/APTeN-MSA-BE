package com.apten.facilityreservation.domain.entity;

import com.apten.common.entity.BaseEntity;
import com.apten.facilityreservation.domain.enums.ReservationType;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 예약 대상 시설 엔티티이다.
// 시설 타입 기본 정책 위에 개별 시설 override 값을 얹는 구조를 사용한다.
@Entity
@Table(name = "facility")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Facility extends BaseEntity {

    // 시설 내부 PK
    @Id
    @Tsid
    @Column(name = "id", nullable = false)
    private Long id;

    // 소속 단지 ID
    @Column(name = "complex_id", nullable = false)
    private Long complexId;

    // 시설 타입 ID
    @Column(name = "type_id", nullable = false)
    private Long typeId;

    // 시설명
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    // 설명
    @Column(name = "description", length = 500)
    private String description;

    // 예약 방식
    @Enumerated(EnumType.STRING)
    @Column(name = "reservation_type", nullable = false, length = 20)
    private ReservationType reservationType;

    // 최대 인원
    @Column(name = "max_count")
    private Integer maxCount;

    // 운영 시작 시간
    @Column(name = "open_time", nullable = false)
    private LocalTime openTime;

    // 운영 종료 시간
    @Column(name = "close_time", nullable = false)
    private LocalTime closeTime;

    // 개별 시설 override 예약 단위이다.
    // 값이 없으면 facility_policy.slot_min을 사용한다.
    @Column(name = "slot_min")
    private Integer slotMin;

    // 개별 시설 override 기본 요금이다.
    // 값이 없으면 facility_policy.base_fee를 사용한다.
    @Column(name = "base_fee", precision = 12, scale = 2)
    private BigDecimal baseFee;

    // 활성 여부
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    // 삭제 여부
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    // 삭제 시각
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
