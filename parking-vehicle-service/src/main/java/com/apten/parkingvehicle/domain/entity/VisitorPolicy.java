package com.apten.parkingvehicle.domain.entity;

import com.apten.parkingvehicle.application.model.request.VisitorPolicyPutReq;
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

// 단지별 방문차량(고정 방문차량 포함) 월 기본 제공 시간과 초과 시간당 요금 정책 원본 엔티티이다.
// parking-vehicle-service가 직접 방문차량 정책을 관리할 때 기준이 되는 테이블이다.
@Entity
@Table(
        name = "visitor_policy",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_visitor_policy_complex_id", columnNames = "complex_id")
        },
        indexes = {
                @Index(name = "idx_visitor_policy_complex_id", columnList = "complex_id"),
                @Index(name = "idx_visitor_policy_is_active", columnList = "is_active")
        }
)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisitorPolicy extends BaseEntity {

    // 방문차량 정책 원본 PK이다.
    @Id
    @Tsid
    @Column(name = "id", nullable = false)
    private Long id;

    // 정책이 속한 단지 ID이다.
    @Column(name = "complex_id", nullable = false)
    private Long complexId;

    // 월 기본 제공 시간 초과 시 시간당 요금이다.
    @Builder.Default
    @Column(name = "hour_fee", nullable = false, precision = 12, scale = 2)
    private BigDecimal hourFee = BigDecimal.ZERO;

    // 월 기본 제공 시간이다. 이 시간을 초과하면 과금이 시작된다.
    @Builder.Default
    @Column(name = "monthly_limit_hours", nullable = false)
    private Integer monthlyLimitHours = 300;

    // 현재 정책 활성 여부이다.
    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // 방문차량 정책 요청값을 현재 엔티티에 반영한다.
    public void apply(VisitorPolicyPutReq req) {
        // 시간당 요금이 들어오면 기존 값을 새 값으로 교체한다.
        if (req.getHourFee() != null) {
            this.hourFee = req.getHourFee();
        }

        // 월 기본 제공 시간이 들어오면 기존 값을 새 값으로 교체한다.
        if (req.getMonthlyLimitHours() != null) {
            this.monthlyLimitHours = req.getMonthlyLimitHours();
        }

        // 활성 여부가 들어오면 운영 여부를 함께 갱신한다.
        if (req.getIsActive() != null) {
            this.isActive = req.getIsActive();
        }
    }
}
