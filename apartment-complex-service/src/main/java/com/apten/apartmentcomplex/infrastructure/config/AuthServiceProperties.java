package com.apten.apartmentcomplex.infrastructure.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

// Auth Service 내부 호출 주소를 묶는 프로퍼티이다.
@Getter
@Setter
@ConfigurationProperties(prefix = "services.auth")
public class AuthServiceProperties {

    private String url;
}
