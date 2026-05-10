package com.apten.common.enumcode;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.module.SimpleDeserializers;
import com.fasterxml.jackson.databind.module.SimpleModule;

// EnumMapperType 구현체에 공통 deserializer를 자동 적용하는 Jackson 모듈이다.
public class EnumMapperTypeModule extends SimpleModule {

    // 모듈 식별 이름과 deserializers 등록을 함께 처리한다.
    public EnumMapperTypeModule() {
        super("EnumMapperTypeModule");
        setDeserializers(new EnumMapperTypeDeserializers());
    }

    // 기본 enum deserializer 대신 EnumMapperType 전용 deserializer를 반환한다.
    private static class EnumMapperTypeDeserializers extends SimpleDeserializers {

        @Override
        @SuppressWarnings("unchecked")
        public JsonDeserializer<?> findEnumDeserializer(
                Class<?> type, DeserializationConfig config, BeanDescription beanDesc) {
            if (EnumMapperType.class.isAssignableFrom(type)) {
                return new EnumMapperTypeDeserializer((Class<? extends Enum<?>>) type);
            }
            return null;
        }
    }
}