package com.apten.household.domain.entity;

import com.apten.common.entity.BaseEntity;
import com.apten.household.domain.enums.HouseholdBillStatus;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.time.LocalDate;
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
    @Column(name = "id", nullable = false)
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
    @Builder.Default
    @Column(name = "base_fee", nullable = false, precision = 12, scale = 2)
    private BigDecimal baseFee = BigDecimal.ZERO;

    // 차량 비용
    @Builder.Default
    @Column(name = "vehicle_fee", nullable = false, precision = 12, scale = 2)
    private BigDecimal vehicleFee = BigDecimal.ZERO;

    // 시설 비용
    @Builder.Default
    @Column(name = "facility_fee", nullable = false, precision = 12, scale = 2)
    private BigDecimal facilityFee = BigDecimal.ZERO;

    // 방문차량 비용
    @Builder.Default
    @Column(name = "visitor_fee", nullable = false, precision = 12, scale = 2)
    private BigDecimal visitorFee = BigDecimal.ZERO;

    // 총 청구 금액
    @Builder.Default
    @Column(name = "total_fee", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalFee = BigDecimal.ZERO;

    // 연체료율
    @Builder.Default
    @Column(name = "late_fee_rate", nullable = false, precision = 5, scale = 2, columnDefinition = "decimal(5,2) default 0")
    private BigDecimal lateFeeRate = BigDecimal.ZERO;

    // 연체료
    @Builder.Default
    @Column(name = "late_fee", nullable = false, precision = 12, scale = 2, columnDefinition = "decimal(12,2) default 0")
    private BigDecimal lateFee = BigDecimal.ZERO;

    // 실제 납부 금액
    @Builder.Default
    @Column(name = "payable_amount", nullable = false, precision = 12, scale = 2, columnDefinition = "decimal(12,2) default 0")
    private BigDecimal payableAmount = BigDecimal.ZERO;

    // 청구 상태
    @Builder.Default
    @Column(name = "status", nullable = false, length = 20)
    private HouseholdBillStatus status = HouseholdBillStatus.DRAFT;

    // 고지서 발송일
    @Column(name = "send_date")
    private LocalDate sendDate;

    // 납부 기한일
    @Column(name = "due_date")
    private LocalDate dueDate;

    // 연체 시작일
    @Column(name = "overdue_start_date")
    private LocalDate overdueStartDate;

    // 홈 화면 노출 종료일
    @Column(name = "home_display_until")
    private LocalDate homeDisplayUntil;

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
        this.payableAmount = totalFee.add(this.lateFee);
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

    // 청구서의 공개, 납부, 연체, 홈 노출 일정을 저장한다.
    public void applySchedule(
            LocalDate sendDate,
            LocalDate dueDate,
            LocalDate overdueStartDate,
            LocalDate homeDisplayUntil,
            BigDecimal lateFeeRate
    ) {
        this.sendDate = sendDate;
        this.dueDate = dueDate;
        this.overdueStartDate = overdueStartDate;
        this.homeDisplayUntil = homeDisplayUntil;
        this.lateFeeRate = lateFeeRate;
    }

    // 연체료와 실제 납부 금액을 갱신한다
    public void updateLateFee(BigDecimal lateFee) {
        this.lateFee = lateFee;
        this.payableAmount = this.totalFee.add(lateFee);
    }

    // 청구 확정을 취소하고 임시 계산 상태로 되돌린다 (FR-426)
    public void unconfirm() {
        this.status = HouseholdBillStatus.DRAFT;
        this.confirmedAt = null;
    }
}
