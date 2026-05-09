package com.apten.common.outbox;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

// outbox relay는 JPA 초기화 이후에만 활성화해서 repository와 entity 준비 이후 동작하게 한다.
@AutoConfiguration(after = {
        HibernateJpaAutoConfiguration.class,
        JpaRepositoriesAutoConfiguration.class
})
@EnableScheduling
@ConditionalOnProperty(prefix = "apten.outbox", name = "enabled", havingValue = "true")
@Import(OutboxRelay.class)
public class OutboxAutoConfiguration {
}
