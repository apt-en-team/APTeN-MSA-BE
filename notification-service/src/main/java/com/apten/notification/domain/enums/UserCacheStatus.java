package com.apten.notification.domain.enums;

import com.apten.common.enumcode.AbstractEnumCodeConverter;
import com.apten.common.enumcode.EnumMapperType;
import jakarta.persistence.Converter;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserCacheStatus implements EnumMapperType {

    // Auth Service 사용자 상태를 notification-service 캐시에서 필요한 범위로 보관한다
    PENDING("01", "대기"),
    ACTIVE("02", "활성"),
    REJECTED("03", "반려"),
    DELETED("04", "삭제");

    private final String code;
    private final String value;

    public static UserCacheStatus from(String input) {
        // Auth 이벤트 status 값과 DB code 값을 모두 같은 enum으로 변환한다
        return Arrays.stream(values())
                .filter(status -> status.name().equals(input)
                        || status.getCode().equals(input)
                        || status.getValue().equals(input))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown user cache status: " + input));
    }

    @Converter(autoApply = true)
    public static class CodeConverter extends AbstractEnumCodeConverter<UserCacheStatus> {
        public CodeConverter() {
            // user_cache.status는 enum name이 아니라 code로 저장한다
            super(UserCacheStatus.class);
        }
    }
}
