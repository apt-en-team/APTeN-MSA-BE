package com.apten.common.outbox;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

// Kafka producer 설정이 있는 서비스에서만 outbox 엔티티, repository, relay를 함께 활성화한다.
@AutoConfiguration
@EnableScheduling
@ConditionalOnProperty(prefix = "spring.kafka.producer", name = "key-serializer")
@Import({OutboxPackageRegistrar.class, OutboxRelay.class})
public class OutboxAutoConfiguration {
}
