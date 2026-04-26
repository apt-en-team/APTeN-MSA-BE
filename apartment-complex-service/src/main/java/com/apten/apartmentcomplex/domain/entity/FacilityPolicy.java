package com.apten.apartmentcomplex.domain.entity;

import com.apten.apartmentcomplex.application.model.request.FacilityPolicyPutReq;
import com.apten.common.entity.BaseEntity;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 시설 정책을 저장하는 엔티티
// 시설 유형별 기본 요금과 예약 단위를 관리한다
@Entity
@Table(
        name = "facility_policy",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_facility_policy_complex_type", columnNames = {"complex_id", "facility_type_code"})
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

    // 시설 정책 내부 PK
    @Id
    @Tsid
    @Column(name = "id", nullable = false)
    private Long id;

    // 정책이 속한 단지 ID
    @Column(name = "complex_id", nullable = false)
    private Long complexId;

    // 시설 유형 코드
    @Column(name = "facility_type_code", nullable = false)
    private String facilityTypeCode;

    // 기본 요금
    @Column(name = "base_fee", nullable = false)
    private BigDecimal baseFee = BigDecimal.ZERO;

    // 예약 단위 분
    @Column(name = "slot_min")
    private Integer slotMin = null;

    // 취소 마감 시간
    @Column(name = "cancel_deadline_hours", nullable = false)
    private Integer cancelDeadlineHours = 2;

    // GX대기 허용 여부 (일반시설은 기본값 falsa. gx일 경우만 true로 표기)
    @Column(name = "gx_waiting_enabled", nullable = false)
    private Boolean gxWaitingEnable = false;

    // 정책 활성 여부
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    public void apply(FacilityPolicyPutReq req) {
        if (req.getFacilityTypeCode() != null) {
            this.facilityTypeCode = req.getFacilityTypeCode();
        }
        if (req.getBaseFee() != null) {
            this.baseFee = req.getBaseFee();
        }
        if (req.getSlotMin() != null) {
            this.slotMin = req.getSlotMin();
        }
        if (req.getCancelDeadlineHours() != null) {
            this.cancelDeadlineHours = req.getCancelDeadlineHours();
        }
        if (req.getGxWaitingEnabled() != null) {
            this.gxWaitingEnable = req.getGxWaitingEnabled();
        }
    }

}
