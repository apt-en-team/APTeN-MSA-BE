package com.apten.parkingvehicle.domain.entity;

import com.apten.common.entity.BaseEntity;
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

// 단지별 차량 대수별 월 요금 정책 원본 엔티티이다.
// parking-vehicle-service가 직접 차량 정책을 관리할 때 기준이 되는 테이블이다.
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

    // 차량 정책 원본 PK이다.
    @Id
    @Tsid
    @Column(name = "id", nullable = false)
    private Long id;

    // 정책이 속한 단지 ID이다.
    @Column(name = "complex_id", nullable = false)
    private Long complexId;

    // 차량 대수 기준이다.
    @Column(name = "car_count", nullable = false)
    private Integer carCount;

    // 차량 대수별 월 요금이다.
    @Builder.Default
    @Column(name = "monthly_fee", nullable = false, precision = 12, scale = 2)
    private BigDecimal monthlyFee = BigDecimal.ZERO;

    // 등록 제한 규칙 사용 여부이다.
    @Builder.Default
    @Column(name = "is_limit_rule", nullable = false)
    private Boolean isLimitRule = true;

    // 현재 정책 활성 여부이다.
    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}
