package com.apten.household.domain.enums;

import com.apten.common.enumcode.AbstractEnumCodeConverter;
import com.apten.common.enumcode.EnumMapperType;
import jakarta.persistence.Converter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

// 단지 캐시 상태를 code와 value로 분리하는 enum이다.
@Getter
@RequiredArgsConstructor
public enum ComplexCacheStatus implements EnumMapperType {

    // 운영 중인 단지 상태이다.
    ACTIVE("01", "활성"),

    // 운영이 중단된 단지 상태이다.
    INACTIVE("02", "비활성");

    // DB에 저장할 상태 code이다.
    private final String code;

    // API 응답에 보여줄 상태 value이다.
    private final String value;

    // JPA가 DB code와 enum을 자동 변환한다.
    @Converter(autoApply = true)
    public static class CodeConverter extends AbstractEnumCodeConverter<ComplexCacheStatus> {

        // 단지 캐시 상태는 필수값으로 사용한다.
        public CodeConverter() {
            super(ComplexCacheStatus.class);
        }
    }
}
