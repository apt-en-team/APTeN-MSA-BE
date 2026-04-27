package com.apten.facilityreservation.domain.entity;

import com.apten.common.entity.BaseEntity;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 세대별 월 시설 이용 비용 집계 엔티티이다.
// Household Service로 발행할 시설 이용 비용 월 집계 결과를 저장한다.
@Entity
@Table(
        name = "facility_usage_monthly",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_facility_usage_monthly_household", columnNames = {"household_id", "usage_year", "usage_month"})
        },
        indexes = {
                @Index(name = "idx_facility_usage_monthly_complex_id", columnList = "complex_id"),
                @Index(name = "idx_facility_usage_monthly_household_id", columnList = "household_id"),
                @Index(name = "idx_facility_usage_monthly_year_month", columnList = "usage_year,usage_month"),
                @Index(name = "idx_facility_usage_monthly_published", columnList = "is_published")
        }
)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacilityUsageMonthly extends BaseEntity {

    // 월 집계 내부 PK이다.
    @Id
    @Tsid
    @Column(name = "id", nullable = false)
    private Long id;

    // 소속 단지 ID이다.
    @Column(name = "complex_id", nullable = false)
    private Long complexId;

    // Household Service 원본 세대 ID이다.
    @Column(name = "household_id", nullable = false)
    private Long householdId;

    // 이용 연도이다.
    @Column(name = "usage_year", nullable = false)
    private Integer usageYear;

    // 이용 월이다.
    @Column(name = "usage_month", nullable = false)
    private Integer usageMonth;

    // 세대별 합산 시설 이용 비용이다.
    @Builder.Default
    @Column(name = "facility_fee", nullable = false, precision = 12, scale = 2)
    private BigDecimal facilityFee = BigDecimal.ZERO;

    // Household Service 발행 여부이다.
    @Builder.Default
    @Column(name = "is_published", nullable = false)
    private Boolean isPublished = false;

    // 발행 완료 시각이다.
    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    // 월 집계 비용을 새 값으로 반영한다.
    public void changeFacilityFee(BigDecimal facilityFee) {
        this.facilityFee = facilityFee;
    }

    // 월 집계 발행 완료 상태로 바꾼다.
    public void markPublished() {
        this.isPublished = true;
        this.publishedAt = LocalDateTime.now();
    }
}
