package com.apten.facilityreservation.domain.enums;

import lombok.Getter;

// 정기 휴무 규칙 유형이다.
@Getter
public enum ClosureRuleType {

    // 매주 특정 요일에 적용되는 규칙이다. (예: 매주 월요일)
    WEEKLY("매주"),

    // 매월 N번째 특정 요일에 적용되는 규칙이다. (예: 매월 2번째·4번째 월요일)
    MONTHLY_NTH("매월 N번째");

    // API 응답으로 노출하는 표시값이다.
    private final String label;

    ClosureRuleType(String label) {
        this.label = label;
    }
}
