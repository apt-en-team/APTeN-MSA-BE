package com.apten.auth.domain.enums;

import com.apten.common.enumcode.AbstractEnumCodeConverter;
import com.apten.common.enumcode.EnumMapperType;
import jakarta.persistence.Converter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

// DB에는 code를 저장하고 API 응답에는 value를 사용하는 회원 상태 enum이다.
@Getter
@RequiredArgsConstructor
public enum UserStatus implements EnumMapperType {

    // 가입 후 세대 승인 처리를 기다리는 상태이다.
    PENDING("01", "대기"),

    // 정상적으로 서비스를 사용할 수 있는 상태이다.
    ACTIVE("02", "활성"),

    // 가입 또는 승인 요청이 반려된 상태이다.
    REJECTED("03", "반려"),

    // 회원 탈퇴 등으로 삭제 처리된 상태이다.
    DELETED("04", "삭제");

    // DB에 저장할 회원 상태 code이다.
    private final String code;

    // FE 응답에 보여줄 회원 상태 value이다.
    private final String value;

    // JPA가 DB 문자열 code와 UserStatus enum을 자동 변환한다.
    @Converter(autoApply = true)
    public static class CodeConverter extends AbstractEnumCodeConverter<UserStatus> {

        // 회원 상태 필드는 필수값이므로 null을 허용하지 않는다.
        public CodeConverter() {
            super(UserStatus.class);
        }
    }
}
