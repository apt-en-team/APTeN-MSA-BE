package com.apten.facilityreservation.application.model.request;

import com.apten.facilityreservation.domain.enums.ClosureRuleType;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import lombok.Getter;

// 정기 휴무 규칙 수정 요청 DTO이다.
@Getter
public class FacilityClosureRulePatchReq {

    // 수정할 규칙 유형이다.
    private ClosureRuleType ruleType;

    // 수정할 적용 요일 목록이다.
    private List<DayOfWeek> daysOfWeek;

    // 수정할 N번째 주 목록이다. MONTHLY_NTH 전용이다.
    private List<Integer> weekOrdinals;

    // 수정할 차단 시작 시각이다. null이면 종일이다.
    private LocalTime startTime;

    // 수정할 차단 종료 시각이다. null이면 종일이다.
    private LocalTime endTime;

    // 수정할 적용 시작일이다.
    private LocalDate validFrom;

    // 수정할 적용 종료일이다.
    private LocalDate validUntil;

    // 수정할 휴무 사유이다.
    private String reason;
}
