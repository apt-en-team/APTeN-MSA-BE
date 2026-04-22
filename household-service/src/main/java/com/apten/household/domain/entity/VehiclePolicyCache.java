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

// 단지별 차량 요금 정책을 로컬 캐시에 저장하는 엔티티
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "vehicle_policy_cache",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_vehicle_policy_cache_complex_car_count", columnNames = {"complex_id", "car_count"})
        },
        indexes = {
                @Index(name = "idx_vehicle_policy_cache_complex_id", columnList = "complex_id"),
                @Index(name = "idx_vehicle_policy_cache_active", columnList = "is_active")
        }
)
public class VehiclePolicyCache extends BaseEntity {

    // 외부 원본 차량 정책 ID
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    // 단지 ID
    @Column(name = "complex_id", nullable = false)
    private Long complexId;

    // 차량 대수 기준
    @Column(name = "car_count", nullable = false)
    private Integer carCount;

    // 월 요금
    @Column(name = "monthly_fee", nullable = false, precision = 12, scale = 2)
    private BigDecimal monthlyFee;

    // 등록 제한 규칙 사용 여부
    @Column(name = "is_limit_rule", nullable = false)
    private Boolean isLimitRule;

    // 활성 여부
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    // 차량 정책 캐시 내용을 갱신한다
    public void apply(
            Long complexId,
            Integer carCount,
            BigDecimal monthlyFee,
            Boolean isLimitRule,
            Boolean isActive
    ) {
        this.complexId = complexId;
        this.carCount = carCount;
        this.monthlyFee = monthlyFee;
        this.isLimitRule = isLimitRule;
        this.isActive = isActive;
    }
}
