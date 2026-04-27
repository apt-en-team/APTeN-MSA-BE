package com.apten.facilityreservation.domain.enums;

import com.apten.common.enumcode.AbstractEnumCodeConverter;
import com.apten.common.enumcode.EnumMapperType;
import lombok.Getter;

// 시설 타입 코드를 표현하는 enum이다.
@Getter
public enum FacilityTypeCode implements EnumMapperType {

    // 독서실 타입이다.
    STUDY_ROOM("01", "독서실"),

    // 헬스장 타입이다.
    GYM("02", "헬스장"),

    // 골프연습장 타입이다.
    GOLF("03", "골프연습장"),

    // GX 타입이다.
    GX("04", "GX");

    // DB에 저장하는 코드값이다.
    private final String code;

    // API 응답으로 노출하는 표시값이다.
    private final String value;

    // 코드값과 표시값을 함께 초기화한다.
    FacilityTypeCode(String code, String value) {
        this.code = code;
        this.value = value;
    }

    // JPA가 DB 코드와 enum을 자동 변환한다.
    @jakarta.persistence.Converter(autoApply = true)
    public static class CodeConverter extends AbstractEnumCodeConverter<FacilityTypeCode> {

        // FacilityTypeCode 전용 converter를 만든다.
        public CodeConverter() {
            super(FacilityTypeCode.class);
        }
    }
}
