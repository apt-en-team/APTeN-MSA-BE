package com.apten.common.outbox;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Import;

// common outbox 패키지를 JPA 초기화 전에 등록하기 위한 선행 자동설정이다.
@AutoConfiguration(before = {
        HibernateJpaAutoConfiguration.class,
        JpaRepositoriesAutoConfiguration.class
})
@ConditionalOnProperty(prefix = "apten.outbox", name = "enabled", havingValue = "true")
@Import(OutboxPackageRegistrar.class)
public class OutboxPackagesAutoConfiguration {
}
