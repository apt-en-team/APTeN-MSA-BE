package com.apten.board.domain.enums;

import com.apten.common.enumcode.AbstractEnumCodeConverter;
import com.apten.common.enumcode.EnumMapperType;
import lombok.Getter;

// 게시판 서비스의 사용자 권한 캐시 enum이다.
@Getter
public enum UserCacheRole implements EnumMapperType {

    // 입주민 권한이다.
    USER("01", "입주민"),

    // 관리자 권한이다.
    ADMIN("02", "관리자"),

    // 마스터 권한이다.
    MASTER("03", "마스터");

    // DB에 저장하는 코드값이다.
    private final String code;

    // API 응답에 노출하는 표시값이다.
    private final String value;

    // 코드와 표시값을 함께 초기화한다.
    UserCacheRole(String code, String value) {
        this.code = code;
        this.value = value;
    }

    // JPA가 DB 코드와 enum을 자동 변환한다.
    @jakarta.persistence.Converter(autoApply = true)
    public static class CodeConverter extends AbstractEnumCodeConverter<UserCacheRole> {

        // UserCacheRole 전용 converter이다.
        public CodeConverter() {
            super(UserCacheRole.class);
        }
    }
}
