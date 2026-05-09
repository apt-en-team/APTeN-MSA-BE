package com.apten.parkingvehicle.domain.enums;

import com.apten.common.enumcode.AbstractEnumCodeConverter;
import com.apten.common.enumcode.EnumMapperType;
import jakarta.persistence.Converter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

// 차량 종류를 code와 value로 분리하는 enum이다.
@Getter
@RequiredArgsConstructor
public enum VehicleType implements EnumMapperType {

    // 일반 승용차이다.
    CAR("01", "일반 승용차"),

    // SUV 차량이다.
    SUV("02", "SUV"),

    // 전기차이다.
    EV("03", "전기차"),

    // 기타 차량이다.
    ETC("04", "기타");

    // DB에 저장할 차량 종류 code이다.
    private final String code;

    // API 응답에 보여줄 차량 종류 value이다.
    private final String value;

    // JPA가 DB code와 enum을 자동 변환한다.
    @Converter(autoApply = true)
    public static class CodeConverter extends AbstractEnumCodeConverter<VehicleType> {

        // 차량 종류는 필수값으로 사용한다.
        public CodeConverter() {
            super(VehicleType.class);
        }
    }
}
