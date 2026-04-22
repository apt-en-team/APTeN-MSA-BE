package com.apten.household.domain.entity;

import com.apten.common.entity.BaseEntity;
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

// 단지 기본 관리비 정책을 로컬 캐시에 저장하는 엔티티
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "complex_policy_cache",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_complex_policy_cache_complex_id", columnNames = "complex_id")
        },
        indexes = {
                @Index(name = "idx_complex_policy_cache_complex_id", columnList = "complex_id"),
                @Index(name = "idx_complex_policy_cache_active", columnList = "is_active")
        }
)
public class ComplexPolicyCache extends BaseEntity {

    // 외부 원본 정책 ID
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    // 단지 ID
    @Column(name = "complex_id", nullable = false)
    private Long complexId;

    // 기본 관리비
    @Column(name = "base_fee", nullable = false, precision = 12, scale = 2)
    private BigDecimal baseFee;

    // 기본 예약 단위
    @Column(name = "default_slot_min", nullable = false)
    private Integer defaultSlotMin;

    // 활성 여부
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    // 정책 캐시 내용을 갱신한다
    public void apply(Long complexId, BigDecimal baseFee, Integer defaultSlotMin, Boolean isActive) {
        this.complexId = complexId;
        this.baseFee = baseFee;
        this.defaultSlotMin = defaultSlotMin;
        this.isActive = isActive;
    }
}
