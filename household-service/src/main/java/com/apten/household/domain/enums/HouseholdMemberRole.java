package com.apten.household.domain.enums;

import com.apten.common.enumcode.AbstractEnumCodeConverter;
import com.apten.common.enumcode.EnumMapperType;
import jakarta.persistence.Converter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

// 세대원 역할을 code와 value로 분리하는 enum이다.
@Getter
@RequiredArgsConstructor
public enum HouseholdMemberRole implements EnumMapperType {

    // 세대주 역할이다.
    HEAD("01", "세대주"),

    // 일반 세대원 역할이다.
    MEMBER("02", "세대원");

    // DB에 저장할 역할 code이다.
    private final String code;

    // API 응답에 보여줄 역할 value이다.
    private final String value;

    // JPA가 DB code와 enum을 자동 변환한다.
    @Converter(autoApply = true)
    public static class CodeConverter extends AbstractEnumCodeConverter<HouseholdMemberRole> {

        // 세대원 역할은 필수값으로 사용한다.
        public CodeConverter() {
            super(HouseholdMemberRole.class);
        }
    }
}
