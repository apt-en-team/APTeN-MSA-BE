package com.apten.parkingvehicle.domain.enums;

import com.apten.common.enumcode.AbstractEnumCodeConverter;
import com.apten.common.enumcode.EnumMapperType;
import jakarta.persistence.Converter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

// 사용자 캐시 권한을 code와 value로 분리하는 enum이다.
@Getter
@RequiredArgsConstructor
public enum UserCacheRole implements EnumMapperType {

    // 입주민 권한이다.
    USER("01", "입주민"),

    // 관리자 권한이다.
    ADMIN("02", "관리자"),

    // 마스터 권한이다.
    MASTER("03", "마스터");

    // DB에 저장할 권한 code이다.
    private final String code;

    // API 응답에 보여줄 권한 value이다.
    private final String value;

    // JPA가 DB code와 enum을 자동 변환한다.
    @Converter(autoApply = true)
    public static class CodeConverter extends AbstractEnumCodeConverter<UserCacheRole> {

        // 사용자 권한은 필수값으로 사용한다.
        public CodeConverter() {
            super(UserCacheRole.class);
        }
    }
}
