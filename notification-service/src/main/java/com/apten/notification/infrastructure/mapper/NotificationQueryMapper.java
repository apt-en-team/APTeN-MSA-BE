package com.apten.notification.infrastructure.mapper;

import com.apten.notification.application.model.dto.NotificationDto;
import java.util.List;

// notification-service의 복잡 조회를 MyBatis로 분리할 때 사용할 Mapper 인터페이스
// 다중 조인이나 화면 전용 조회가 필요해지면 이 인터페이스와 XML을 함께 확장한다
public interface NotificationQueryMapper {

    // 알림 목록 조회가 필요해질 때 확장할 기본 메서드 위치
    List<NotificationDto> findNotificationSummaries();
}
