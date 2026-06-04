package com.apten.household.domain.enums;

import com.apten.common.enumcode.AbstractEnumCodeConverter;
import com.apten.common.enumcode.EnumMapperType;
import jakarta.persistence.Converter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

// 사용자 캐시 권한을 code와 value로 분리하는 enum이다.
@Getter
@RequiredArgsConstructor
public enum UserCacheRole implements EnumMapperType {

    USER("01", "입주민"),
    ADMIN("02", "관리자"),
    MANAGER("03", "매니저");

    private final String code;
    private final String value;

    @Converter(autoApply = true)
    public static class CodeConverter extends AbstractEnumCodeConverter<UserCacheRole> {
        public CodeConverter() {
            super(UserCacheRole.class);
        }
    }
}
