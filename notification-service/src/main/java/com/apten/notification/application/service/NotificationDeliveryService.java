package com.apten.notification.application.service;

import com.apten.notification.application.model.request.NotificationPushPostReq;
import com.apten.notification.application.model.response.NotificationBoardOrVoteCreatedEventRes;
import com.apten.notification.application.model.response.NotificationPushPostRes;
import com.apten.notification.application.model.response.NotificationReservationStatusChangedEventRes;
import com.apten.notification.application.model.response.NotificationUserSignupRequestedEventRes;
import com.apten.notification.application.model.response.NotificationVehicleApprovalRequestedEventRes;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

// 푸시 발송과 이벤트 기반 알림 생성 위치를 담당하는 응용 서비스
@Service
@RequiredArgsConstructor
public class NotificationDeliveryService {

    private final NotificationPushService notificationPushService;

    public NotificationPushPostRes sendPushNotification(NotificationPushPostReq request) {
        // 내부 API는 DB 알림 ID만 넘기고 실제 발송 가능 여부는 PushService 설정이 판단한다
        Long notificationId = request == null ? null : request.getNotificationId();
        boolean sent = notificationPushService.send(notificationId);
        return NotificationPushPostRes.builder()
                .notificationId(notificationId)
                .sent(sent)
                .sentAt(LocalDateTime.now())
                .build();
    }

    public NotificationUserSignupRequestedEventRes handleUserSignupRequestedEvent() {
        // TODO: Kafka 연동 단계에서 회원가입 신청 알림 이벤트 소비 처리
        return NotificationUserSignupRequestedEventRes.builder()
                .consumed(false)
                .createdCount(0)
                .build();
    }

    public NotificationVehicleApprovalRequestedEventRes handleVehicleApprovalRequestedEvent() {
        // TODO: Kafka 연동 단계에서 차량 등록 신청 알림 이벤트 소비 처리
        return NotificationVehicleApprovalRequestedEventRes.builder()
                .consumed(false)
                .createdCount(0)
                .build();
    }

    public NotificationReservationStatusChangedEventRes handleReservationStatusChangedEvent() {
        // TODO: Kafka 연동 단계에서 시설예약/GX 알림 생성 처리
        return NotificationReservationStatusChangedEventRes.builder()
                .consumed(false)
                .createdCount(0)
                .build();
    }

    public NotificationBoardOrVoteCreatedEventRes handleBoardOrVoteCreatedEvent() {
        // TODO: Kafka 연동 단계에서 공지 또는 투표 알림 생성 처리
        return NotificationBoardOrVoteCreatedEventRes.builder()
                .consumed(false)
                .createdCount(0)
                .build();
    }
}
