package com.apten.notification.domain.enums;

import com.apten.common.enumcode.AbstractEnumCodeConverter;
import com.apten.common.enumcode.EnumMapperType;
import jakarta.persistence.Converter;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserCacheRole implements EnumMapperType {

    // MASTER는 캐시에 저장될 수 있지만 단지별 알림 수신 대상에서는 제외한다
    USER("01", "입주민"),
    ADMIN("02", "단지 관리자"),
    MANAGER("03", "단지 관리 책임자"),
    MASTER("04", "플랫폼 전체 운영자");

    private final String code;
    private final String value;

    public static UserCacheRole from(String input) {
        // Auth 이벤트 role 값과 DB code 값을 모두 같은 enum으로 변환한다
        return Arrays.stream(values())
                .filter(role -> role.name().equals(input)
                        || role.getCode().equals(input)
                        || role.getValue().equals(input))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown user cache role: " + input));
    }

    @Converter(autoApply = true)
    public static class CodeConverter extends AbstractEnumCodeConverter<UserCacheRole> {
        public CodeConverter() {
            // user_cache.role은 enum name이 아니라 code로 저장한다
            super(UserCacheRole.class);
        }
    }
}
