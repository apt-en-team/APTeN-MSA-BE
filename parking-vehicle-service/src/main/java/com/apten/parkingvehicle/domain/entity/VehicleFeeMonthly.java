package com.apten.parkingvehicle.domain.entity;

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

// 세대별 월 차량 비용 산정 결과를 저장하는 엔티티이다.
@Entity
@Table(
        name = "vehicle_fee_monthly",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_vehicle_fee_monthly_household_period", columnNames = {"household_id", "bill_year", "bill_month"})
        },
        indexes = {
                @Index(name = "idx_vehicle_fee_monthly_complex_id", columnList = "complex_id"),
                @Index(name = "idx_vehicle_fee_monthly_household_id", columnList = "household_id"),
                @Index(name = "idx_vehicle_fee_monthly_year_month", columnList = "bill_year, bill_month"),
                @Index(name = "idx_vehicle_fee_monthly_is_published", columnList = "is_published")
        }
)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleFeeMonthly extends BaseEntity {

    // 차량 비용 월집계 내부 PK이다.
    @Id
    @Tsid
    @Column(name = "id", nullable = false)
    private Long id;

    // 단지 ID이다.
    @Column(name = "complex_id", nullable = false)
    private Long complexId;

    // 세대 ID이다.
    @Column(name = "household_id", nullable = false)
    private Long householdId;

    // 청구 연도이다.
    @Column(name = "bill_year", nullable = false)
    private Integer billYear;

    // 청구 월이다.
    @Column(name = "bill_month", nullable = false)
    private Integer billMonth;

    // 승인 차량 수이다.
    @Builder.Default
    @Column(name = "approved_vehicle_count", nullable = false)
    private Integer approvedVehicleCount = 0;

    // 월 차량 비용이다.
    @Builder.Default
    @Column(name = "vehicle_fee", nullable = false, precision = 12, scale = 2)
    private BigDecimal vehicleFee = BigDecimal.ZERO;

    // 외부 발행 여부이다.
    @Builder.Default
    @Column(name = "is_published", nullable = false)
    private Boolean isPublished = false;

    // 발행 시각이다.
    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    // 차량 비용 집계 결과를 갱신한다.
    public void apply(Integer approvedVehicleCount, BigDecimal vehicleFee, Boolean isPublished, LocalDateTime publishedAt) {
        this.approvedVehicleCount = approvedVehicleCount;
        this.vehicleFee = vehicleFee;
        this.isPublished = isPublished;
        this.publishedAt = publishedAt;
    }
}
