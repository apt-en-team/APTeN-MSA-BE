package com.apten.auth.domain.enums;

import com.apten.common.enumcode.AbstractEnumCodeConverter;
import com.apten.common.enumcode.EnumMapperType;
import jakarta.persistence.Converter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

// DB에는 code를 저장하고 API 응답에는 value를 사용하는 소셜 계정 연결 상태 enum이다.
@Getter
@RequiredArgsConstructor
public enum SocialLinkStatus implements EnumMapperType {

    // 소셜 계정이 내부 회원과 연결된 상태이다.
    CONNECTED("01", "연결"),

    // 소셜 계정 연결이 해제된 상태이다.
    DISCONNECTED("02", "연결 해제");

    // DB에 저장할 소셜 연결 상태 code이다.
    private final String code;

    // FE 응답에 보여줄 소셜 연결 상태 value이다.
    private final String value;

    // JPA가 DB 문자열 code와 SocialLinkStatus enum을 자동 변환한다.
    @Converter(autoApply = true)
    public static class CodeConverter extends AbstractEnumCodeConverter<SocialLinkStatus> {

        // 소셜 연결 상태 필드는 필수값이므로 null을 허용하지 않는다.
        public CodeConverter() {
            super(SocialLinkStatus.class);
        }
    }
}
