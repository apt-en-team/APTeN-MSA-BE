package com.apten.facilityreservation.domain.enums;

import com.apten.common.enumcode.AbstractEnumCodeConverter;
import com.apten.common.enumcode.EnumMapperType;
import lombok.Getter;

// GX 프로그램 상태를 표현하는 enum이다.
@Getter
public enum GxProgramStatus implements EnumMapperType {

    // 모집 중인 상태이다.
    OPEN("01", "모집중"),

    // 프로그램이 취소된 상태이다.
    CANCELLED("02", "프로그램취소"),

    // 프로그램이 종료된 상태이다.
    CLOSED("03", "종료");

    // DB에 저장하는 코드값이다.
    private final String code;

    // API 응답으로 노출하는 표시값이다.
    private final String value;

    // 코드값과 표시값을 함께 초기화한다.
    GxProgramStatus(String code, String value) {
        this.code = code;
        this.value = value;
    }

    // JPA가 DB 코드와 enum을 자동 변환한다.
    @jakarta.persistence.Converter(autoApply = true)
    public static class CodeConverter extends AbstractEnumCodeConverter<GxProgramStatus> {

        // GxProgramStatus 전용 converter를 만든다.
        public CodeConverter() {
            super(GxProgramStatus.class);
        }
    }
}
