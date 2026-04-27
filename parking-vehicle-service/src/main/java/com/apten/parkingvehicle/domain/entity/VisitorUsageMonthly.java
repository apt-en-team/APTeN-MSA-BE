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
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 세대별 방문차량 월 이용시간 집계 결과를 저장하는 엔티티
@Entity
@Table(
        name = "visitor_usage_monthly",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_visitor_usage_monthly_household_period", columnNames = {"household_id", "usage_year", "usage_month"})
        },
        indexes = {
                @Index(name = "idx_visitor_usage_monthly_complex_id", columnList = "complex_id"),
                @Index(name = "idx_visitor_usage_monthly_household_id", columnList = "household_id"),
                @Index(name = "idx_visitor_usage_monthly_year_month", columnList = "usage_year, usage_month")
        }
)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisitorUsageMonthly extends BaseEntity {

    // 월 집계 내부 PK
    @Id
    @Tsid
    @Column(name = "id", nullable = false)
    private Long id;

    // 소속 단지 ID
    @Column(name = "complex_id", nullable = false)
    private Long complexId;

    // 세대 ID
    @Column(name = "household_id", nullable = false)
    private Long householdId;

    // 집계 연도
    @Column(name = "usage_year", nullable = false)
    private Integer usageYear;

    // 집계 월
    @Column(name = "usage_month", nullable = false)
    private Integer usageMonth;

    // 총 이용 분
    @Builder.Default
    @Column(name = "total_minutes", nullable = false)
    private Integer totalMinutes = 0;

    // 총 이용 시간
    @Builder.Default
    @Column(name = "total_hours", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalHours = BigDecimal.ZERO;

    // 무료 이용 분
    @Builder.Default
    @Column(name = "free_minutes", nullable = false)
    private Integer freeMinutes = 0;

    // 초과 이용 분
    @Builder.Default
    @Column(name = "extra_minutes", nullable = false)
    private Integer extraMinutes = 0;

    // 월 방문차량 비용
    @Builder.Default
    @Column(name = "visitor_fee", nullable = false, precision = 12, scale = 2)
    private BigDecimal visitorFee = BigDecimal.ZERO;

    // 발행 여부
    @Builder.Default
    @Column(name = "is_published", nullable = false)
    private Boolean isPublished = false;

    // 발행 일시
    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    // 월 집계 결과를 반영한다
    public void apply(
            Integer totalMinutes,
            BigDecimal totalHours,
            Integer freeMinutes,
            Integer extraMinutes,
            BigDecimal visitorFee,
            Boolean isPublished,
            LocalDateTime publishedAt
    ) {
        this.totalMinutes = totalMinutes;
        this.totalHours = totalHours;
        this.freeMinutes = freeMinutes;
        this.extraMinutes = extraMinutes;
        this.visitorFee = visitorFee;
        this.isPublished = isPublished;
        this.publishedAt = publishedAt;
    }
}
