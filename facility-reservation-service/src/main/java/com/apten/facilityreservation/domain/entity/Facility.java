package com.apten.facilityreservation.domain.entity;

import com.apten.common.entity.BaseEntity;
import com.apten.facilityreservation.application.model.request.FacilityActivePatchReq;
import com.apten.facilityreservation.application.model.request.FacilityPatchReq;
import com.apten.facilityreservation.domain.enums.ReservationType;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
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
@Table(
        name = "facility",
        indexes = {
                @Index(name = "idx_facility_complex_id", columnList = "complex_id"),
                @Index(name = "idx_facility_type_id", columnList = "type_id"),
                @Index(name = "idx_facility_reservation_type", columnList = "reservation_type"),
                @Index(name = "idx_facility_is_active", columnList = "is_active")
        }
)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Facility extends BaseEntity {

    // 시설 내부 PK이다.
    @Id
    @Tsid
    @Column(name = "id", nullable = false)
    private Long id;

    // 소속 단지 ID이다.
    @Column(name = "complex_id", nullable = false)
    private Long complexId;

    // 시설 타입 ID이다.
    @Column(name = "type_id", nullable = false)
    private Long typeId;

    // 시설명이다.
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    // 시설 설명이다.
    @Column(name = "description", length = 500)
    private String description;

    // 예약 방식이다.
    @Builder.Default
    @Column(name = "reservation_type", nullable = false, length = 20)
    private ReservationType reservationType = ReservationType.COUNT;

    // 정원형 시설 최대 인원이다.
    @Column(name = "max_count")
    private Integer maxCount;

    // 운영 시작 시간이다.
    @Column(name = "open_time", nullable = false)
    private LocalTime openTime;

    // 운영 종료 시간이다.
    @Column(name = "close_time", nullable = false)
    private LocalTime closeTime;

    // 개별 시설 override 예약 단위이다.
    @Column(name = "slot_min")
    private Integer slotMin;

    // 개별 시설 override 기본 요금이다.
    @Column(name = "base_fee", precision = 12, scale = 2)
    private BigDecimal baseFee;

    // 활성 여부이다.
    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // 삭제 여부이다.
    @Builder.Default
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    // 삭제 시각이다.
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // 시설 수정 요청을 엔티티에 반영한다.
    public void apply(FacilityPatchReq req) {
        if (req.getTypeId() != null) {
            this.typeId = req.getTypeId();
        }
        if (req.getName() != null) {
            this.name = req.getName();
        }
        if (req.getDescription() != null) {
            this.description = req.getDescription();
        }
        if (req.getReservationType() != null) {
            this.reservationType = req.getReservationType();
        }
        if (req.getMaxCount() != null) {
            this.maxCount = req.getMaxCount();
        }
        if (req.getOpenTime() != null) {
            this.openTime = req.getOpenTime();
        }
        if (req.getCloseTime() != null) {
            this.closeTime = req.getCloseTime();
        }
        if (req.getSlotMin() != null) {
            this.slotMin = req.getSlotMin();
        }
        if (req.getBaseFee() != null) {
            this.baseFee = req.getBaseFee();
        }
    }

    // 시설 활성 여부를 변경한다.
    public void changeActive(FacilityActivePatchReq req) {
        if (req.getIsActive() != null) {
            this.isActive = req.getIsActive();
        }
    }

    // 시설을 소프트 삭제한다.
    public void softDelete() {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
        this.isActive = false;
    }
}
