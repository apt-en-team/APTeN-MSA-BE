package com.apten.facilityreservation.domain.enums;

import com.apten.common.enumcode.AbstractEnumCodeConverter;
import com.apten.common.enumcode.EnumMapperType;
import lombok.Getter;

// 좌석 임시 선점 상태를 표현하는 enum이다.
@Getter
public enum ReservationHoldStatus implements EnumMapperType {

    // 임시 선점이 유지 중인 상태이다.
    HOLDING("01", "임시선점중"),

    // 임시 선점 시간이 만료된 상태이다.
    EXPIRED("02", "시간만료"),

    // 선점이 실제 예약으로 확정된 상태이다.
    CONFIRMED("03", "예약확정완료"),

    // 사용자가 선점을 취소한 상태이다.
    CANCELLED("04", "선점취소");

    // DB에 저장하는 코드값이다.
    private final String code;

    // API 응답으로 노출하는 표시값이다.
    private final String value;

    // 코드값과 표시값을 함께 초기화한다.
    ReservationHoldStatus(String code, String value) {
        this.code = code;
        this.value = value;
    }

    // JPA가 DB 코드와 enum을 자동 변환한다.
    @jakarta.persistence.Converter(autoApply = true)
    public static class CodeConverter extends AbstractEnumCodeConverter<ReservationHoldStatus> {

        // ReservationHoldStatus 전용 converter를 만든다.
        public CodeConverter() {
            super(ReservationHoldStatus.class);
        }
    }
}
