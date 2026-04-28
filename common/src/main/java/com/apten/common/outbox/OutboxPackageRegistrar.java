package com.apten.common.outbox;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.domain.EntityScanPackages;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import java.util.LinkedHashSet;
import java.util.Set;

// common outbox 패키지를 서비스 애플리케이션의 JPA 자동 스캔 후보로 등록한다.
public class OutboxPackageRegistrar implements ImportBeanDefinitionRegistrar {

    // producer 서비스가 common outbox 엔티티와 repository를 자동으로 찾을 수 있게 패키지를 추가한다.
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        // Spring Boot 기본 자동설정 패키지 후보에 outbox 패키지를 추가한다.
        AutoConfigurationPackages.register(registry, "com.apten.common.outbox");

        // 서비스 기본 패키지와 common outbox 패키지를 함께 entity scan 대상으로 유지한다.
        Set<String> entityScanPackages = new LinkedHashSet<>();

        // 현재 서비스의 기본 자동설정 패키지를 함께 넣어 기존 엔티티 스캔이 깨지지 않게 한다.
        if (registry instanceof BeanFactory beanFactory && AutoConfigurationPackages.has(beanFactory)) {
            entityScanPackages.addAll(AutoConfigurationPackages.get(beanFactory));
        }

        // common outbox 엔티티가 항상 Hibernate managed entity에 포함되도록 추가한다.
        entityScanPackages.add("com.apten.common.outbox");

        // 명시적 entity scan 패키지를 서비스 패키지 + common outbox 패키지 조합으로 등록한다.
        EntityScanPackages.register(registry, entityScanPackages.toArray(String[]::new));
    }
}
