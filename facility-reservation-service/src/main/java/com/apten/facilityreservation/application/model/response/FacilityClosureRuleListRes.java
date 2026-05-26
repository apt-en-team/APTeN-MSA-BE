package com.apten.facilityreservation.application.model.response;

import com.apten.facilityreservation.domain.enums.ClosureRuleType;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

// 정기 휴무 규칙 목록 응답 DTO이다.
@Getter
@Builder
public class FacilityClosureRuleListRes {

    // 규칙 ID이다.
    private Long closureRuleId;

    // 시설 ID이다.
    private Long facilityId;

    // 좌석 ID이다. null이면 시설 전체 적용이다.
    private Long seatId;

    // 좌석 번호이다.
    private Integer seatNo;

    // 좌석 이름이다.
    private String seatName;

    // 규칙 유형이다. WEEKLY 또는 MONTHLY_NTH
    private ClosureRuleType ruleType;

    // 규칙 유형 표시 라벨이다.
    private String ruleTypeLabel;

    // 적용 요일 목록이다.
    private List<DayOfWeek> daysOfWeek;

    // N번째 주 목록이다. MONTHLY_NTH 전용이다.
    private List<Integer> weekOrdinals;

    // 차단 시작 시각이다. null이면 종일이다.
    private LocalTime startTime;

    // 차단 종료 시각이다. null이면 종일이다.
    private LocalTime endTime;

    // 규칙 적용 시작일이다.
    private LocalDate validFrom;

    // 규칙 적용 종료일이다. null이면 무기한이다.
    private LocalDate validUntil;

    // 휴무 사유이다.
    private String reason;

    // 활성 여부이다.
    private Boolean isActive;
}
