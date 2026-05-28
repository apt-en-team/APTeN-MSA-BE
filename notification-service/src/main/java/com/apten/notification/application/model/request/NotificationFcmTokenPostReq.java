package com.apten.notification.application.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// FCM 토큰 등록 요청 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationFcmTokenPostReq {
    // 브라우저나 앱에서 발급받은 FCM 토큰 원문
    private String token;
    // WEB, MOBILE 같은 기기 구분값
    private String deviceType;
    // Chrome, Safari 등 푸시 동작 차이를 확인하기 위한 브라우저 정보
    private String browserType;
    // PWA 또는 앱 버전별 토큰 이슈를 추적하기 위한 값
    private String appVersion;
}
