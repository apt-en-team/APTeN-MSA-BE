package com.apten.household.domain.enums;

import com.apten.common.enumcode.AbstractEnumCodeConverter;
import com.apten.common.enumcode.EnumMapperType;
import jakarta.persistence.Converter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

// 시설 이용 스냅샷 상태를 code와 value로 분리하는 enum이다.
@Getter
@RequiredArgsConstructor
public enum FacilityUsageStatus implements EnumMapperType {

    // 이용 완료된 예약 상태이다.
    COMPLETED("01", "이용완료");

    // DB에 저장할 상태 code이다.
    private final String code;

    // API 응답에 보여줄 상태 value이다.
    private final String value;

    // JPA가 DB code와 enum을 자동 변환한다.
    @Converter(autoApply = true)
    public static class CodeConverter extends AbstractEnumCodeConverter<FacilityUsageStatus> {

        // 시설 이용 상태는 필수값으로 사용한다.
        public CodeConverter() {
            super(FacilityUsageStatus.class);
        }
    }
}
