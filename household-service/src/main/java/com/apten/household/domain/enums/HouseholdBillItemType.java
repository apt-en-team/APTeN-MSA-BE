package com.apten.household.domain.enums;

import com.apten.common.enumcode.AbstractEnumCodeConverter;
import com.apten.common.enumcode.EnumMapperType;
import jakarta.persistence.Converter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

// 세대 청구 상세 항목 유형을 code와 value로 분리하는 enum이다.
@Getter
@RequiredArgsConstructor
public enum HouseholdBillItemType implements EnumMapperType {

    // 기본 관리비 항목이다.
    BASE_FEE("01", "기본관리비"),

    // 차량 비용 항목이다.
    VEHICLE_FEE("02", "차량비용"),

    // 시설 이용 비용 항목이다.
    FACILITY_FEE("03", "시설이용비용"),

    // 방문차량 비용 항목이다.
    VISITOR_FEE("04", "방문차량비용");

    // DB에 저장할 항목 유형 code이다.
    private final String code;

    // API 응답에 보여줄 항목 유형 value이다.
    private final String value;

    // JPA가 DB code와 enum을 자동 변환한다.
    @Converter(autoApply = true)
    public static class CodeConverter extends AbstractEnumCodeConverter<HouseholdBillItemType> {

        // 청구 항목 유형은 필수값으로 사용한다.
        public CodeConverter() {
            super(HouseholdBillItemType.class);
        }
    }
}
