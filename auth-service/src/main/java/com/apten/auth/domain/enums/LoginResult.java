package com.apten.auth.domain.enums;

import com.apten.common.enumcode.AbstractEnumCodeConverter;
import com.apten.common.enumcode.EnumMapperType;
import jakarta.persistence.Converter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

// DB에는 code를 저장하고 API 응답에는 value를 사용하는 로그인 결과 enum이다.
@Getter
@RequiredArgsConstructor
public enum LoginResult implements EnumMapperType {

    // 로그인 성공 결과이다.
    SUCCESS("01", "성공"),

    // 로그인 실패 결과이다.
    FAIL("02", "실패");

    // DB에 저장할 로그인 결과 code이다.
    private final String code;

    // FE 응답에 보여줄 로그인 결과 value이다.
    private final String value;

    // JPA가 DB 문자열 code와 LoginResult enum을 자동 변환한다.
    @Converter(autoApply = true)
    public static class CodeConverter extends AbstractEnumCodeConverter<LoginResult> {

        // 로그인 결과 필드는 필수값이므로 null을 허용하지 않는다.
        public CodeConverter() {
            super(LoginResult.class);
        }
    }
}
