package com.apten.notification.application.service;

import com.apten.notification.application.model.request.NotificationFcmTokenDeleteReq;
import com.apten.notification.application.model.request.NotificationFcmTokenPatchReq;
import com.apten.notification.application.model.request.NotificationFcmTokenPostReq;
import com.apten.notification.application.model.response.NotificationFcmTokenDeleteRes;
import com.apten.notification.application.model.response.NotificationFcmTokenPatchRes;
import com.apten.notification.application.model.response.NotificationFcmTokenPostRes;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

// FCM 토큰 등록과 해제, 갱신 흐름을 담당하는 응용 서비스
@Service
public class NotificationFcmService {

    public NotificationFcmTokenPostRes registerFcmToken(NotificationFcmTokenPostReq request) {
        // TODO: FCM 토큰 등록 로직 구현
        return NotificationFcmTokenPostRes.builder()
                .tokenRegistered(true)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public NotificationFcmTokenDeleteRes deleteFcmToken(NotificationFcmTokenDeleteReq request) {
        // TODO: FCM 토큰 비활성화 로직 구현
        return NotificationFcmTokenDeleteRes.builder()
                .tokenDeleted(true)
                .build();
    }

    public NotificationFcmTokenPatchRes updateFcmToken(NotificationFcmTokenPatchReq request) {
        // TODO: FCM 토큰 갱신 로직 구현
        return NotificationFcmTokenPatchRes.builder()
                .tokenUpdated(true)
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
