package com.apten.common.enumcode;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.util.Arrays;

// EnumMapperType을 구현한 enum을 name / code / value 어느 형식으로든 역직렬화하는 공통 deserializer이다.
public class EnumMapperTypeDeserializer extends JsonDeserializer<Object> {

    // 역직렬화 대상이 될 enum 클래스이다.
    private final Class<? extends Enum<?>> enumClass;

    // 대상 enum 클래스를 받아 deserializer를 만든다.
    public EnumMapperTypeDeserializer(Class<? extends Enum<?>> enumClass) {
        this.enumClass = enumClass;
    }

    // 입력 문자열을 enum 상수명 / code / value 순으로 매칭해 enum 인스턴스를 반환한다.
    @Override
    public Object deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        String input = parser.getValueAsString();
        if (input == null || input.isBlank()) {
            return null;
        }

        return Arrays.stream(enumClass.getEnumConstants())
                .filter(constant -> matches(constant, input))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "일치하는 " + enumClass.getSimpleName() + " 값이 없습니다. input=" + input));
    }

    // enum 상수와 입력 문자열을 name / code / value 순으로 비교한다.
    private boolean matches(Enum<?> constant, String input) {
        if (constant.name().equalsIgnoreCase(input)) {
            return true;
        }
        EnumMapperType mapper = (EnumMapperType) constant;
        return mapper.getCode().equalsIgnoreCase(input) || mapper.getValue().equals(input);
    }
}