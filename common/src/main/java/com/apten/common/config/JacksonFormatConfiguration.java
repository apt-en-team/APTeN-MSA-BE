package com.apten.common.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
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

            // Instant 직렬화 시 나노초(9자리) 대신 밀리초(3자리)로 제한한다.
            // 나노초 포맷은 Jackson이 파싱하지 못해 Kafka consumer 역직렬화 오류가 발생한다.
            builder.featuresToDisable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS);
            builder.featuresToDisable(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS);
        };
    }
}
