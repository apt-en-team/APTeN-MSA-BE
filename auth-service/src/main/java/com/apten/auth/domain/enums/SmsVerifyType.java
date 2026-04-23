package com.apten.auth.domain.enums;

import com.apten.common.enumcode.AbstractEnumCodeConverter;
import com.apten.common.enumcode.EnumMapperType;
import jakarta.persistence.Converter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

// DB에는 code를 저장하고 API 응답에는 value를 사용하는 SMS 인증 목적 enum이다.
@Getter
@RequiredArgsConstructor
public enum SmsVerifyType implements EnumMapperType {

    // 회원가입 휴대폰 인증에 사용하는 타입이다.
    SIGNUP("01", "회원가입"),

    // 비밀번호 재설정 휴대폰 인증에 사용하는 타입이다.
    RESET_PASSWORD("02", "비밀번호 재설정");

    // DB에 저장할 SMS 인증 목적 code이다.
    private final String code;

    // FE 응답에 보여줄 SMS 인증 목적 value이다.
    private final String value;

    // JPA가 DB 문자열 code와 SmsVerifyType enum을 자동 변환한다.
    @Converter(autoApply = true)
    public static class CodeConverter extends AbstractEnumCodeConverter<SmsVerifyType> {

        // SMS 인증 목적 필드는 필수값이므로 null을 허용하지 않는다.
        public CodeConverter() {
            super(SmsVerifyType.class);
        }
    }
}
