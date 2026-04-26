package com.apten.apartmentcomplex.domain.entity;

import com.apten.common.entity.BaseEntity;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.kafka.common.protocol.types.Field;

// 단지 기본 운영 정책을 저장하는 엔티티
// 기본 관리비, 납부기한, 연체료 정책을 이 테이블이 가진다
@Entity
@Table(
        name = "complex_policy",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_complex_policy_complex_id", columnNames = "complex_id")
        },
        indexes = {
                @Index(name = "idx_complex_policy_complex_id", columnList = "complex_id"),
                @Index(name = "idx_complex_policy_is_active", columnList = "is_active")
        }
)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplexPolicy extends BaseEntity {

    // 정책 내부 PK
    @Id
    @Tsid
    @Column(name = "id", nullable = false)
    private Long id;

    // 정책이 속한 단지 ID
    @Column(name = "complex_id", nullable = false)
    private Long complexId;

    // 기본 관리비
    @Column(name = "base_fee", nullable = false, precision = 12, scale = 2)
    private BigDecimal baseFee;

    // 매월 납부기한일
    @Column(name = "payment_due_day", nullable = false)
    private Integer paymentDueDay;

    // 월 기준 연체료율
    @Column(name = "late_fee_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal lateFeeRate;

    // 연체료 기준
    @Column(name = "late_fee_unit", nullable = false, length = 20)
    private String lateFeeUnit;

    // 정책 활성 여부
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    // 적용 시작일
    @Column(name = "start_date")
    private LocalDate startDate;

    // 적용 종료일
    @Column(name = "end_date")
    private LocalDate endDate;

    // 기본 정책 값을 갱신할 때 사용
    public void apply(
            BigDecimal baseFee,
            Integer paymentDueDay,
            BigDecimal lateFeeRate,
            String lateFeeUnit,
            Boolean isActive,
            LocalDate startDate,
            LocalDate endDate
    ) {
        this.baseFee = baseFee;
        this.paymentDueDay = paymentDueDay;
        this.lateFeeRate = lateFeeRate;
        this.lateFeeUnit = lateFeeUnit;
        this.isActive = isActive;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}