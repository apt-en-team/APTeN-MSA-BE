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

// 단지별 방문차량 정책 캐시 엔티티
@Entity
@Table(name = "visitor_policy_cache")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisitorPolicyCache extends BaseEntity {

    // 원본 정책 PK
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    // 소속 단지 ID
    @Column(name = "complex_id", nullable = false)
    private Long complexId;

    // 무료 분
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
}
