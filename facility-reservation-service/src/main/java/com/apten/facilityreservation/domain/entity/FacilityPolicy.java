package com.apten.facilityreservation.domain.entity;

import com.apten.common.entity.BaseEntity;
import com.apten.facilityreservation.application.model.request.FacilityPolicyPutReq;
import com.apten.facilityreservation.domain.enums.FacilityFeeType;
import com.apten.facilityreservation.domain.enums.FacilityUsageUnitType;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 시설별 기본 정책을 관리하는 원본 엔티티이다.
// 시설 공간 정보와 분리된 예약 정책성 값을 이 테이블이 담당한다.
@Entity
@Table(
        name = "facility_policy",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_facility_policy_complex_facility", columnNames = {"complex_id", "facility_id"})
        },
        indexes = {
                @Index(name = "idx_facility_policy_complex_id", columnList = "complex_id"),
                @Index(name = "idx_facility_policy_facility_id", columnList = "facility_id"),
                @Index(name = "idx_facility_policy_is_active", columnList = "is_active")
        }
)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacilityPolicy extends BaseEntity {

    // 시설 정책 내부 PK이다.
    @Id
    @Tsid
    @Column(name = "id", nullable = false)
    private Long id;

    // 소속 단지 ID이다.
    @Column(name = "complex_id", nullable = false)
    private Long complexId;

    // 정책이 적용되는 시설 ID이다.
    @Column(name = "facility_id", nullable = false)
    private Long facilityId;

    // 시설 타입 기본 요금이다.
    @Builder.Default
    @Column(name = "base_fee", nullable = false, precision = 12, scale = 2)
    private BigDecimal baseFee = BigDecimal.ZERO;

    // 예약 단위 타입이다.
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "usage_unit_type", nullable = false, length = 20)
    private FacilityUsageUnitType usageUnitType = FacilityUsageUnitType.MINUTE;

    // 시설 타입 기본 예약 단위이다.
    @Builder.Default
    @Column(name = "slot_min", nullable = false)
    private Integer slotMin = 30;

    // 예약 시작 몇 시간 전까지 취소 가능한지 나타내는 값이다.
    @Builder.Default
    @Column(name = "cancel_deadline_hours", nullable = false)
    private Integer cancelDeadlineHours = 2;

    // 최대 예약 인원이다.
    @Builder.Default
    @Column(name = "max_reservation_count")
    private Integer maxReservationCount = null;

    // 요금 청구 방식이다.
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "fee_type", nullable = false, length = 20, columnDefinition = "VARCHAR(20) NOT NULL DEFAULT 'FLAT'")
    private FacilityFeeType feeType = FacilityFeeType.FLAT;

    // BASE_PLUS_EXTRA 전용 — 기본 포함 인원 수이다.
    @Column(name = "included_person_count")
    private Integer includedPersonCount;

    // BASE_PLUS_EXTRA 전용 — 초과 인원당 추가 요금이다.
    @Column(name = "extra_person_fee", precision = 12, scale = 2)
    private BigDecimal extraPersonFee;

    // 구독 신청 기준일이다. 이 일자 이하 신청은 당월부터 청구, 초과 신청은 익월부터 청구한다. null이면 항상 당월 청구한다.
    @Column(name = "subscribe_cutoff_day")
    private Integer subscribeCutoffDay;

    // 구독 해지 기준일이다. 이 일자 이하 해지는 당월 미청구, 초과 해지는 익월부터 미청구한다. null이면 항상 당월 청구한다.
    @Column(name = "cancel_cutoff_day")
    private Integer cancelCutoffDay;

    // 정책 활성 여부이다. 항상 true로 고정된다.
    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // 요청값을 시설 정책 엔티티에 반영한다.
    public void apply(FacilityPolicyPutReq req) {
        if (req.getFacilityId() != null) {
            this.facilityId = req.getFacilityId();
        }
        if (req.getBaseFee() != null) {
            this.baseFee = req.getBaseFee();
        }
        if (req.getUsageUnitType() != null) {
            this.usageUnitType = req.getUsageUnitType();
        }
        if (req.getSlotMin() != null) {
            this.slotMin = req.getSlotMin();
        } else if (this.usageUnitType == FacilityUsageUnitType.DAY) {
            // DAY 정책은 내부 저장값을 1440분으로 정규화해 기존 slotMin 의존 로직과 호환한다.
            this.slotMin = 1440;
        }
        if (req.getCancelDeadlineHours() != null) {
            this.cancelDeadlineHours = req.getCancelDeadlineHours();
        }
        if (req.getMaxReservationCount() != null) {
            this.maxReservationCount = req.getMaxReservationCount();
        }
        if (req.getFeeType() != null) {
            this.feeType = req.getFeeType();
        }
        // BASE_PLUS_EXTRA 전용 필드는 null 포함 항상 덮어쓴다.
        this.includedPersonCount = req.getIncludedPersonCount();
        this.extraPersonFee = req.getExtraPersonFee();
        // 신청/해지 기준일은 null 포함 항상 덮어쓴다.
        this.subscribeCutoffDay = req.getSubscribeCutoffDay();
        this.cancelCutoffDay = req.getCancelCutoffDay();
        // isActive는 항상 true로 고정한다.
        this.isActive = true;
    }
}
