package com.apten.apartmentcomplex.domain.entity;

import com.apten.common.entity.BaseEntity;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 방문 차량 정책을 저장하는 엔티티
// 무료 시간과 초과 요금 계산 기준을 이 테이블이 가진다
@Entity
@Table(name = "visitor_policy")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisitorPolicy extends BaseEntity {

    // 방문 차량 정책 내부 PK
    @Id
    @Tsid
    @Column(name = "id", nullable = false)
    private Long id;

    // 정책이 속한 단지 ID
    @Column(name = "complex_id", nullable = false)
    private Long complexId;

    // 무료 허용 분
    @Column(name = "free_minutes", nullable = false)
    private Integer freeMinutes;

    // 시간당 요금
    @Column(name = "hour_fee", nullable = false)
    private BigDecimal hourFee;

    // 월 기준 최대 허용 시간
    @Column(name = "monthly_limit_hours", nullable = false)
    private Integer monthlyLimitHours;

    // 정책 활성 여부
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    // 방문 차량 정책 값을 갱신할 때 사용한다
    public void apply(
            Integer freeMinutes,
            BigDecimal hourFee,
            Integer monthlyLimitHours,
            Boolean isActive
    ) {
        this.freeMinutes = freeMinutes;
        this.hourFee = hourFee;
        this.monthlyLimitHours = monthlyLimitHours;
        this.isActive = isActive;
    }
}
