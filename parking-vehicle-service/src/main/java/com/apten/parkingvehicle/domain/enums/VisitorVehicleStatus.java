package com.apten.parkingvehicle.domain.enums;

import com.apten.common.enumcode.AbstractEnumCodeConverter;
import com.apten.common.enumcode.EnumMapperType;
import jakarta.persistence.Converter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

// 방문차량 처리 상태를 code와 value로 분리하는 enum이다.
@Getter
@RequiredArgsConstructor
public enum VisitorVehicleStatus implements EnumMapperType {

    // 등록 완료 상태이다.
    APPROVED("01", "등록완료"),

    // 사용자 취소 상태이다.
    CANCELLED("02", "사용자취소"),

    // 자동 만료 상태이다.
    EXPIRED("03", "자동만료"),

    // 삭제 상태이다.
    DELETED("04", "삭제");

    // DB에 저장할 상태 code이다.
    private final String code;

    // API 응답에 보여줄 상태 value이다.
    private final String value;

    // JPA가 DB code와 enum을 자동 변환한다.
    @Converter(autoApply = true)
    public static class CodeConverter extends AbstractEnumCodeConverter<VisitorVehicleStatus> {

        // 방문차량 상태는 필수값으로 사용한다.
        public CodeConverter() {
            super(VisitorVehicleStatus.class);
        }
    }
}
