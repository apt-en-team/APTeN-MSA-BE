package com.apten.common.enums;

import com.apten.common.enumcode.AbstractEnumCodeConverter;
import com.apten.common.enumcode.EnumMapperType;
import jakarta.persistence.Converter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

// 단지별 기능 사용 여부를 구분하는 공통 기능 코드이다.
@Getter
@RequiredArgsConstructor
public enum FeatureCode implements EnumMapperType {

    // 시설, 예약, GX 기능 묶음이다.
    FACILITY("FACILITY", "시설/예약"),

    // 주차 현황과 입출차 기록 기능 묶음이다.
    PARKING_STATUS("PARKING_STATUS", "주차 현황"),

    // 전자투표 기능이다.
    VOTE("VOTE", "전자투표");

    private final String code;
    private final String value;

    // 기능 코드는 DB에 문자열 code로 저장한다.
    @Converter(autoApply = true)
    public static class CodeConverter extends AbstractEnumCodeConverter<FeatureCode> {

        public CodeConverter() {
            super(FeatureCode.class);
        }
    }
}
