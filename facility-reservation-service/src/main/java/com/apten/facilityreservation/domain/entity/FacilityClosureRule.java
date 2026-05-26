package com.apten.facilityreservation.domain.entity;

import com.apten.common.entity.BaseEntity;
import com.apten.facilityreservation.domain.enums.ClosureRuleType;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 시설 정기 휴무 규칙 엔티티이다.
// 매주 특정 요일, 매월 N번째 특정 요일처럼 반복 패턴으로 예약을 차단한다.
// FacilityBlockTime(임시·단건 차단)과 달리 규칙 1행으로 무기한 적용이 가능하다.
@Entity
@Table(
        name = "facility_closure_rule",
        indexes = {
                @Index(name = "idx_closure_rule_facility_id", columnList = "facility_id"),
                @Index(name = "idx_closure_rule_is_active", columnList = "is_active")
        }
)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacilityClosureRule extends BaseEntity {

    // 정기 휴무 규칙 내부 PK이다.
    @Id
    @Tsid
    @Column(name = "id", nullable = false)
    private Long id;

    // 시설 ID이다.
    @Column(name = "facility_id", nullable = false)
    private Long facilityId;

    // null이면 시설 전체 적용, 값 있으면 해당 좌석만 적용한다.
    @Column(name = "seat_id")
    private Long seatId;

    // 반복 규칙 유형이다. WEEKLY(매주) 또는 MONTHLY_NTH(매월 N번째)
    @Enumerated(EnumType.STRING)
    @Column(name = "rule_type", nullable = false, length = 20)
    private ClosureRuleType ruleType;

    // 쉼표로 구분된 DayOfWeek 이름이다. 예: "MONDAY,WEDNESDAY"
    @Column(name = "days_of_week", nullable = false, length = 100)
    private String daysOfWeek;

    // MONTHLY_NTH 전용: 몇 번째 주인지 쉼표 구분 정수이다. 예: "2,4" (2번째·4번째)
    // WEEKLY이면 null이다.
    @Column(name = "week_ordinals", length = 20)
    private String weekOrdinals;

    // 차단 시작 시각이다. null이면 종일 차단이다.
    @Column(name = "start_time")
    private LocalTime startTime;

    // 차단 종료 시각이다. null이면 종일 차단이다.
    @Column(name = "end_time")
    private LocalTime endTime;

    // 규칙 적용 시작일이다. null이면 즉시 적용이다.
    @Column(name = "valid_from")
    private LocalDate validFrom;

    // 규칙 적용 종료일이다. null이면 무기한 적용이다.
    @Column(name = "valid_until")
    private LocalDate validUntil;

    // 휴무 사유이다.
    @Column(name = "reason", length = 255)
    private String reason;

    // 활성 여부이다. false면 예약 차단에서 제외된다.
    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // daysOfWeek 문자열을 DayOfWeek 집합으로 변환한다.
    public Set<DayOfWeek> parseDaysOfWeek() {
        if (daysOfWeek == null || daysOfWeek.isBlank()) return Set.of();
        return Arrays.stream(daysOfWeek.split(","))
                .map(String::trim)
                .map(DayOfWeek::valueOf)
                .collect(Collectors.toSet());
    }

    // weekOrdinals 문자열을 Integer 집합으로 변환한다. (1=첫째 주, 5=다섯째 주)
    public Set<Integer> parseWeekOrdinals() {
        if (weekOrdinals == null || weekOrdinals.isBlank()) return Set.of();
        return Arrays.stream(weekOrdinals.split(","))
                .map(String::trim)
                .map(Integer::parseInt)
                .collect(Collectors.toSet());
    }

    // 주어진 날짜가 이 규칙에 의해 차단되는지 평가한다.
    // validFrom/validUntil → 요일 → (MONTHLY_NTH이면 몇 번째 주) 순서로 검사한다.
    public boolean isDateBlocked(LocalDate date) {
        // 적용 기간 밖이면 차단 아님
        if (validFrom != null && date.isBefore(validFrom)) return false;
        if (validUntil != null && date.isAfter(validUntil)) return false;

        // 요일이 규칙 요일 목록에 없으면 차단 아님
        if (!parseDaysOfWeek().contains(date.getDayOfWeek())) return false;

        // MONTHLY_NTH: 해당 월에서 몇 번째 주인지 추가 검사
        if (ruleType == ClosureRuleType.MONTHLY_NTH) {
            int weekOrdinal = (date.getDayOfMonth() - 1) / 7 + 1;
            return parseWeekOrdinals().contains(weekOrdinal);
        }

        // WEEKLY: 요일만 맞으면 차단
        return true;
    }

    // 규칙이 종일 차단인지 반환한다. startTime이 null이면 종일이다.
    public boolean isAllDay() {
        return startTime == null;
    }

    // 규칙 내용을 수정한다.
    public void update(ClosureRuleType ruleType, String daysOfWeek, String weekOrdinals,
                       LocalTime startTime, LocalTime endTime,
                       LocalDate validFrom, LocalDate validUntil, String reason) {
        this.ruleType = ruleType;
        this.daysOfWeek = daysOfWeek;
        this.weekOrdinals = weekOrdinals;
        this.startTime = startTime;
        this.endTime = endTime;
        this.validFrom = validFrom;
        this.validUntil = validUntil;
        this.reason = reason;
    }

    // 규칙을 비활성화한다.
    public void deactivate() {
        this.isActive = false;
    }
}
