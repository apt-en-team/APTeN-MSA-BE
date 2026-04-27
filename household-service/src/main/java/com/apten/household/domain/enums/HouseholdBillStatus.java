package com.apten.household.domain.enums;

import com.apten.common.enumcode.AbstractEnumCodeConverter;
import com.apten.common.enumcode.EnumMapperType;
import jakarta.persistence.Converter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

// 세대 월 청구 상태를 code와 value로 분리하는 enum이다.
@Getter
@RequiredArgsConstructor
public enum HouseholdBillStatus implements EnumMapperType {

    // 임시 계산 상태이다.
    DRAFT("01", "임시계산"),

    // 확정 완료 상태이다.
    CONFIRMED("02", "확정완료");

    // DB에 저장할 상태 code이다.
    private final String code;

    // API 응답에 보여줄 상태 value이다.
    private final String value;

    // JPA가 DB code와 enum을 자동 변환한다.
    @Converter(autoApply = true)
    public static class CodeConverter extends AbstractEnumCodeConverter<HouseholdBillStatus> {

        // 청구 상태는 필수값으로 사용한다.
        public CodeConverter() {
            super(HouseholdBillStatus.class);
        }
    }
}
