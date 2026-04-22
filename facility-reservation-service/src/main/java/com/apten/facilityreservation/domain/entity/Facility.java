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

// 예약 대상 시설 엔티티
// 예약 방식과 운영 시간, 기본 요금을 이 테이블이 관리한다
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

    // 예약 단위 분
    @Column(name = "slot_min", nullable = false)
    private Integer slotMin;

    // 기본 요금
    @Column(name = "base_fee", nullable = false)
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
