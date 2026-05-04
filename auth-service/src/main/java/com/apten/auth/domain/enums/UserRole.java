package com.apten.auth.domain.enums;

import com.apten.common.enumcode.AbstractEnumCodeConverter;
import com.apten.common.enumcode.EnumMapperType;
import jakarta.persistence.Converter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

// DB에는 code를 저장하고 API 응답에는 value를 사용하는 사용자 권한 enum이다.
// MASTER → MANAGER → ADMIN → USER 순서로 권한 계층이 내려간다.
@Getter
@RequiredArgsConstructor
public enum UserRole implements EnumMapperType {

    // 일반 입주민 사용자 권한
    USER("01", "입주민"),

    // 단지 관리자 사용자 권한
    ADMIN("02", "단지 관리자"),

    // 단지 관리 책임자 권한
    MANAGER("03", "단지 관리 책임자"),

    // 플랫폼 전체 운영자 사용자 권한
    MASTER("04", "플랫폼 전체 운영자");

    // DB에 저장할 사용자 권한
    private final String code;

    // FE 응답에 보여줄 사용자 권한 value이다.
    private final String value;

    // 공통 사용자 컨텍스트용 역할로 변환한다.
    public com.apten.common.security.UserRole toCommonUserRole() {
        return switch (this) {
            case ADMIN -> com.apten.common.security.UserRole.ADMIN;
            case MANAGER -> com.apten.common.security.UserRole.MANAGER;
            case MASTER -> com.apten.common.security.UserRole.MASTER;
            default -> com.apten.common.security.UserRole.USER;
        };
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