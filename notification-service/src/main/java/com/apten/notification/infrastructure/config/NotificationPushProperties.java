package com.apten.notification.infrastructure.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "apten.notification.push")
public class NotificationPushProperties {
    // yml의 apten.notification.push.fcm-enabled 값은 APTEN_NOTIFICATION_PUSH_FCM_ENABLED로 덮어쓸 수 있다
    // 기본 false는 Firebase 없이 DB/WebSocket 알림만 안전하게 사용하기 위한 값이다
    private boolean fcmEnabled = false;
    // WebpushConfig.link는 절대 URL이어야 하므로 상대경로 앞에 이 값을 붙인다
    private String webBaseUrl = "https://tc.greenart.n-e.kr";
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
