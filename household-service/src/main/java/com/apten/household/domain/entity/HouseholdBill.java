package com.apten.household.domain.entity;

import com.apten.common.entity.BaseEntity;
import com.apten.household.domain.enums.HouseholdBillStatus;
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
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 세대 월 청구 헤더를 저장하는 엔티티
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "household_bill",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_household_bill_year_month", columnNames = {"household_id", "bill_year", "bill_month"})
        },
        indexes = {
                @Index(name = "idx_household_bill_complex_id", columnList = "complex_id"),
                @Index(name = "idx_household_bill_status", columnList = "status"),
                @Index(name = "idx_household_bill_year_month", columnList = "bill_year, bill_month")
        }
)
public class HouseholdBill extends BaseEntity {

    // 청구 PK
    @Id
    @Tsid
    private Long id;

    // 세대 ID
    @Column(name = "household_id", nullable = false)
    private Long householdId;

    // 단지 ID
    @Column(name = "complex_id", nullable = false)
    private Long complexId;

    // 청구 연도
    @Column(name = "bill_year", nullable = false)
    private Integer billYear;

    // 청구 월
    @Column(name = "bill_month", nullable = false)
    private Integer billMonth;

    // 기본 관리비
    @Column(name = "base_fee", nullable = false, precision = 12, scale = 2)
    private BigDecimal baseFee;

    // 차량 비용
    @Column(name = "vehicle_fee", nullable = false, precision = 12, scale = 2)
    private BigDecimal vehicleFee;

    // 시설 비용
    @Column(name = "facility_fee", nullable = false, precision = 12, scale = 2)
    private BigDecimal facilityFee;

    // 방문차량 비용
    @Column(name = "visitor_fee", nullable = false, precision = 12, scale = 2)
    private BigDecimal visitorFee;

    // 총 청구 금액
    @Column(name = "total_fee", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalFee;

    // 청구 상태
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private HouseholdBillStatus status;

    // 확정 시각
    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    // 청구 금액을 갱신한다
    public void updateAmounts(
            BigDecimal baseFee,
            BigDecimal vehicleFee,
            BigDecimal facilityFee,
            BigDecimal visitorFee,
            BigDecimal totalFee
    ) {
        this.baseFee = baseFee;
        this.vehicleFee = vehicleFee;
        this.facilityFee = facilityFee;
        this.visitorFee = visitorFee;
        this.totalFee = totalFee;
    }

    // 청구 상태를 변경한다
    public void changeStatus(HouseholdBillStatus status) {
        this.status = status;
    }

    // 청구 확정 시각을 갱신한다
    public void confirm(LocalDateTime confirmedAt) {
        this.status = HouseholdBillStatus.CONFIRMED;
        this.confirmedAt = confirmedAt;
    }
}
