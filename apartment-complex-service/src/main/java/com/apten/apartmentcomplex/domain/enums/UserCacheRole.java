package com.apten.apartmentcomplex.domain.enums;

import com.apten.common.enumcode.AbstractEnumCodeConverter;
import com.apten.common.enumcode.EnumMapperType;
import jakarta.persistence.Converter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

// DB에는 code를 저장하고 API 응답에는 value를 사용하는 유저 캐시 권한 enum이다.
@Getter
@RequiredArgsConstructor
public enum UserCacheRole implements EnumMapperType {

    // 사용할 수 없는 유저 캐시 권한이다.
    USER("01", "입주민"),

    // 사용할 수 있는 유저 캐시 권한이다.
    ADMIN("02", "관리자");

    // DB에 저장할 유저 캐시 권한 code이다.
    private final String code;

    // FE 응답에 보여줄 유저 캐시 권한 value이다.
    private final String value;

    // JPA가 DB 문자열 code와 UserCacheRole enum을 자동 변환한다.
    @Converter(autoApply = true)
    public static class CodeConverter extends AbstractEnumCodeConverter<UserCacheRole> {

        // 유저 캐시 권한 필드는 필수값이므로 null을 허용하지 않는다.
        public CodeConverter() {
            super(UserCacheRole.class);
        }
    }
}
