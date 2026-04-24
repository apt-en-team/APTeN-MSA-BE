package com.apten.apartmentcomplex.domain.enums;

import com.apten.common.enumcode.AbstractEnumCodeConverter;
import com.apten.common.enumcode.EnumMapperType;
import jakarta.persistence.Converter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

// DB에는 code를 저장하고 API 응답에는 value를 사용하는 단지 관리자 역할 enum이다.
@Getter
@RequiredArgsConstructor
public enum ComplexAdminRole implements EnumMapperType {

    // 단지 운영 최상위 관리자 역할이다.
    MASTER("01", "마스터"),

    // 단지 운영 관리자 역할이다.
    MANAGER("02", "매니저");

    // DB에 저장할 관리자 역할 code이다.
    private final String code;

    // FE 응답에 보여줄 관리자 역할 value이다.
    private final String value;

    // JPA가 DB 문자열 code와 ComplexAdminRole enum을 자동 변환한다.
    @Converter(autoApply = true)
    public static class CodeConverter extends AbstractEnumCodeConverter<ComplexAdminRole> {

        // 관리자 역할 필드는 필수값이므로 null을 허용하지 않는다.
        public CodeConverter() {
            super(ComplexAdminRole.class);
        }
    }
}
