package com.apten.facilityreservation.domain.enums;

import com.apten.common.enumcode.AbstractEnumCodeConverter;
import com.apten.common.enumcode.EnumMapperType;
import lombok.Getter;

// GX 예약 취소 사유를 표현하는 enum이다.
@Getter
public enum GxReservationCancelReason implements EnumMapperType {

    // 사용자가 취소한 사유이다.
    USER("01", "사용자취소"),

    // 관리자가 취소한 사유이다.
    ADMIN("02", "관리자취소"),

    // 프로그램 취소에 따른 사유이다.
    PROGRAM("03", "프로그램취소");

    // DB에 저장하는 코드값이다.
    private final String code;

    // API 응답으로 노출하는 표시값이다.
    private final String value;

    // 코드값과 표시값을 함께 초기화한다.
    GxReservationCancelReason(String code, String value) {
        this.code = code;
        this.value = value;
    }

    // JPA가 DB 코드와 enum을 자동 변환한다.
    @jakarta.persistence.Converter(autoApply = true)
    public static class CodeConverter extends AbstractEnumCodeConverter<GxReservationCancelReason> {

        // GxReservationCancelReason 전용 converter를 만든다.
        public CodeConverter() {
            super(GxReservationCancelReason.class);
        }
    }
}
