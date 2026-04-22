package com.apten.parkingvehicle.domain.entity;

import com.apten.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 단지별 차량 정책 캐시 엔티티
@Entity
@Table(name = "vehicle_policy_cache")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehiclePolicyCache extends BaseEntity {

    // 원본 정책 PK
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    // 소속 단지 ID
    @Column(name = "complex_id", nullable = false)
    private Long complexId;

    // 차량 대수 기준
    @Column(name = "car_count", nullable = false)
    private Integer carCount;

    // 월 요금
    @Column(name = "monthly_fee", nullable = false, precision = 12, scale = 2)
    private BigDecimal monthlyFee;

    // 제한 규칙 사용 여부
    @Column(name = "is_limit_rule", nullable = false)
    private Boolean isLimitRule;

    // 활성 여부
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;
}
