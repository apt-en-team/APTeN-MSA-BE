package com.apten.facilityreservation.domain.enums;

import com.apten.common.enumcode.AbstractEnumCodeConverter;
import com.apten.common.enumcode.EnumMapperType;
import lombok.Getter;

// 사용자 캐시의 상태값을 표현하는 enum이다.
@Getter
public enum UserCacheStatus implements EnumMapperType {

    // 승인 대기 상태이다.
    PENDING("01", "승인대기"),

    // 활성 사용자 상태이다.
    ACTIVE("02", "활성"),

    // 승인 거절 상태이다.
    REJECTED("03", "승인거절"),

    // 탈퇴 또는 삭제 상태이다.
    DELETED("04", "탈퇴");

    // DB에 저장하는 코드값이다.
    private final String code;

    // API 응답으로 노출하는 표시값이다.
    private final String value;

    // 코드값과 표시값을 함께 초기화한다.
    UserCacheStatus(String code, String value) {
        this.code = code;
        this.value = value;
    }

    // JPA가 DB 코드와 enum을 자동 변환한다.
    @jakarta.persistence.Converter(autoApply = true)
    public static class CodeConverter extends AbstractEnumCodeConverter<UserCacheStatus> {

        // UserCacheStatus 전용 converter를 만든다.
        public CodeConverter() {
            super(UserCacheStatus.class);
        }
    }
}
