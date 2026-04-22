package com.apten.notification.application.service;

import com.apten.notification.application.model.request.NotificationPushPostReq;
import com.apten.notification.application.model.response.NotificationBoardOrVoteCreatedEventRes;
import com.apten.notification.application.model.response.NotificationPushPostRes;
import com.apten.notification.application.model.response.NotificationReservationStatusChangedEventRes;
import com.apten.notification.application.model.response.NotificationUserSignupRequestedEventRes;
import com.apten.notification.application.model.response.NotificationVehicleApprovalRequestedEventRes;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

// 푸시 발송과 이벤트 기반 알림 생성 위치를 담당하는 응용 서비스
@Service
public class NotificationDeliveryService {

    public NotificationPushPostRes sendPushNotification(NotificationPushPostReq request) {
        // TODO: FCM 토큰 조회 후 푸시 발송 처리
        return NotificationPushPostRes.builder()
                .notificationUid(request.getNotificationUid())
                .sent(false)
                .sentAt(LocalDateTime.now())
                .build();
    }

    public NotificationUserSignupRequestedEventRes handleUserSignupRequestedEvent() {
        // TODO: 회원가입 신청 알림 이벤트 소비 처리
        return NotificationUserSignupRequestedEventRes.builder()
                .consumed(false)
                .createdCount(0)
                .build();
    }

    public NotificationVehicleApprovalRequestedEventRes handleVehicleApprovalRequestedEvent() {
        // TODO: 차량 등록 신청 알림 이벤트 소비 처리
        return NotificationVehicleApprovalRequestedEventRes.builder()
                .consumed(false)
                .createdCount(0)
                .build();
    }

    public NotificationReservationStatusChangedEventRes handleReservationStatusChangedEvent() {
        // TODO: 예약 상태 알림 생성 처리
        return NotificationReservationStatusChangedEventRes.builder()
                .consumed(false)
                .createdCount(0)
                .build();
    }

    public NotificationBoardOrVoteCreatedEventRes handleBoardOrVoteCreatedEvent() {
        // TODO: 공지 또는 투표 알림 생성 처리
        return NotificationBoardOrVoteCreatedEventRes.builder()
                .consumed(false)
                .createdCount(0)
                .build();
    }
}
