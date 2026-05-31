package com.apten.notification.infrastructure.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import java.io.FileInputStream;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class FirebaseConfig {

    private final NotificationPushProperties properties;

    @Bean
    @ConditionalOnProperty(prefix = "apten.notification.push", name = "fcm-enabled", havingValue = "true")
    public FirebaseMessaging firebaseMessaging() throws IOException {
        // APTEN_NOTIFICATION_PUSH_FCM_ENABLED=true일 때만 FirebaseMessaging Bean을 만든다
        String credentialsPath = properties.getFirebase().getCredentialsPath();
        log.info("[FCM] FirebaseMessaging Bean 생성 시도. credentialsPath={}, projectId={}",
                credentialsPath, properties.getFirebase().getProjectId());

        if (!StringUtils.hasText(credentialsPath)) {
            // 운영/배포에서는 FIREBASE_CREDENTIALS_PATH가 실제 마운트된 service account json을 가리켜야 한다
            throw new IllegalStateException("FIREBASE_CREDENTIALS_PATH is required when FCM is enabled");
        }

        FirebaseApp app = FirebaseApp.getApps().isEmpty() ? initializeFirebaseApp(credentialsPath) : FirebaseApp.getApps().get(0);
        log.info("[FCM] FirebaseMessaging Bean 생성 완료. appName={}", app.getName());
        return FirebaseMessaging.getInstance(app);
    }

    private FirebaseApp initializeFirebaseApp(String credentialsPath) throws IOException {
        // 서비스 계정 JSON은 Git에 넣지 않고 서버/로컬 외부 경로에서 읽는다
        // 파일이 없으면 기동 실패하므로 배포 manifest에서 credentials 마운트를 먼저 확인한다
        try (FileInputStream serviceAccount = new FileInputStream(credentialsPath)) {
            FirebaseOptions.Builder builder = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount));

            String projectId = properties.getFirebase().getProjectId();
            if (StringUtils.hasText(projectId)) {
                builder.setProjectId(projectId);
            }

            // Spring 재시작이나 테스트에서 이미 초기화된 FirebaseApp과 충돌하지 않게 한 번만 만든다
            return FirebaseApp.initializeApp(builder.build());
        }
    }
}
