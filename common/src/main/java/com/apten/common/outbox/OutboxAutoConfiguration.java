package com.apten.common.outbox;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

// Kafka producer 설정이 있는 서비스에서만 outbox 스캔과 relay 스케줄링을 활성화한다.
@Configuration
@EnableScheduling
@ConditionalOnProperty(prefix = "spring.kafka.producer", name = "key-serializer")
@Import({OutboxPackageRegistrar.class, OutboxRelay.class})
public class OutboxAutoConfiguration {
}
