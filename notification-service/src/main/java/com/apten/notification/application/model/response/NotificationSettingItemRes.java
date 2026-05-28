package com.apten.notification.application.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// category 하나의 알림 설정 표시값
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationSettingItemRes {
    // NotificationCategory enum 이름
    private String category;
    // NotificationCategory DB 저장 코드
    private String categoryCode;
    // NotificationCategory 화면 표시값
    private String categoryValue;
    // true이면 해당 category 알림 생성 허용
    private Boolean enabled;
}
