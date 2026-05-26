package com.apten.notification.domain.repository;

import com.apten.notification.domain.entity.Notification;
import com.apten.notification.domain.enums.NotificationType;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

// notification-service의 저장과 단순 조회를 맡는 JPA Repository
// 복잡 조회는 infrastructure/mapper의 MyBatis 인터페이스로 분리하는 기준을 따른다
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // 알림 목록 기본 조회: 본인 알림만 최신순으로 본다
    Page<Notification> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    // 읽음/미읽음 필터가 있을 때 사용하는 목록 조회
    Page<Notification> findByUserIdAndIsReadOrderByCreatedAtDesc(Long userId, Boolean isRead, Pageable pageable);

    // 알림 유형 필터가 있을 때 사용하는 목록 조회
    Page<Notification> findByUserIdAndTypeOrderByCreatedAtDesc(Long userId, NotificationType type, Pageable pageable);

    // 읽음 상태와 유형 필터를 함께 적용할 때 사용하는 목록 조회
    Page<Notification> findByUserIdAndIsReadAndTypeOrderByCreatedAtDesc(
            Long userId,
            Boolean isRead,
            NotificationType type,
            Pageable pageable
    );

    // 배지와 WebSocket payload에서 사용할 미읽음 수 조회
    long countByUserIdAndIsRead(Long userId, Boolean isRead);

    // 전체 읽음 처리에서 본인 미읽음 알림만 가져온다
    List<Notification> findByUserIdAndIsRead(Long userId, Boolean isRead);
}
