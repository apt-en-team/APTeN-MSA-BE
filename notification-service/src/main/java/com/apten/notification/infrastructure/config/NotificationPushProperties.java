package com.apten.notification.infrastructure.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "apten.notification.push")
public class NotificationPushProperties {
    // false이면 Firebase 호출 없이 DB/WebSocket 알림만 동작한다
    private boolean fcmEnabled = false;
    // Firebase credential은 코드가 아니라 yml/env 설정으로만 주입한다
    private Firebase firebase = new Firebase();

    @Getter
    @Setter
    public static class Firebase {
        // 서비스 계정 파일 경로는 HTTPS 배포 후 실제 발송 단계에서 사용한다
        private String credentialsPath;
        // Firebase 프로젝트 식별자는 운영 환경별 설정으로 분리한다
        private String projectId;
    }
}
