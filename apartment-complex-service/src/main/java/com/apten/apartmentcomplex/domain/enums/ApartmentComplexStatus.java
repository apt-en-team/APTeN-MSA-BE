package com.apten.apartmentcomplex.domain.enums;

import com.apten.common.enumcode.AbstractEnumCodeConverter;
import com.apten.common.enumcode.EnumMapperType;
import jakarta.persistence.Converter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

// DB에는 code를 저장하고 API 응답에는 value를 사용하는 단지 상태 enum이다.
@Getter
@RequiredArgsConstructor
public enum ApartmentComplexStatus implements EnumMapperType {

    // 운영 중인 단지 상태이다.
    ACTIVE("01", "활성"),

    // 운영을 멈춘 단지 상태이다.
    INACTIVE("02", "비활성"),

    // 삭제 처리된 단지 상태이다.
    DELETED("03", "삭제");

    // DB에 저장할 단지 상태 code이다.
    private final String code;

    // FE 응답에 보여줄 단지 상태 value이다.
    private final String value;

    // JPA가 DB 문자열 code와 ApartmentComplexStatus enum을 자동 변환한다.
    @Converter(autoApply = true)
    public static class CodeConverter extends AbstractEnumCodeConverter<ApartmentComplexStatus> {

        // 단지 상태 필드는 필수값이므로 null을 허용하지 않는다.
        public CodeConverter() {
            super(ApartmentComplexStatus.class);
        }
    }
}
