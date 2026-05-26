package com.apten.household.domain.enums;

import com.apten.common.enumcode.AbstractEnumCodeConverter;
import com.apten.common.enumcode.EnumMapperType;
import jakarta.persistence.Converter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExpectedResidentStatus implements EnumMapperType {

    AVAILABLE("01", "사용가능"),
    MATCHED("02", "매칭완료"),
    DISABLED("03", "비활성");

    private final String code;
    private final String value;

    @Converter(autoApply = true)
    public static class CodeConverter extends AbstractEnumCodeConverter<ExpectedResidentStatus> {

        public CodeConverter() {
            super(ExpectedResidentStatus.class);
        }
    }
}
