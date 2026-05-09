package com.apten.common.config;

import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;

// Long/long 타입 ID가 프론트에서 Number 정밀도 손실이 나지 않도록 문자열로 응답한다.
@AutoConfiguration
public class JacksonFormatConfiguration {

    // Jackson ObjectMapper에 Long -> String 직렬화 규칙을 전역 등록한다.
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jsonMapperBuilderCustomizer() {
        return builder -> {
            // Long wrapper 타입을 JSON 문자열로 변환한다.
            builder.serializerByType(Long.class, ToStringSerializer.instance);

            // long primitive 타입도 JSON 문자열로 변환한다.
            builder.serializerByType(Long.TYPE, ToStringSerializer.instance);
        };
    }
}
