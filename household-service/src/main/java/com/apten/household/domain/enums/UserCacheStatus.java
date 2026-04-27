package com.apten.household.domain.enums;

import com.apten.common.enumcode.AbstractEnumCodeConverter;
import com.apten.common.enumcode.EnumMapperType;
import jakarta.persistence.Converter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

// 사용자 캐시 상태를 code와 value로 분리하는 enum이다.
@Getter
@RequiredArgsConstructor
public enum UserCacheStatus implements EnumMapperType {

    // 승인 대기 상태이다.
    PENDING("01", "대기"),

    // 활성 상태이다.
    ACTIVE("02", "활성"),

    // 반려 상태이다.
    REJECTED("03", "반려"),

    // 삭제 상태이다.
    DELETED("04", "삭제");

    // DB에 저장할 상태 code이다.
    private final String code;

    // API 응답에 보여줄 상태 value이다.
    private final String value;

    // JPA가 DB code와 enum을 자동 변환한다.
    @Converter(autoApply = true)
    public static class CodeConverter extends AbstractEnumCodeConverter<UserCacheStatus> {

        // 사용자 상태는 필수값으로 사용한다.
        public CodeConverter() {
            super(UserCacheStatus.class);
        }
    }
}
