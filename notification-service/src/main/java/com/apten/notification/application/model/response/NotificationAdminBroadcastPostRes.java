package com.apten.notification.application.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 관리자/매니저 복수 대상 알림 생성 응답 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationAdminBroadcastPostRes {
    // ACTIVE ADMIN/MANAGER 조회 결과 수 (알림 생성 시도 대상 수)
    private Integer targetCount;
    // 실제 알림이 생성된 수 (notification_setting OFF 제외 등으로 targetCount보다 작을 수 있다)
    private Integer createdCount;
}
