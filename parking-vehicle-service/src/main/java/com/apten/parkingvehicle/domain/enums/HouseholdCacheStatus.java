package com.apten.parkingvehicle.domain.enums;

import com.apten.common.enumcode.AbstractEnumCodeConverter;
import com.apten.common.enumcode.EnumMapperType;
import jakarta.persistence.Converter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

// 세대 캐시 상태를 code와 value로 분리하는 enum이다.
@Getter
@RequiredArgsConstructor
public enum HouseholdCacheStatus implements EnumMapperType {

    // 입주 상태이다.
    OCCUPIED("01", "입주"),

    // 공실 상태이다.
    VACANT("02", "공실"),

    // 퇴거 상태이다.
    MOVED_OUT("03", "퇴거");

    // DB에 저장할 code이다.
    private final String code;

    // API 응답에 보여줄 value이다.
    private final String value;

    // JPA가 DB code와 enum을 자동 변환한다.
    @Converter(autoApply = true)
    public static class CodeConverter extends AbstractEnumCodeConverter<HouseholdCacheStatus> {

        // 세대 캐시 상태는 필수값으로 사용한다.
        public CodeConverter() {
            super(HouseholdCacheStatus.class);
        }
    }
}
