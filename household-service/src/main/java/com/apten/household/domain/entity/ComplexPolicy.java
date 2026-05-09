package com.apten.household.domain.entity;

import com.apten.common.entity.BaseEntity;
import com.apten.household.application.model.request.ComplexPolicyPutReq;
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

// 단지별 기본 관리비 정책 원본을 관리하는 엔티티이다.
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "complex_policy",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_complex_policy_complex_id", columnNames = "complex_id")
        },
        indexes = {
                @Index(name = "idx_complex_policy_complex_id", columnList = "complex_id")
        }
)
public class ComplexPolicy extends BaseEntity {

    // 청구 정책 내부 PK이다.
    @Id
    @Tsid
    @Column(name = "id", nullable = false)
    private Long id;

    // 정책이 적용되는 단지 ID이다.
    @Column(name = "complex_id", nullable = false)
    private Long complexId;

    // 세대 청구 기본 관리비이다.
    @Builder.Default
    @Column(name = "base_fee", nullable = false, precision = 12, scale = 2)
    private BigDecimal baseFee = BigDecimal.ZERO;

    // 매월 납부기한 일자이다.
    @Builder.Default
    @Column(name = "due_day", nullable = false)
    private Integer dueDay = 25;

    // 연체료율이다.
    @Builder.Default
    @Column(name = "late_fee_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal lateFeeRate = BigDecimal.ZERO;

    // 정책 활성 여부이다.
    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // 요청값을 현재 정책 엔티티에 반영한다.
    public void apply(ComplexPolicyPutReq req) {
        // 기본 관리비를 요청값으로 갱신한다.
        if (req.getBaseFee() != null) {
            this.baseFee = req.getBaseFee();
        }

        // 납부기한일을 요청값으로 갱신한다.
        if (req.getDueDay() != null) {
            this.dueDay = req.getDueDay();
        }

        // 연체료율을 요청값으로 갱신한다.
        if (req.getLateFeeRate() != null) {
            this.lateFeeRate = req.getLateFeeRate();
        }

        // 활성 여부를 요청값으로 갱신한다.
        if (req.getIsActive() != null) {
            this.isActive = req.getIsActive();
        }
    }
}
