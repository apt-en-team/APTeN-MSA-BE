package com.apten.facilityreservation.domain.enums;

import com.apten.common.enumcode.AbstractEnumCodeConverter;
import com.apten.common.enumcode.EnumMapperType;
import lombok.Getter;

// 시설 이용 구독 상태를 표현하는 enum이다.
@Getter
public enum FacilitySubscriptionStatus implements EnumMapperType {

    // 구독 중인 상태이다.
    ACTIVE("01", "구독중"),

    // 이용 해지 처리된 상태이다.
    CANCELLED("02", "해지");

    // DB에 저장하는 코드값이다.
    private final String code;

    // API 응답으로 노출하는 표시값이다.
    private final String value;

    // 코드값과 표시값을 함께 초기화한다.
    FacilitySubscriptionStatus(String code, String value) {
        this.code = code;
        this.value = value;
    }

    // JPA가 DB 코드와 enum을 자동 변환한다.
    @jakarta.persistence.Converter(autoApply = true)
    public static class CodeConverter extends AbstractEnumCodeConverter<FacilitySubscriptionStatus> {

        // FacilitySubscriptionStatus 전용 converter를 만든다.
        public CodeConverter() {
            super(FacilitySubscriptionStatus.class);
        }
    }
}
