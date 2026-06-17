package com.apten.common.outbox;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;

// outbox relay는 JPA 초기화 이후에만 활성화해서 repository와 entity 준비 이후 동작하게 한다.
// @Import 방식은 @ConditionalOnProperty를 무시하므로 @Bean 메서드 방식으로 등록한다.
@AutoConfiguration(after = {
        HibernateJpaAutoConfiguration.class,
        JpaRepositoriesAutoConfiguration.class
})
@EnableScheduling
@ConditionalOnProperty(prefix = "apten.outbox", name = "enabled", havingValue = "true")
public class OutboxAutoConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = "apten.outbox", name = "polling-enabled", havingValue = "true", matchIfMissing = true)
    public OutboxRelay outboxRelay(OutboxRepository outboxRepository, KafkaTemplate<String, String> kafkaTemplate) {
        return new OutboxRelay(outboxRepository, kafkaTemplate);
    }

    @Bean
    @ConditionalOnProperty(prefix = "apten.outbox", name = "cleanup-enabled", havingValue = "true")
    public OutboxCleanupJob outboxCleanupJob(OutboxRepository outboxRepository) {
        return new OutboxCleanupJob(outboxRepository);
    }
}
