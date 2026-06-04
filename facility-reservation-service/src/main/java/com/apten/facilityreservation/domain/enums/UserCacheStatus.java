package com.apten.facilityreservation.domain.enums;

import com.apten.common.enumcode.AbstractEnumCodeConverter;
import com.apten.common.enumcode.EnumMapperType;
import lombok.Getter;

// 사용자 캐시의 상태값을 표현하는 enum이다.
@Getter
public enum UserCacheStatus implements EnumMapperType {

    PENDING("01", "승인대기"),
    ACTIVE("02", "활성"),
    REJECTED("03", "승인거절"),
    DELETED("04", "탈퇴"),
    LOCKED("05", "잠금");

    private final String code;
    private final String value;

    UserCacheStatus(String code, String value) {
        this.code = code;
        this.value = value;
    }

    @jakarta.persistence.Converter(autoApply = true)
    public static class CodeConverter extends AbstractEnumCodeConverter<UserCacheStatus> {
        public CodeConverter() {
            super(UserCacheStatus.class);
        }
    }
}
