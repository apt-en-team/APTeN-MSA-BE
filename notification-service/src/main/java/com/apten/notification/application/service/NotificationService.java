package com.apten.notification.application.service;

import com.apten.common.exception.BusinessException;
import com.apten.common.exception.CommonErrorCode;
import com.apten.notification.application.model.request.NotificationAdminBroadcastPostReq;
import com.apten.notification.application.model.request.NotificationCreateCommand;
import com.apten.notification.application.model.request.NotificationOwnerCheckReq;
import com.apten.notification.application.model.request.NotificationPostReq;
import com.apten.notification.application.model.request.NotificationSearchReq;
import com.apten.notification.application.model.response.NotificationAdminBroadcastPostRes;
import com.apten.notification.application.model.response.NotificationCleanupRes;
import com.apten.notification.application.model.response.NotificationGetPageRes;
import com.apten.notification.application.model.response.NotificationOwnerCheckRes;
import com.apten.notification.application.model.response.NotificationPostRes;
import com.apten.notification.application.model.response.NotificationReadAllRes;
import com.apten.notification.application.model.response.NotificationReadRes;
import com.apten.notification.application.model.response.NotificationRes;
import com.apten.notification.application.model.response.NotificationUnreadCountRes;
import com.apten.notification.domain.entity.Notification;
import com.apten.notification.domain.entity.UserCache;
import com.apten.notification.domain.enums.NotificationCategory;
import com.apten.notification.domain.enums.NotificationTargetType;
import com.apten.notification.domain.enums.NotificationType;
import com.apten.notification.domain.enums.UserCacheRole;
import com.apten.notification.domain.enums.UserCacheStatus;
import com.apten.notification.domain.repository.NotificationRepository;
import com.apten.notification.domain.repository.UserCacheRepository;
import com.apten.notification.exception.NotificationErrorCode;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationSettingService notificationSettingService;
    private final NotificationRealtimeService notificationRealtimeService;
    private final NotificationPushService notificationPushService;
    private final UserCacheRepository userCacheRepository;

    // 알림 목록 조회
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

    // 미읽음 알림 수 조회
    @Transactional(readOnly = true)
    public NotificationUnreadCountRes getUnreadCount(Long userId) {
        validateUserId(userId);
        return NotificationUnreadCountRes.builder()
                .unreadCount(notificationRepository.countByUserIdAndIsRead(userId, false))
                .build();
    }

    // 알림 읽음 처리
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

    // 전체 알림 읽음 처리
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

    // 알림 생성 (외부 HTTP 요청)
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

    // 알림 생성 (setting 검증 → DB 저장 → 커밋 후 WebSocket/FCM 발송)
    @Transactional
    public NotificationPostRes createNotification(NotificationCreateCommand command) {
        validateCreateCommand(command);
        NotificationType type = parseType(command.getType());
        NotificationCategory category = NotificationCategory.fromType(type);
        // 사용자가 카테고리를 OFF로 둔 경우 원본 DB 저장과 WebSocket/FCM 발송을 모두 생략한다
        if (!notificationSettingService.isEnabled(command.getReceiverUserId(), category)) {
            log.info("[Notification] 알림 설정 OFF로 생성 생략. receiverUserId={}, category={}, type={}",
                    command.getReceiverUserId(), category, type);
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
        // DB 저장이 원본이므로 commit 이후 WebSocket/FCM을 각각 best-effort로 시도한다
        dispatchAfterCommit(saved);
        return NotificationPostRes.builder()
                .notificationId(saved.getId())
                .receiverUserId(saved.getUserId())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    // 오래된 읽음 알림 삭제 (30일 기준, 미읽음 제외)
    @Transactional
    public NotificationCleanupRes cleanupOldNotifications() {
        LocalDateTime now = LocalDateTime.now();
        // 미읽음 알림은 사용자가 아직 확인하지 않았으므로 삭제 대상에서 제외한다
        LocalDateTime threshold = now.minusDays(30);
        int deletedCount = notificationRepository.deleteByIsReadTrueAndCreatedAtBefore(threshold);
        return NotificationCleanupRes.builder()
                .deletedCount(deletedCount)
                .executedAt(now)
                .build();
    }

    // 관리자 broadcast 알림 생성 (단지 ADMIN/MANAGER 전체, MASTER 제외)
    @Transactional
    public NotificationAdminBroadcastPostRes createAdminBroadcastNotification(NotificationAdminBroadcastPostReq request) {
        // complexId 기준으로 ACTIVE 상태의 ADMIN, MANAGER를 조회한다
        // MASTER는 단지별 알림 수신 대상에서 제외한다
        List<UserCache> admins = userCacheRepository.findByComplexIdAndRoleInAndStatus(
                request.getComplexId(),
                List.of(UserCacheRole.ADMIN, UserCacheRole.MANAGER),
                UserCacheStatus.ACTIVE
        );

        if (admins.isEmpty()) {
            log.info("[Notification] 관리자 broadcast 대상 없음. complexId={}, type={}", request.getComplexId(), request.getType());
            return NotificationAdminBroadcastPostRes.builder()
                    .targetCount(0)
                    .createdCount(0)
                    .build();
        }

        int createdCount = 0;
        for (UserCache admin : admins) {
            try {
                NotificationPostRes result = createNotification(NotificationCreateCommand.builder()
                        .receiverUserId(admin.getId())
                        .complexId(request.getComplexId())
                        .type(request.getType())
                        .targetType(request.getTargetType())
                        .targetId(request.getTargetId())
                        .title(request.getTitle())
                        .content(request.getContent())
                        .linkPath(request.getLinkPath())
                        .payloadJson(request.getPayloadJson())
                        .build());
                // notificationId가 있으면 실제로 저장된 것이다 (setting OFF이면 null)
                if (result.getNotificationId() != null) {
                    createdCount++;
                }
            } catch (Exception exception) {
                // 일부 관리자 알림 실패가 나머지 생성을 막지 않도록 예외를 흡수한다
                log.warn("[Notification] 관리자 알림 생성 실패. userId={}, complexId={}, type={}",
                        admin.getId(), request.getComplexId(), request.getType(), exception);
            }
        }

        log.info("[Notification] 관리자 broadcast 완료. complexId={}, type={}, target={}, created={}",
                request.getComplexId(), request.getType(), admins.size(), createdCount);

        return NotificationAdminBroadcastPostRes.builder()
                .targetCount(admins.size())
                .createdCount(createdCount)
                .build();
    }

    // 입주민 broadcast 알림 생성 (단지 USER 전체, 작성자 본인 제외)
    @Transactional
    public void createResidentBroadcastNotification(
            Long complexId, String type, String targetType, Long targetId,
            String title, String content, String linkPath, Long excludeUserId) {

        List<UserCache> residents = userCacheRepository.findByComplexIdAndRoleAndStatus(
                complexId, UserCacheRole.USER, UserCacheStatus.ACTIVE
        );

        if (residents.isEmpty()) {
            log.info("[Notification] 입주민 broadcast 대상 없음. complexId={}, type={}", complexId, type);
            return;
        }

        int createdCount = 0;
        for (UserCache resident : residents) {
            // 작성자 본인에게는 알림을 보내지 않는다
            if (excludeUserId != null && excludeUserId.equals(resident.getId())) {
                continue;
            }
            try {
                NotificationPostRes result = createNotification(NotificationCreateCommand.builder()
                        .receiverUserId(resident.getId())
                        .complexId(complexId)
                        .type(type)
                        .targetType(targetType)
                        .targetId(targetId)
                        .title(title)
                        .content(content)
                        .linkPath(linkPath)
                        .build());
                if (result.getNotificationId() != null) {
                    createdCount++;
                }
            } catch (Exception exception) {
                log.warn("[Notification] 입주민 알림 생성 실패. userId={}, complexId={}, type={}",
                        resident.getId(), complexId, type, exception);
            }
        }

        log.info("[Notification] 입주민 broadcast 완료. complexId={}, type={}, target={}, created={}",
                complexId, type, residents.size(), createdCount);
    }

    // 알림 소유자 확인 (내부 권한 검증용)
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

    private void dispatchAfterCommit(Notification notification) {
        boolean synchronizationActive = TransactionSynchronizationManager.isSynchronizationActive();
        log.info("[Notification] 후속 발송 dispatch 진입. notificationId={}, transactionSyncActive={}",
                notification.getId(), synchronizationActive);

        // 테스트나 내부 호출처럼 트랜잭션 동기화가 없으면 즉시 best-effort 전송한다
        if (!synchronizationActive) {
            log.info("[Notification] 트랜잭션 동기화 없음 - 즉시 후속 발송. notificationId={}", notification.getId());
            sendRealtimeBestEffort(notification);
            sendFcmBestEffort(notification);
            return;
        }

        // 커밋 전에 메시지가 먼저 나가면 클라이언트 재조회 시 저장된 알림이 안 보일 수 있어 afterCommit으로 늦춘다
        log.info("[Notification] afterCommit 후속 발송 등록. notificationId={}", notification.getId());
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                log.info("[Notification] afterCommit 후속 발송 실행. notificationId={}", notification.getId());
                sendRealtimeBestEffort(notification);
                sendFcmBestEffort(notification);
            }
        });
    }

    private void sendRealtimeBestEffort(Notification notification) {
        try {
            // WebSocket 실패는 DB 알림 저장과 FCM 발송을 막지 않는다
            notificationRealtimeService.sendNewNotification(notification);
        } catch (RuntimeException exception) {
            log.warn("[Notification] WebSocket 전송 실패 - notificationId={}", notification.getId(), exception);
        }
    }

    private void sendFcmBestEffort(Notification notification) {
        try {
            // FCM 실패는 DB 알림 저장과 WebSocket 전송을 막지 않는다
            boolean sent = notificationPushService.send(notification.getId());
            log.info("[Notification] FCM best-effort 발송 완료. notificationId={}, sent={}",
                    notification.getId(), sent);
        } catch (RuntimeException exception) {
            log.warn("[Notification] FCM 전송 실패 - notificationId={}", notification.getId(), exception);
        }
    }
}
