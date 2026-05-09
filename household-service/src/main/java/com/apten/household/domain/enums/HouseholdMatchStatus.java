package com.apten.household.domain.enums;

import com.apten.common.enumcode.AbstractEnumCodeConverter;
import com.apten.common.enumcode.EnumMapperType;
import jakarta.persistence.Converter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

// 세대 매칭 처리 상태를 code와 value로 분리하는 enum이다.
@Getter
@RequiredArgsConstructor
public enum HouseholdMatchStatus implements EnumMapperType {

    // 승인 대기 상태이다.
    PENDING("01", "승인대기"),

    // 승인 완료 상태이다.
    APPROVED("02", "승인완료"),

    // 승인 거절 상태이다.
    REJECTED("03", "승인거절"),

    // 세대 미존재 상태이다.
    NOT_FOUND("04", "세대미존재");

    // DB에 저장할 처리 상태 code이다.
    private final String code;

    // API 응답에 보여줄 처리 상태 value이다.
    private final String value;

    // JPA가 DB code와 enum을 자동 변환한다.
    @Converter(autoApply = true)
    public static class CodeConverter extends AbstractEnumCodeConverter<HouseholdMatchStatus> {

        // 세대 매칭 상태는 필수값으로 사용한다.
        public CodeConverter() {
            super(HouseholdMatchStatus.class);
        }
    }
}
