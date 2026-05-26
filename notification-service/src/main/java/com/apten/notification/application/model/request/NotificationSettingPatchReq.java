package com.apten.notification.application.model.request;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 여러 알림 category 설정을 한 번에 바꾸는 요청 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationSettingPatchReq {
    // 변경할 category와 ON/OFF 값 목록
    private List<NotificationSettingReq> settings;
}
