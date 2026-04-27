package com.apten.board.domain.enums;

import com.apten.common.enumcode.AbstractEnumCodeConverter;
import com.apten.common.enumcode.EnumMapperType;
import lombok.Getter;

// 세대원 캐시 역할 enum이다.
@Getter
public enum HouseholdMemberRole implements EnumMapperType {

    // 세대주 역할이다.
    HEAD("01", "세대주"),

    // 일반 세대원 역할이다.
    MEMBER("02", "세대원");

    // DB에 저장하는 코드값이다.
    private final String code;

    // API 응답에 노출하는 표시값이다.
    private final String value;

    // 코드와 표시값을 함께 초기화한다.
    HouseholdMemberRole(String code, String value) {
        this.code = code;
        this.value = value;
    }

    // JPA가 DB 코드와 enum을 자동 변환한다.
    @jakarta.persistence.Converter(autoApply = true)
    public static class CodeConverter extends AbstractEnumCodeConverter<HouseholdMemberRole> {

        // HouseholdMemberRole 전용 converter이다.
        public CodeConverter() {
            super(HouseholdMemberRole.class);
        }
    }
}
