package com.apten.auth.domain.enums;

import com.apten.common.enumcode.AbstractEnumCodeConverter;
import com.apten.common.enumcode.EnumMapperType;
import jakarta.persistence.Converter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

// DB에는 code를 저장하고 API 응답에는 value를 사용하는 사용자 권한 enum이다.
@Getter
@RequiredArgsConstructor
public enum UserRole implements EnumMapperType {

    // 일반 입주민 사용자 권한이다.
    USER("01", "입주민"),

    // 관리자 사용자 권한이다.
    ADMIN("02", "관리자");

    // DB에 저장할 사용자 권한 code이다.
    private final String code;

    // FE 응답에 보여줄 사용자 권한 value이다.
    private final String value;

    // 공통 사용자 컨텍스트용 역할로 변환한다.
    public com.apten.common.security.UserRole toCommonUserRole() {
        return this == ADMIN ? com.apten.common.security.UserRole.ADMIN : com.apten.common.security.UserRole.RESIDENT;
    }

    // JPA가 DB 문자열 code와 UserRole enum을 자동 변환한다.
    @Converter(autoApply = true)
    public static class CodeConverter extends AbstractEnumCodeConverter<UserRole> {

        // 사용자 권한 필드는 필수값이므로 null을 허용하지 않는다.
        public CodeConverter() {
            super(UserRole.class);
        }
    }
}
