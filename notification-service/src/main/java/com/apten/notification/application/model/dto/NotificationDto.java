package com.apten.notification.application.model.dto;

import com.apten.notification.domain.enums.NotificationStatus;
import lombok.Builder;
import lombok.Getter;

// notification-service 내부 계층 간 전달에 사용할 최소 DTO
// 요청 모델과 엔티티를 직접 섞지 않기 위한 중간 전달 객체다
@Getter
@Builder
public class NotificationDto {

    // 알림 식별자
    private final Long id;

    // 알림 제목
    private final String title;

    // 알림 상태
    private final NotificationStatus status;
}
