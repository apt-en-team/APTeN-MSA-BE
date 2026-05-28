package com.apten.facilityreservation.infrastructure.client;

import com.apten.facilityreservation.infrastructure.client.model.NotificationCreateReq;
import com.apten.facilityreservation.infrastructure.config.NotificationServiceProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

// notification-service 내부 알림 생성 API를 호출하는 HTTP 클라이언트이다
// 예약 서비스는 이 클라이언트를 직접 알지 않고 FacilityNotificationService를 통해서만 사용한다
@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationInternalClient {

    // notification-service의 InternalNotificationController endpoint와 맞춘다
    private static final String INTERNAL_NOTIFICATIONS_PATH = "/internal/notifications";

    private final RestClient.Builder restClientBuilder;
    private final NotificationServiceProperties notificationServiceProperties;

    public void createNotification(NotificationCreateReq request) {
        String url = notificationServiceProperties.getServiceUrl() + INTERNAL_NOTIFICATIONS_PATH;
        // ResultResponse body는 현재 사용하지 않고, 호출 성공/실패만 판단한다
        // 실패 예외는 상위 FacilityNotificationService에서 best-effort로 흡수한다
      log.info("url: {}", url);
        restClientBuilder.build()
                .post()
                .uri(url)
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }
}
