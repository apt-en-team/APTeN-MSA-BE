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

// 단지별 방문차량 요금 정책을 로컬 캐시에 저장하는 엔티티
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "visitor_policy_cache",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_visitor_policy_cache_complex_id", columnNames = "complex_id")
        },
        indexes = {
                @Index(name = "idx_visitor_policy_cache_complex_id", columnList = "complex_id"),
                @Index(name = "idx_visitor_policy_cache_active", columnList = "is_active")
        }
)
public class VisitorPolicyCache extends BaseEntity {

    // 외부 원본 방문차량 정책 ID
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    // 단지 ID
    @Column(name = "complex_id", nullable = false)
    private Long complexId;

    // 무료 시간
    @Column(name = "free_minutes", nullable = false)
    private Integer freeMinutes;

    // 시간당 요금
    @Column(name = "hour_fee", nullable = false, precision = 12, scale = 2)
    private BigDecimal hourFee;

    // 월 기준 시간
    @Column(name = "monthly_limit_hours", nullable = false)
    private Integer monthlyLimitHours;

    // 활성 여부
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    // 방문차량 정책 캐시 내용을 갱신한다
    public void apply(
            Long complexId,
            Integer freeMinutes,
            BigDecimal hourFee,
            Integer monthlyLimitHours,
            Boolean isActive
    ) {
        this.complexId = complexId;
        this.freeMinutes = freeMinutes;
        this.hourFee = hourFee;
        this.monthlyLimitHours = monthlyLimitHours;
        this.isActive = isActive;
    }
}
