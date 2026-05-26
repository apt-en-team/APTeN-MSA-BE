package com.apten.household.domain.enums;

import com.apten.common.enumcode.AbstractEnumCodeConverter;
import com.apten.common.enumcode.EnumMapperType;
import jakarta.persistence.Converter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum HouseholdMatchRejectReason implements EnumMapperType {

    INFO_MISMATCH("01", "정보불일치"),
    HOUSEHOLD_NOT_FOUND("02", "세대미존재"),
    DUPLICATE_REQUEST("03", "중복요청"),
    ADMIN_REJECTED("04", "관리자거절");

    private final String code;
    private final String value;

    @Converter(autoApply = true)
    public static class CodeConverter extends AbstractEnumCodeConverter<HouseholdMatchRejectReason> {

        public CodeConverter() {
            super(HouseholdMatchRejectReason.class, true);
        }
    }
}
