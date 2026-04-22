package com.apten.notification.application.model.response;

import com.apten.notification.domain.enums.NotificationStatus;
import lombok.Builder;
import lombok.Getter;

// notification-service 응답 모델의 공통 시작점으로 두는 최소 응답 DTO
// 공통 ResultResponse 안에 담길 알림 전용 응답 객체가 이 패키지에 모이게 된다
@Getter
@Builder
public class NotificationBaseResponse {

    // 알림 식별자
    private final Long id;

    // 알림 제목
    private final String title;

    // 알림 상태
    private final NotificationStatus status;
}
