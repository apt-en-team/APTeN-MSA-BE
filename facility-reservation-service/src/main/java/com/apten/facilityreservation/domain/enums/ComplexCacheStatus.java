package com.apten.facilityreservation.domain.enums;

import com.apten.common.enumcode.AbstractEnumCodeConverter;
import com.apten.common.enumcode.EnumMapperType;
import lombok.Getter;

// 단지 캐시 운영 상태를 표현하는 enum이다.
@Getter
public enum ComplexCacheStatus implements EnumMapperType {

    // 운영 중인 단지 상태이다.
    ACTIVE("01", "운영중"),

    // 비활성 처리된 단지 상태이다.
    INACTIVE("02", "비활성");

    // DB에 저장하는 코드값이다.
    private final String code;

    // API 응답으로 노출하는 표시값이다.
    private final String value;

    // 코드값과 표시값을 함께 초기화한다.
    ComplexCacheStatus(String code, String value) {
        this.code = code;
        this.value = value;
    }

    // JPA가 DB 코드와 enum을 자동 변환한다.
    @jakarta.persistence.Converter(autoApply = true)
    public static class CodeConverter extends AbstractEnumCodeConverter<ComplexCacheStatus> {

        // ComplexCacheStatus 전용 converter를 만든다.
        public CodeConverter() {
            super(ComplexCacheStatus.class);
        }
    }
}
