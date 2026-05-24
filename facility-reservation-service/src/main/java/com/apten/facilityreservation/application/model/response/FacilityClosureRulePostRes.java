package com.apten.facilityreservation.application.model.response;

import com.apten.facilityreservation.domain.enums.ClosureRuleType;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

// 정기 휴무 규칙 등록 응답 DTO이다.
@Getter
@Builder
public class FacilityClosureRulePostRes {

    // 생성된 규칙 ID이다.
    private Long closureRuleId;

    // 시설 ID이다.
    private Long facilityId;

    // 규칙 유형이다.
    private ClosureRuleType ruleType;

    // 적용 요일 목록이다.
    private List<DayOfWeek> daysOfWeek;

    // N번째 주 목록이다.
    private List<Integer> weekOrdinals;

    // 차단 시작 시각이다.
    private LocalTime startTime;

    // 차단 종료 시각이다.
    private LocalTime endTime;

    // 규칙 적용 시작일이다.
    private LocalDate validFrom;

    // 규칙 적용 종료일이다.
    private LocalDate validUntil;

    // 생성 시각이다.
    private LocalDateTime createdAt;
}
