package com.apten.household.domain.enums;

import com.apten.common.enumcode.AbstractEnumCodeConverter;
import com.apten.common.enumcode.EnumMapperType;
import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.persistence.Converter;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

// 세대 상태를 code와 value로 분리하는 enum이다.
@Getter
@RequiredArgsConstructor
public enum HouseholdStatus implements EnumMapperType {

    // 입주 완료 상태이다.
    OCCUPIED("01", "입주"),

    // 공실 상태이다.
    VACANT("02", "공실"),

    // 퇴거 완료 상태이다.
    MOVED_OUT("03", "퇴거");

    // DB에 저장할 상태 code이다.
    private final String code;

    // API 응답에 보여줄 상태 value이다.
    private final String value;

    @JsonCreator
    public static HouseholdStatus from(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return Arrays.stream(values())
                .filter(status -> status.name().equals(value)
                        || status.code.equals(value)
                        || status.value.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("일치하는 세대 상태가 없습니다. value=" + value));
    }

    // JPA가 DB code와 enum을 자동 변환한다.
    @Converter(autoApply = true)
    public static class CodeConverter extends AbstractEnumCodeConverter<HouseholdStatus> {

        // 세대 상태는 필수값으로 사용한다.
        public CodeConverter() {
            super(HouseholdStatus.class);
        }
    }
}
