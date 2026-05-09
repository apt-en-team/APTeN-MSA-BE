package com.apten.facilityreservation.domain.enums;

import com.apten.common.enumcode.AbstractEnumCodeConverter;
import com.apten.common.enumcode.EnumMapperType;
import lombok.Getter;

// 시설 예약 방식을 표현하는 enum이다.
@Getter
public enum ReservationType implements EnumMapperType {

    // 좌석 단위로 예약하는 방식이다.
    SEAT("01", "좌석형"),

    // 정원만 관리하는 방식이다.
    COUNT("02", "정원형"),

    // 관리자 승인으로 확정하는 방식이다.
    APPROVAL("03", "승인형");

    // DB에 저장하는 코드값이다.
    private final String code;

    // API 응답으로 노출하는 표시값이다.
    private final String value;

    // 코드값과 표시값을 함께 초기화한다.
    ReservationType(String code, String value) {
        this.code = code;
        this.value = value;
    }

    // JPA가 DB 코드와 enum을 자동 변환한다.
    @jakarta.persistence.Converter(autoApply = true)
    public static class CodeConverter extends AbstractEnumCodeConverter<ReservationType> {

        // ReservationType 전용 converter를 만든다.
        public CodeConverter() {
            super(ReservationType.class);
        }
    }
}
