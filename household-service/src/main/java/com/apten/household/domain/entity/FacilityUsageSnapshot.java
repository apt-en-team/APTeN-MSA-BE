package com.apten.household.domain.entity;

import com.apten.common.entity.BaseEntity;
import com.apten.household.domain.enums.FacilityUsageStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 비용 계산에 사용할 시설 이용 스냅샷 엔티티
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "facility_usage_snapshot",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_facility_usage_snapshot_id", columnNames = "id")
        },
        indexes = {
                @Index(name = "idx_facility_usage_household_id", columnList = "household_id"),
                @Index(name = "idx_facility_usage_complex_id", columnList = "complex_id"),
                @Index(name = "idx_facility_usage_date", columnList = "usage_date"),
                @Index(name = "idx_facility_usage_status", columnList = "status")
        }
)
public class FacilityUsageSnapshot extends BaseEntity {

    // 외부 원본 예약 ID
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    // 세대 ID
    @Column(name = "household_id", nullable = false)
    private Long householdId;

    // 단지 ID
    @Column(name = "complex_id", nullable = false)
    private Long complexId;

    // 시설 ID
    @Column(name = "facility_id", nullable = false)
    private Long facilityId;

    // 시설명
    @Column(name = "facility_name", nullable = false, length = 100)
    private String facilityName;

    // 이용일
    @Column(name = "usage_date", nullable = false)
    private LocalDate usageDate;

    // 이용 금액
    @Builder.Default
    @Column(name = "usage_fee", nullable = false, precision = 12, scale = 2)
    private BigDecimal usageFee = BigDecimal.ZERO;

    // 이용 상태
    @Builder.Default
    @Column(name = "status", nullable = false, length = 20)
    private FacilityUsageStatus status = FacilityUsageStatus.COMPLETED;

    // 시설 이용 스냅샷 내용을 갱신한다
    public void apply(
            Long householdId,
            Long complexId,
            Long facilityId,
            String facilityName,
            LocalDate usageDate,
            BigDecimal usageFee,
            FacilityUsageStatus status
    ) {
        this.householdId = householdId;
        this.complexId = complexId;
        this.facilityId = facilityId;
        this.facilityName = facilityName;
        this.usageDate = usageDate;
        this.usageFee = usageFee;
        this.status = status;
    }
}
