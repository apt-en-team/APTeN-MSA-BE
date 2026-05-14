package com.apten.parkingvehicle.domain.enums;

import com.apten.common.enumcode.AbstractEnumCodeConverter;
import com.apten.common.enumcode.EnumMapperType;
import jakarta.persistence.Converter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

// 단지 주차 운영 타입을 code와 value로 분리하는 enum이다.
@Getter
@RequiredArgsConstructor
public enum ParkingType implements EnumMapperType {

    // 주차 시스템 미사용 상태이다.
    NONE("01", "미사용"),

    // 주차장 단위 총 면수만 관리하는 기본 운영 상태이다.
    BASIC("02", "기본"),

    // 자리별 센서로 실시간 점유 상태를 관리하는 운영 상태이다.
    SENSOR("03", "센서");

    // DB에 저장할 운영 타입 code이다.
    private final String code;

    // API 응답에 보여줄 운영 타입 value이다.
    private final String value;

    // JPA가 DB code와 enum을 자동 변환한다.
    @Converter(autoApply = true)
    public static class CodeConverter extends AbstractEnumCodeConverter<ParkingType> {

        // 주차 운영 타입은 필수값으로 사용한다.
        public CodeConverter() {
            super(ParkingType.class);
        }
    }
}