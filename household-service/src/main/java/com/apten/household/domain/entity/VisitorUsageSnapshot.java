package com.apten.household.domain.entity;

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

// 세대별 월 누적 방문차량 이용시간을 저장하는 엔티티
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "visitor_usage_snapshot",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_visitor_usage_household_year_month", columnNames = {"household_id", "usage_year", "usage_month"})
        },
        indexes = {
                @Index(name = "idx_visitor_usage_complex_id", columnList = "complex_id"),
                @Index(name = "idx_visitor_usage_household_id", columnList = "household_id"),
                @Index(name = "idx_visitor_usage_year_month", columnList = "usage_year, usage_month")
        }
)
public class VisitorUsageSnapshot extends BaseEntity {

    // 내부 스냅샷 PK
    @Id
    @Tsid
    @Column(name = "id", nullable = false)
    private Long id;

    // 세대 ID
    @Column(name = "household_id", nullable = false)
    private Long householdId;

    // 단지 ID
    @Column(name = "complex_id", nullable = false)
    private Long complexId;

    // 집계 연도
    @Column(name = "usage_year", nullable = false)
    private Integer usageYear;

    // 집계 월
    @Column(name = "usage_month", nullable = false)
    private Integer usageMonth;

    // 총 이용 분
    @Column(name = "total_minutes", nullable = false)
    private Integer totalMinutes;

    // 총 이용 시간
    @Column(name = "total_hours", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalHours;

    // 방문차량 이용 스냅샷 내용을 갱신한다
    public void apply(
            Long householdId,
            Long complexId,
            Integer usageYear,
            Integer usageMonth,
            Integer totalMinutes,
            BigDecimal totalHours
    ) {
        this.householdId = householdId;
        this.complexId = complexId;
        this.usageYear = usageYear;
        this.usageMonth = usageMonth;
        this.totalMinutes = totalMinutes;
        this.totalHours = totalHours;
    }
}
