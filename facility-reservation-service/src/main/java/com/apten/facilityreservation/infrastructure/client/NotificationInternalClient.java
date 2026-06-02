package com.apten.facilityreservation.infrastructure.client;

import com.apten.facilityreservation.infrastructure.client.model.NotificationAdminBroadcastReq;
import com.apten.facilityreservation.infrastructure.client.model.NotificationCreateReq;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

// notification-service 내부 알림 생성 API를 호출하는 Feign 클라이언트이다
// 게이트웨이 인증을 거치지 않고 내부망에서 notification-service를 직접 호출한다
// url은 application.yml의 apten.internal.notification.service-url을 직접 참조한다
// FacilityNotificationService에서만 사용하며 직접 호출하지 않는다
@FeignClient(name = "notification-internal", url = "${apten.internal.notification.service-url}")
public interface NotificationInternalClient {

    // 입주민 단일 수신자 알림 생성
    @PostMapping("/internal/notifications")
    void createNotification(@RequestBody NotificationCreateReq request);

    // 단지 ACTIVE ADMIN/MANAGER 전체 대상 알림 생성 (MASTER 제외)
    @PostMapping("/internal/notifications/admin-broadcast")
    void createAdminBroadcastNotification(@RequestBody NotificationAdminBroadcastReq request);
}
