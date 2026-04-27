package com.apten.facilityreservation.domain.entity;

import com.apten.common.entity.BaseEntity;
import com.apten.facilityreservation.application.model.request.FacilityPolicyPutReq;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 시설 타입별 기본 정책을 관리하는 원본 엔티티이다.
// 개별 시설 override가 없을 때 기본 요금과 예약 단위, 취소 정책 기준으로 사용한다.
@Entity
@Table(
        name = "facility_policy",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_facility_policy_complex_type",
                        columnNames = {"complex_id", "facility_type_code"}
                )
        },
        indexes = {
                @Index(name = "idx_facility_policy_complex_id", columnList = "complex_id"),
                @Index(name = "idx_facility_policy_type", columnList = "facility_type_code"),
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

    // 정책이 적용되는 단지 ID이다.
    @Column(name = "complex_id", nullable = false)
    private Long complexId;

    // 정책이 적용되는 시설 타입 코드이다.
    @Column(name = "facility_type_code", nullable = false, length = 30)
    private String facilityTypeCode;

    // 시설 타입 기본 요금이다.
    @Builder.Default
    @Column(name = "base_fee", nullable = false, precision = 12, scale = 2)
    private BigDecimal baseFee = BigDecimal.ZERO;

    // 시설 타입 기본 예약 단위이다.
    @Builder.Default
    @Column(name = "slot_min", nullable = false)
    private Integer slotMin = 30;

    // 예약 시작 몇 시간 전까지 취소 가능한지 나타내는 값이다.
    @Builder.Default
    @Column(name = "cancel_deadline_hours", nullable = false)
    private Integer cancelDeadlineHours = 2;

    // GX 대기 허용 여부이다.
    @Builder.Default
    @Column(name = "gx_waiting_enabled", nullable = false)
    private Boolean gxWaitingEnabled = false;

    // 정책 활성 여부이다.
    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // 요청값을 시설 정책 엔티티에 반영한다.
    public void apply(FacilityPolicyPutReq req) {
        // 시설 타입 코드가 오면 정책 대상을 새 값으로 교체한다.
        if (req.getFacilityTypeCode() != null) {
            this.facilityTypeCode = req.getFacilityTypeCode();
        }

        // 기본 요금이 오면 새 값으로 교체한다.
        if (req.getBaseFee() != null) {
            this.baseFee = req.getBaseFee();
        }

        // 예약 단위가 오면 새 값으로 교체한다.
        if (req.getSlotMin() != null) {
            this.slotMin = req.getSlotMin();
        }

        // 취소 마감 시간이 오면 새 값으로 교체한다.
        if (req.getCancelDeadlineHours() != null) {
            this.cancelDeadlineHours = req.getCancelDeadlineHours();
        }

        // GX 대기 허용 여부가 오면 새 값으로 교체한다.
        if (req.getGxWaitingEnabled() != null) {
            this.gxWaitingEnabled = req.getGxWaitingEnabled();
        }

        // 활성 여부가 오면 정책 사용 여부를 함께 갱신한다.
        if (req.getIsActive() != null) {
            this.isActive = req.getIsActive();
        }
    }
}
