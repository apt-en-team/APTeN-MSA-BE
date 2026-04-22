package com.apten.notification.application.service;

import com.apten.notification.application.mapper.NotificationDtoMapper;
import com.apten.notification.application.model.dto.NotificationDto;
import com.apten.notification.application.model.response.NotificationBaseResponse;
import com.apten.notification.domain.entity.Notification;
import com.apten.notification.domain.enums.NotificationStatus;
import com.apten.notification.domain.repository.NotificationRepository;
import com.apten.notification.infrastructure.mapper.NotificationQueryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

// notification-service 유스케이스를 조합할 기본 서비스 클래스
// JPA와 MyBatis를 함께 사용하는 위치가 application/service라는 점을 구조로 보여준다
@Service
@RequiredArgsConstructor
public class NotificationService {

    // 단건 저장과 기본 조회는 JPA Repository가 맡는다
    private final NotificationRepository notificationRepository;

    // 복잡 조회가 필요해지면 MyBatis 매퍼를 이 계층에서만 사용한다
    private final ObjectProvider<NotificationQueryMapper> notificationQueryMapperProvider;

    // 요청 DTO와 응답 DTO 변환은 전용 매퍼에 맡긴다
    private final NotificationDtoMapper notificationDtoMapper;

    // 컨트롤러가 바로 연결할 수 있는 최소 응답 형태를 반환한다
    public NotificationBaseResponse getNotificationTemplate() {
        Notification notification = Notification.builder()
                .id(1L)
                .title("notification-template")
                .status(NotificationStatus.READY)
                .build();

        NotificationDto notificationDto = notificationDtoMapper.toDto(notification);
        return notificationDtoMapper.toResponse(notificationDto);
    }
}
