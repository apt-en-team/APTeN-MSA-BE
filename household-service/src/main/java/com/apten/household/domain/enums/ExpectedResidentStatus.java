package com.apten.household.domain.enums;

import com.apten.common.enumcode.AbstractEnumCodeConverter;
import com.apten.common.enumcode.EnumMapperType;
import jakarta.persistence.Converter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExpectedResidentStatus implements EnumMapperType {

    AVAILABLE("AVAILABLE", "사용가능"),
    MATCHED("MATCHED", "매칭완료"),
    DISABLED("DISABLED", "비활성");

    private final String code;
    private final String value;

    @Converter(autoApply = true)
    public static class CodeConverter extends AbstractEnumCodeConverter<ExpectedResidentStatus> {

        public CodeConverter() {
            super(ExpectedResidentStatus.class);
        }
    }
}
