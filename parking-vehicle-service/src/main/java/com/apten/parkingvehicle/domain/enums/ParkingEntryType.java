package com.apten.parkingvehicle.domain.enums;

import com.apten.common.enumcode.AbstractEnumCodeConverter;
import com.apten.common.enumcode.EnumMapperType;
import jakarta.persistence.Converter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

// 주차 입출차 구분을 code와 value로 분리하는 enum이다.
@Getter
@RequiredArgsConstructor
public enum ParkingEntryType implements EnumMapperType {

    // 입차 상태이다.
    IN("01", "입차"),

    // 출차 상태이다.
    OUT("02", "출차");

    // DB에 저장할 code이다.
    private final String code;

    // API 응답에 보여줄 value이다.
    private final String value;

    // JPA가 DB code와 enum을 자동 변환한다.
    @Converter(autoApply = true)
    public static class CodeConverter extends AbstractEnumCodeConverter<ParkingEntryType> {

        // 엔티티 컬럼은 NOT NULL이지만 동적 쿼리 파라미터 바인딩에서 null이 들어올 수 있어 허용한다.
        public CodeConverter() {
            super(ParkingEntryType.class, true);
        }
    }
}
