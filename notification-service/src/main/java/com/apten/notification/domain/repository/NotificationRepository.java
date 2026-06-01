package com.apten.notification.domain.repository;

import com.apten.notification.domain.entity.Notification;
import com.apten.notification.domain.enums.NotificationType;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

// notification-service의 저장과 단순 조회를 맡는 JPA Repository
// 복잡 조회는 infrastructure/mapper의 MyBatis 인터페이스로 분리하는 기준을 따른다
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // 알림 목록 조회 (기본)
    Page<Notification> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    // 알림 목록 조회 (읽음 필터)
    Page<Notification> findByUserIdAndIsReadOrderByCreatedAtDesc(Long userId, Boolean isRead, Pageable pageable);

    // 알림 목록 조회 (유형 필터)
    Page<Notification> findByUserIdAndTypeOrderByCreatedAtDesc(Long userId, NotificationType type, Pageable pageable);

    // 알림 목록 조회 (읽음 + 유형 필터)
    Page<Notification> findByUserIdAndIsReadAndTypeOrderByCreatedAtDesc(
            Long userId,
            Boolean isRead,
            NotificationType type,
            Pageable pageable
    );

    // 미읽음 알림 수 조회
    long countByUserIdAndIsRead(Long userId, Boolean isRead);

    // 미읽음 알림 목록 조회
    List<Notification> findByUserIdAndIsRead(Long userId, Boolean isRead);

    // 기준 일시 이전 읽음 알림 일괄 삭제
    @Modifying
    @Query("DELETE FROM Notification n WHERE n.isRead = true AND n.createdAt < :threshold")
    int deleteByIsReadTrueAndCreatedAtBefore(@Param("threshold") LocalDateTime threshold);
}
