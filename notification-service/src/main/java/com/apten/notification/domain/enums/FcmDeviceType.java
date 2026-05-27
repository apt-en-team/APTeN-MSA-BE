package com.apten.notification.domain.enums;

import com.apten.common.enumcode.AbstractEnumCodeConverter;
import com.apten.common.enumcode.EnumMapperType;
import jakarta.persistence.Converter;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FcmDeviceType implements EnumMapperType {

    // 코드값은 DB 저장용, value는 화면/문서 설명용으로 사용한다
    WEB("01", "웹 브라우저"),
    MOBILE("02", "모바일 기기");

    private final String code;
    private final String value;

    public static FcmDeviceType from(String input) {
        // API에서는 enum name, code, value 중 어떤 값이 와도 같은 타입으로 해석한다
        if (input == null || input.isBlank()) {
            return null;
        }
        return Arrays.stream(values())
                .filter(type -> type.name().equals(input)
                        || type.getCode().equals(input)
                        || type.getValue().equals(input))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown FCM device type: " + input));
    }

    @Converter(autoApply = true)
    public static class CodeConverter extends AbstractEnumCodeConverter<FcmDeviceType> {
        public CodeConverter() {
            // nullable 컬럼이므로 null deviceType을 허용한다
            super(FcmDeviceType.class, true);
        }
    }
}
