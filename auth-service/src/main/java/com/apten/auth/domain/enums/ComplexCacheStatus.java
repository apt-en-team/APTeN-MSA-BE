package com.apten.auth.domain.enums;

import com.apten.common.enumcode.AbstractEnumCodeConverter;
import com.apten.common.enumcode.EnumMapperType;
import jakarta.persistence.Converter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

// DB에는 code를 저장하고 API 응답에는 value를 사용하는 단지 캐시 상태 enum이다.
@Getter
@RequiredArgsConstructor
public enum ComplexCacheStatus implements EnumMapperType {

    // 사용할 수 있는 단지 캐시 상태이다.
    ACTIVE("01", "활성"),

    // 사용할 수 없는 단지 캐시 상태이다.
    INACTIVE("02", "비활성");

    // DB에 저장할 단지 캐시 상태 code이다.
    private final String code;

    // FE 응답에 보여줄 단지 캐시 상태 value이다.
    private final String value;

    // JPA가 DB 문자열 code와 ComplexCacheStatus enum을 자동 변환한다.
    @Converter(autoApply = true)
    public static class CodeConverter extends AbstractEnumCodeConverter<ComplexCacheStatus> {

        // 단지 캐시 상태 필드는 필수값이므로 null을 허용하지 않는다.
        public CodeConverter() {
            super(ComplexCacheStatus.class);
        }
    }
}
