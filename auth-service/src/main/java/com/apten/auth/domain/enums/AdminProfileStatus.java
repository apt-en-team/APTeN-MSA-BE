package com.apten.auth.domain.enums;

import com.apten.common.enumcode.AbstractEnumCodeConverter;
import com.apten.common.enumcode.EnumMapperType;
import jakarta.persistence.Converter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

//추가 관리자 프로필 상태를 code/value로 관리하는 enum
@Getter
@RequiredArgsConstructor
public enum AdminProfileStatus implements EnumMapperType {

    ACTIVE("01", "활성"),
    INACTIVE("02", "비활성"),
    DELETED("03", "삭제");

    private final String code;
    private final String value;

    @Converter(autoApply = true)
    public static class CodeConverter extends AbstractEnumCodeConverter<AdminProfileStatus> {

        public CodeConverter() {
            super(AdminProfileStatus.class);
        }
    }
}
