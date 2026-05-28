package com.apten.notification.application.model.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 알림 설정 조회 응답 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationSettingGetRes {
    // 전체 category 설정 상태 목록
    private List<NotificationSettingItemRes> settings;
}
