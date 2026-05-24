package com.apten.facilityreservation.application.model.request;

import com.apten.facilityreservation.domain.enums.ClosureRuleType;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import lombok.Getter;

// 정기 휴무 규칙 등록 요청 DTO이다.
@Getter
public class FacilityClosureRulePostReq {

    // 규칙 유형이다. WEEKLY(매주) 또는 MONTHLY_NTH(매월 N번째)
    private ClosureRuleType ruleType;

    // 적용 요일 목록이다. 예: [MONDAY, WEDNESDAY]
    private List<DayOfWeek> daysOfWeek;

    // MONTHLY_NTH 전용: 몇 번째 주인지 목록이다. 예: [2, 4] (2번째·4번째 주)
    private List<Integer> weekOrdinals;

    // 차단 시작 시각이다. null이면 종일 차단이다.
    private LocalTime startTime;

    // 차단 종료 시각이다. null이면 종일 차단이다.
    private LocalTime endTime;

    // 규칙 적용 시작일이다. null이면 즉시 적용이다.
    private LocalDate validFrom;

    // 규칙 적용 종료일이다. null이면 무기한 적용이다.
    private LocalDate validUntil;

    // null이면 시설 전체에 적용한다. 좌석 ID를 지정하면 해당 좌석만 적용한다.
    private Long seatId;

    // 휴무 사유이다.
    private String reason;
}
