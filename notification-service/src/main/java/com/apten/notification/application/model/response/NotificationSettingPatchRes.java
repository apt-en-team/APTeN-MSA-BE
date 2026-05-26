package com.apten.notification.application.model.response;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 알림 설정 변경 응답 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationSettingPatchRes {
    // 저장 처리 성공 여부
    private Boolean updated;
    // 변경 후 전체 category 설정 상태
    private List<NotificationSettingItemRes> settings;
    // 변경 처리 시각
    private LocalDateTime updatedAt;
}
