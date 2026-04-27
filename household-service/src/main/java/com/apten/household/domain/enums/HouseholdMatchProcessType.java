package com.apten.household.domain.enums;

import com.apten.common.enumcode.AbstractEnumCodeConverter;
import com.apten.common.enumcode.EnumMapperType;
import jakarta.persistence.Converter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

// 세대 매칭 처리 방식을 code와 value로 분리하는 enum이다.
@Getter
@RequiredArgsConstructor
public enum HouseholdMatchProcessType implements EnumMapperType {

    // 자동 승인 처리 방식이다.
    AUTO("01", "자동"),

    // 수동 승인 처리 방식이다.
    MANUAL("02", "수동");

    // DB에 저장할 처리 방식 code이다.
    private final String code;

    // API 응답에 보여줄 처리 방식 value이다.
    private final String value;

    // JPA가 DB code와 enum을 자동 변환한다.
    @Converter(autoApply = true)
    public static class CodeConverter extends AbstractEnumCodeConverter<HouseholdMatchProcessType> {

        // 세대 매칭 처리 방식은 필수값으로 사용한다.
        public CodeConverter() {
            super(HouseholdMatchProcessType.class);
        }
    }
}
