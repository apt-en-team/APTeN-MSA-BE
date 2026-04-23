package com.apten.common.outbox;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

// common outbox 패키지를 서비스 애플리케이션의 JPA 자동 스캔 후보로 등록한다.
public class OutboxPackageRegistrar implements ImportBeanDefinitionRegistrar {

    // producer 서비스가 common outbox 엔티티와 repository를 자동으로 찾을 수 있게 패키지를 추가한다.
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        AutoConfigurationPackages.register(registry, "com.apten.common.outbox");
    }
}
