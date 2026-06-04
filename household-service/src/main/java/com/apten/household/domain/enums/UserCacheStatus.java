package com.apten.household.domain.enums;

import com.apten.common.enumcode.AbstractEnumCodeConverter;
import com.apten.common.enumcode.EnumMapperType;
import jakarta.persistence.Converter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

// 사용자 캐시 상태를 code와 value로 분리하는 enum이다.
@Getter
@RequiredArgsConstructor
public enum UserCacheStatus implements EnumMapperType {

    PENDING("01", "대기"),
    ACTIVE("02", "활성"),
    REJECTED("03", "반려"),
    DELETED("04", "삭제"),
    LOCKED("05", "잠금");

    private final String code;
    private final String value;

    @Converter(autoApply = true)
    public static class CodeConverter extends AbstractEnumCodeConverter<UserCacheStatus> {
        public CodeConverter() {
            super(UserCacheStatus.class);
        }
    }
}
