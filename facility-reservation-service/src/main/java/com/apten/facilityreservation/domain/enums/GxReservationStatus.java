package com.apten.facilityreservation.domain.enums;

import com.apten.common.enumcode.AbstractEnumCodeConverter;
import com.apten.common.enumcode.EnumMapperType;
import lombok.Getter;

// GX 예약 상태를 표현하는 enum이다.
@Getter
public enum GxReservationStatus implements EnumMapperType {

    // 대기 상태이다.
    WAITING("01", "대기중"),

    // 승인 완료 상태이다.
    CONFIRMED("02", "승인완료"),

    // 거절 상태이다.
    REJECTED("03", "거절"),

    // 취소 상태이다.
    CANCELLED("04", "취소");

    // DB에 저장하는 코드값이다.
    private final String code;

    // API 응답으로 노출하는 표시값이다.
    private final String value;

    // 코드값과 표시값을 함께 초기화한다.
    GxReservationStatus(String code, String value) {
        this.code = code;
        this.value = value;
    }

    // JPA가 DB 코드와 enum을 자동 변환한다.
    @jakarta.persistence.Converter(autoApply = true)
    public static class CodeConverter extends AbstractEnumCodeConverter<GxReservationStatus> {

        // GxReservationStatus 전용 converter를 만든다.
        public CodeConverter() {
            super(GxReservationStatus.class);
        }
    }
}
