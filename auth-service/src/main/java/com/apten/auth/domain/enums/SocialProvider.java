package com.apten.auth.domain.enums;

import com.apten.common.enumcode.AbstractEnumCodeConverter;
import com.apten.common.enumcode.EnumMapperType;
import jakarta.persistence.Converter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

// DB에는 code를 저장하고 API 응답에는 value를 사용하는 소셜 제공자 enum이다.
@Getter
@RequiredArgsConstructor
public enum SocialProvider implements EnumMapperType {

    // Google OAuth2 제공자이다.
    GOOGLE("01", "구글"),

    // Kakao OAuth2 제공자이다.
    KAKAO("02", "카카오"),

    // Naver OAuth2 제공자이다.
    NAVER("03", "네이버");

    // DB에 저장할 소셜 제공자 code이다.
    private final String code;

    // FE 응답에 보여줄 소셜 제공자 value이다.
    private final String value;

    // JPA가 DB 문자열 code와 SocialProvider enum을 자동 변환한다.
    @Converter(autoApply = true)
    public static class CodeConverter extends AbstractEnumCodeConverter<SocialProvider> {

        // 소셜 제공자 필드는 필수값이므로 null을 허용하지 않는다.
        public CodeConverter() {
            super(SocialProvider.class);
        }
    }
}
