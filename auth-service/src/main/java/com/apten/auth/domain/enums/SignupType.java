package com.apten.auth.domain.enums;

import com.apten.common.enumcode.AbstractEnumCodeConverter;
import com.apten.common.enumcode.EnumMapperType;
import jakarta.persistence.Converter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

// DB에는 code를 저장하고 API 응답에는 value를 사용하는 가입 방식 enum이다.
@Getter
@RequiredArgsConstructor
public enum SignupType implements EnumMapperType {

    // 이메일과 비밀번호로 가입한 방식이다.
    EMAIL("01", "이메일"),

    // Google 계정으로 가입한 방식이다.
    GOOGLE("02", "구글"),

    // Kakao 계정으로 가입한 방식이다.
    KAKAO("03", "카카오"),

    // Naver 계정으로 가입한 방식이다.
    NAVER("04", "네이버"),

    // MASTER 또는 MANAGER가 직접 생성한 계정 방식이다.
    CREATED_BY_ADMIN("05", "관리자 생성");

    // DB에 저장할 가입 방식 code이다.
    private final String code;

    // FE 응답에 보여줄 가입 방식 value이다.
    private final String value;

    // JPA가 DB 문자열 code와 SignupType enum을 자동 변환한다.
    @Converter(autoApply = true)
    public static class CodeConverter extends AbstractEnumCodeConverter<SignupType> {

        // 가입 방식 필드는 필수값이므로 null을 허용하지 않는다.
        public CodeConverter() {
            super(SignupType.class);
        }
    }
}