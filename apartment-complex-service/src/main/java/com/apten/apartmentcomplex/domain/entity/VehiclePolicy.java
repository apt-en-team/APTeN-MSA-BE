package com.apten.apartmentcomplex.domain.entity;

import com.apten.common.entity.BaseEntity;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 단지 차량 정책을 저장하는 엔티티
// 세대당 차량 허용 기준과 월 요금 계산 기준을 담는다
@Entity
@Table(
        name = "vehicle_policy",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_vehicle_policy_complex_car_count", columnNames = {"complex_id", "car_count"})
        },
        indexes = {
                @Index(name = "idx_vehicle_policy_complex_id", columnList = "complex_id"),
                @Index(name = "idx_vehicle_policy_car_count", columnList = "car_count"),
                @Index(name = "idx_vehicle_policy_is_active", columnList = "is_active")
        }
)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehiclePolicy extends BaseEntity {

    // 차량 정책 내부 PK
    @Id
    @Tsid
    @Column(name = "id", nullable = false)
    private Long id;

    // 정책이 속한 단지 ID
    @Column(name = "complex_id", nullable = false)
    private Long complexId;

    // 차량 대수 기준
    @Column(name = "car_count", nullable = false)
    private Integer carCount;

    // 월 요금
    @Column(name = "monthly_fee", nullable = false)
    private BigDecimal monthlyFee;

    // 등록 제한 규칙 사용 여부
    @Column(name = "is_limit_rule", nullable = false)
    private Boolean isLimitRule;

    // 정책 활성 여부
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    // 차량 정책 값을 갱신할 때 사용한다
    public void apply(Integer carCount, BigDecimal monthlyFee, Boolean isLimitRule, Boolean isActive) {
        this.carCount = carCount;
        this.monthlyFee = monthlyFee;
        this.isLimitRule = isLimitRule;
        this.isActive = isActive;
    }
}
