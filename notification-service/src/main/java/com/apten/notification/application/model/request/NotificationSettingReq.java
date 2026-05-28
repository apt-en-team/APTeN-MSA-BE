package com.apten.notification.application.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 알림 category 하나의 ON/OFF 변경값
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationSettingReq {
    // NotificationCategory name/code/value 중 하나
    private String category;
    // true이면 알림 생성 허용, false이면 생성 제외
    private Boolean enabled;
}
