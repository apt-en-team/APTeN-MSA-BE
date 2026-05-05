package com.apten.apartmentcomplex.infrastructure.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

// 행안부 주소 검색 API 설정값을 묶는 프로퍼티이다.
@Getter
@Setter
@ConfigurationProperties(prefix = "juso")
public class JusoProperties {

    private String apiKey;
    private String searchUrl;
}
