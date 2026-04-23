package com.apten.common.kafka;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.converter.RecordMessageConverter;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;

// Kafka consumer가 JSON 문자열 메시지를 필요한 Java 객체로 변환할 수 있게 공통 converter를 등록한다.
@Configuration
@ConditionalOnProperty(prefix = "spring.kafka.consumer", name = "value-deserializer")
public class KafkaConfiguration {

    // @KafkaListener가 문자열 JSON payload를 DTO 파라미터로 받을 수 있도록 converter를 제공한다.
    @Bean
    public RecordMessageConverter recordMessageConverter() {
        return new StringJsonMessageConverter();
    }
}
