package com.apten.facilityreservation.domain.enums;

import com.apten.common.enumcode.AbstractEnumCodeConverter;
import com.apten.common.enumcode.EnumMapperType;
import lombok.Getter;

// 일반 예약 상태를 표현하는 enum이다.
@Getter
public enum ReservationStatus implements EnumMapperType {

    // 예약이 확정된 상태이다.
    CONFIRMED("01", "예약완료"),

    // 예약이 취소된 상태이다.
    CANCELLED("02", "취소"),

    // 이용이 완료된 상태이다.
    COMPLETED("03", "이용완료");

    // DB에 저장하는 코드값이다.
    private final String code;

    // API 응답으로 노출하는 표시값이다.
    private final String value;

    // 코드값과 표시값을 함께 초기화한다.
    ReservationStatus(String code, String value) {
        this.code = code;
        this.value = value;
    }

    // JPA가 DB 코드와 enum을 자동 변환한다.
    @jakarta.persistence.Converter(autoApply = true)
    public static class CodeConverter extends AbstractEnumCodeConverter<ReservationStatus> {

        // ReservationStatus 전용 converter를 만든다.
        public CodeConverter() {
            super(ReservationStatus.class);
        }
    }
}
