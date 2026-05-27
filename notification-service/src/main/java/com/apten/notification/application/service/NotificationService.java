package com.apten.notification.application.service;

import com.apten.common.exception.BusinessException;
import com.apten.common.exception.CommonErrorCode;
import com.apten.notification.application.model.request.NotificationCreateCommand;
import com.apten.notification.application.model.request.NotificationOwnerCheckReq;
import com.apten.notification.application.model.request.NotificationPostReq;
import com.apten.notification.application.model.request.NotificationSearchReq;
import com.apten.notification.application.model.response.NotificationCleanupRes;
import com.apten.notification.application.model.response.NotificationGetPageRes;
import com.apten.notification.application.model.response.NotificationOwnerCheckRes;
import com.apten.notification.application.model.response.NotificationPostRes;
import com.apten.notification.application.model.response.NotificationReadAllRes;
import com.apten.notification.application.model.response.NotificationReadRes;
import com.apten.notification.application.model.response.NotificationRes;
import com.apten.notification.application.model.response.NotificationUnreadCountRes;
import com.apten.notification.domain.entity.Notification;
import com.apten.notification.domain.enums.NotificationCategory;
import com.apten.notification.domain.enums.NotificationTargetType;
import com.apten.notification.domain.enums.NotificationType;
import com.apten.notification.domain.repository.NotificationRepository;
import com.apten.notification.exception.NotificationErrorCode;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

