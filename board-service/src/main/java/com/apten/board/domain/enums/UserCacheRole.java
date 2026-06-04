package com.apten.board.domain.enums;

import com.apten.common.enumcode.AbstractEnumCodeConverter;
import com.apten.common.enumcode.EnumMapperType;
import lombok.Getter;

// 게시판 서비스의 사용자 권한 캐시 enum이다.
@Getter
public enum UserCacheRole implements EnumMapperType {

    USER("01", "입주민"),
    ADMIN("02", "관리자"),
    MANAGER("03", "매니저");

    private final String code;
    private final String value;

    UserCacheRole(String code, String value) {
        this.code = code;
        this.value = value;
    }

    @jakarta.persistence.Converter(autoApply = true)
    public static class CodeConverter extends AbstractEnumCodeConverter<UserCacheRole> {
        public CodeConverter() {
            super(UserCacheRole.class);
        }
    }
}
