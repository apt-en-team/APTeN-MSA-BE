package com.apten.notification.application.model.request;

import com.apten.notification.domain.enums.NotificationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// notification-service 요청 모델의 공통 시작점으로 두는 최소 요청 DTO
// 알림 등록과 수정에서 공통으로 받을 값만 먼저 정리해 둔다
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationBaseRequest {

    // 알림 제목 입력값
    private String title;

    // 알림 상태 입력값
    private NotificationStatus status;
}
