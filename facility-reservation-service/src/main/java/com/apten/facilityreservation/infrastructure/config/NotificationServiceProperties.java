package com.apten.facilityreservation.infrastructure.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

// notification-service 내부 호출 주소를 묶는 프로퍼티이다
// 환경별 주소만 yml/env로 바꾸면 호출 코드는 그대로 유지된다
@Getter
@Setter
@ConfigurationProperties(prefix = "apten.internal.notification")
public class NotificationServiceProperties {

    // 로컬은 localhost, 배포는 서비스 DNS 주소를 yml/env에서 주입한다
    // 예: 로컬 http://localhost:9081, 배포 http://notification-service:80
    private String serviceUrl;
}
