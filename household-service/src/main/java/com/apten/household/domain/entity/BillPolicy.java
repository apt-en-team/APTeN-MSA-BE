package com.apten.household.domain.entity;

import com.apten.common.entity.BaseEntity;
import com.apten.household.application.model.request.BillPolicyPutReq;
import io.hypersistence.utils.hibernate.id.Tsid;
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

// 세대 월 청구 생성에 사용하는 기본 청구 정책 원본 엔티티이다.
// 기본 관리비와 납부기한, 연체료율처럼 청구 기준이 되는 값을 직접 관리한다.
@Entity
@Table(
        name = "bill_policy",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_bill_policy_complex_id", columnNames = "complex_id")
        },
        indexes = {
                @Index(name = "idx_bill_policy_complex_id", columnList = "complex_id"),
                @Index(name = "idx_bill_policy_is_active", columnList = "is_active")
        }
)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillPolicy extends BaseEntity {

    // 청구 정책 내부 PK이다.
    @Id
    @Tsid
    @Column(name = "id", nullable = false)
    private Long id;

    // 정책이 적용되는 단지 ID이다.
    @Column(name = "complex_id", nullable = false)
    private Long complexId;

    // 세대 청구에 공통으로 적용할 기본 관리비이다.
    @Builder.Default
    @Column(name = "base_fee", nullable = false, precision = 12, scale = 2)
    private BigDecimal baseFee = BigDecimal.ZERO;

    // 매월 납부 마감일로 사용할 일자이다.
    @Builder.Default
    @Column(name = "payment_due_day", nullable = false)
    private Integer paymentDueDay = 25;

    // 월 기준 연체료율이다.
    @Builder.Default
    @Column(name = "late_fee_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal lateFeeRate = BigDecimal.ZERO;

    // 연체료율이 어떤 기준으로 적용되는지 나타내는 문자열이다.
    @Builder.Default
    @Column(name = "late_fee_unit", nullable = false, length = 20)
    private String lateFeeUnit = "MONTHLY";

    // 현재 청구 생성에 이 정책을 사용할지 여부이다.
    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // 정책 적용 시작일이다.
    @Column(name = "start_date")
    private LocalDate startDate;

    // 정책 적용 종료일이다.
    @Column(name = "end_date")
    private LocalDate endDate;

    // 청구 기본 정책 요청값을 현재 엔티티에 반영한다.
    public void apply(BillPolicyPutReq req) {
        // 기본 관리비가 오면 새 값으로 교체한다.
        if (req.getBaseFee() != null) {
            this.baseFee = req.getBaseFee();
        }

        // 납부기한일이 오면 새 값으로 교체한다.
        if (req.getPaymentDueDay() != null) {
            this.paymentDueDay = req.getPaymentDueDay();
        }

        // 연체료율이 오면 새 값으로 교체한다.
        if (req.getLateFeeRate() != null) {
            this.lateFeeRate = req.getLateFeeRate();
        }

        // 활성 여부가 오면 정책 사용 여부를 함께 갱신한다.
        if (req.getIsActive() != null) {
            this.isActive = req.getIsActive();
        }
    }
}