// 앱 내 알림 조회와 읽음 처리, 내부 생성 흐름을 담당하는 응용 서비스
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationSettingService notificationSettingService;
    private final NotificationRealtimeService notificationRealtimeService;

    @Transactional(readOnly = true)
    public NotificationGetPageRes getNotificationList(Long userId, NotificationSearchReq request) {
        validateUserId(userId);
        NotificationSearchReq searchReq = request == null ? NotificationSearchReq.builder().build() : request;
        int page = searchReq.getPage() == null || searchReq.getPage() < 0 ? 0 : searchReq.getPage();
        int size = searchReq.getSize() == null || searchReq.getSize() <= 0 ? 20 : searchReq.getSize();
        Pageable pageable = PageRequest.of(page, size);
        NotificationType type = parseNullableType(searchReq.getType());

        Page<Notification> notifications;
        if (searchReq.getIsRead() != null && type != null) {
            notifications = notificationRepository.findByUserIdAndIsReadAndTypeOrderByCreatedAtDesc(
                    userId,
                    searchReq.getIsRead(),
                    type,
                    pageable
            );
        } else if (searchReq.getIsRead() != null) {
            notifications = notificationRepository.findByUserIdAndIsReadOrderByCreatedAtDesc(
                    userId,
                    searchReq.getIsRead(),
                    pageable
            );
        } else if (type != null) {
            notifications = notificationRepository.findByUserIdAndTypeOrderByCreatedAtDesc(userId, type, pageable);
        } else {
            notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        }

        return NotificationGetPageRes.builder()
                .content(notifications.getContent().stream().map(this::toRes).toList())
                .page(notifications.getNumber())
                .size(notifications.getSize())
                .totalElements(notifications.getTotalElements())
                .totalPages(notifications.getTotalPages())
                .hasNext(notifications.hasNext())
                .build();
    }

    @Transactional(readOnly = true)
    public NotificationUnreadCountRes getUnreadCount(Long userId) {
        validateUserId(userId);
        return NotificationUnreadCountRes.builder()
                .unreadCount(notificationRepository.countByUserIdAndIsRead(userId, false))
                .build();
    }

    @Transactional
    public NotificationReadRes readNotification(Long userId, Long notificationId) {
        validateUserId(userId);
        Notification notification = getNotification(notificationId);
        validateOwner(userId, notification);
        LocalDateTime readAt = LocalDateTime.now();
        notification.markRead(readAt);
        return NotificationReadRes.builder()
                .notificationId(notification.getId())
                .isRead(notification.getIsRead())
                .readAt(notification.getReadAt())
                .build();
    }

    @Transactional
    public NotificationReadAllRes readAllNotifications(Long userId) {
        validateUserId(userId);
        List<Notification> unreadNotifications = notificationRepository.findByUserIdAndIsRead(userId, false);
        LocalDateTime readAt = LocalDateTime.now();
        unreadNotifications.forEach(notification -> notification.markRead(readAt));
        return NotificationReadAllRes.builder()
                .updatedCount(unreadNotifications.size())
                .readAt(readAt)
                .build();
    }

    @Transactional
    public NotificationPostRes createNotification(NotificationPostReq request) {
        return createNotification(NotificationCreateCommand.builder()
                .receiverUserId(request.getReceiverUserId())
                .complexId(request.getComplexId())
                .type(request.getType())
                .targetType(request.getTargetType())
                .targetId(request.getTargetId())
                .title(request.getTitle())
                .content(request.getContent())
                .linkPath(request.getLinkPath())
                .payloadJson(request.getPayloadJson())
                .build());
    }

    @Transactional
    public NotificationPostRes createNotification(NotificationCreateCommand command) {
        validateCreateCommand(command);
        NotificationType type = parseType(command.getType());
        NotificationCategory category = NotificationCategory.fromType(type);
        if (!notificationSettingService.isEnabled(command.getReceiverUserId(), category)) {
            return NotificationPostRes.builder()
                    .receiverUserId(command.getReceiverUserId())
                    .createdAt(LocalDateTime.now())
                    .build();
        }

        Notification notification = Notification.builder()
                .userId(command.getReceiverUserId())
                .complexId(command.getComplexId())
                .type(type)
                .targetType(parseTargetType(command.getTargetType()))
                .targetId(command.getTargetId())
                .title(command.getTitle())
                .content(command.getContent())
                .linkPath(command.getLinkPath())
                .payloadJson(command.getPayloadJson())
                .isRead(false)
                .build();

        Notification saved = notificationRepository.save(notification);
        // DB 저장이 원본이므로 commit 이후 접속 중인 사용자에게만 실시간 전송을 시도한다
        sendRealtimeAfterCommit(saved);
        return NotificationPostRes.builder()
                .notificationId(saved.getId())
                .receiverUserId(saved.getUserId())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    @Transactional
    public NotificationCleanupRes cleanupOldNotifications() {
        // 후속 범위: 오래된 알림 삭제 정책 확정 후 구현
        return NotificationCleanupRes.builder()
                .deletedCount(0)
                .executedAt(LocalDateTime.now())
                .build();
    }

    @Transactional(readOnly = true)
    public NotificationOwnerCheckRes checkNotificationOwner(NotificationOwnerCheckReq request) {
        if (request == null || request.getLoginUserId() == null || request.getNotificationId() == null) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }
        Notification notification = getNotification(request.getNotificationId());
        return NotificationOwnerCheckRes.builder()
                .allowed(notification.getUserId().equals(request.getLoginUserId()))
                .build();
    }

    private NotificationRes toRes(Notification notification) {
        return NotificationRes.builder()
                .notificationId(notification.getId())
                .title(notification.getTitle())
                .content(notification.getContent())
                .type(notification.getType().name())
                .typeCode(notification.getType().getCode())
                .typeValue(notification.getType().getValue())
                .targetType(notification.getTargetType().name())
                .targetTypeCode(notification.getTargetType().getCode())
                .targetTypeValue(notification.getTargetType().getValue())
                .targetId(notification.getTargetId())
                .linkPath(notification.getLinkPath())
                .isRead(notification.getIsRead())
                .readAt(notification.getReadAt())
                .createdAt(notification.getCreatedAt())
                .build();
    }

    private Notification getNotification(Long notificationId) {
        if (notificationId == null) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }
        return notificationRepository.findById(notificationId)
                .orElseThrow(() -> new BusinessException(NotificationErrorCode.NOTIFICATION_NOT_FOUND));
    }

    private void validateOwner(Long userId, Notification notification) {
        if (!notification.getUserId().equals(userId)) {
            throw new BusinessException(NotificationErrorCode.FORBIDDEN);
        }
    }

    private void validateUserId(Long userId) {
        if (userId == null) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }
    }

    private void validateCreateCommand(NotificationCreateCommand command) {
        if (command == null
                || command.getReceiverUserId() == null
                || command.getComplexId() == null
                || command.getType() == null
                || command.getTargetType() == null
                || command.getTitle() == null
                || command.getTitle().isBlank()
                || command.getContent() == null
                || command.getContent().isBlank()) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }
    }

    private NotificationType parseNullableType(String type) {
        if (type == null || type.isBlank()) {
            return null;
        }
        return parseType(type);
    }

    private NotificationType parseType(String type) {
        try {
            return NotificationType.from(type);
        } catch (RuntimeException exception) {
            throw new BusinessException(NotificationErrorCode.INVALID_NOTIFICATION_TYPE);
        }
    }

    private NotificationTargetType parseTargetType(String targetType) {
        try {
            return NotificationTargetType.from(targetType);
        } catch (RuntimeException exception) {
            throw new BusinessException(NotificationErrorCode.INVALID_NOTIFICATION_TARGET_TYPE);
        }
    }

    private void sendRealtimeAfterCommit(Notification notification) {
        // 테스트나 내부 호출처럼 트랜잭션 동기화가 없으면 즉시 best-effort 전송한다
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            notificationRealtimeService.sendNewNotification(notification);
            return;
        }

        // 커밋 전에 메시지가 먼저 나가면 클라이언트 재조회 시 데이터가 안 보일 수 있어 afterCommit으로 늦춘다
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                notificationRealtimeService.sendNewNotification(notification);
            }
        });
    }
}
