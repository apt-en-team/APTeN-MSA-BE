package com.apten.notification.application.mapper;

import com.apten.notification.application.model.dto.NotificationDto;
import com.apten.notification.application.model.request.NotificationBaseRequest;
import com.apten.notification.application.model.response.NotificationBaseResponse;
import com.apten.notification.domain.entity.Notification;
import org.springframework.stereotype.Component;

// notification-service의 요청, 응답, 내부 DTO 변환을 맡는 전용 매퍼
// 서비스가 변환 코드까지 떠안지 않도록 application 계층 안에서 역할을 분리한다
@Component
public class NotificationDtoMapper {

    // 요청 DTO를 저장 전 엔티티 형태로 옮긴다
    public Notification toEntity(NotificationBaseRequest request) {
        return Notification.builder()
                .title(request.getTitle())
                .status(request.getStatus())
                .build();
    }

    // 엔티티를 서비스 내부 전달용 DTO로 바꾼다
    public NotificationDto toDto(Notification notification) {
        return NotificationDto.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .status(notification.getStatus())
                .build();
    }

    // 내부 DTO를 외부 응답 모델로 바꾼다
    public NotificationBaseResponse toResponse(NotificationDto notificationDto) {
        return NotificationBaseResponse.builder()
                .id(notificationDto.getId())
                .title(notificationDto.getTitle())
                .status(notificationDto.getStatus())
                .build();
    }
}
