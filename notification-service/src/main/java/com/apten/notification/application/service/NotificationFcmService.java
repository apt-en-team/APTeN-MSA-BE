package com.apten.notification.application.service;

import com.apten.common.exception.BusinessException;
import com.apten.common.exception.CommonErrorCode;
import com.apten.notification.application.model.request.NotificationFcmTokenDeleteReq;
import com.apten.notification.application.model.request.NotificationFcmTokenPatchReq;
import com.apten.notification.application.model.request.NotificationFcmTokenPostReq;
import com.apten.notification.application.model.response.NotificationFcmTokenDeleteRes;
import com.apten.notification.application.model.response.NotificationFcmTokenPatchRes;
import com.apten.notification.application.model.response.NotificationFcmTokenPostRes;
import com.apten.notification.domain.entity.FcmToken;
import com.apten.notification.domain.enums.FcmDeviceType;
import com.apten.notification.domain.repository.FcmTokenRepository;
import com.apten.notification.exception.NotificationErrorCode;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// FCM 토큰 등록과 해제, 갱신 흐름을 담당하는 응용 서비스
@Service
@RequiredArgsConstructor
public class NotificationFcmService {

    private final FcmTokenRepository fcmTokenRepository;

    @Transactional
    public NotificationFcmTokenPostRes registerFcmToken(Long userId, Long complexId, NotificationFcmTokenPostReq request) {
        // Gateway가 전달한 사용자/단지 컨텍스트를 기준으로 토큰 소유자를 결정한다
        validateUserContext(userId, complexId);
        validateToken(request == null ? null : request.getToken());
        LocalDateTime now = LocalDateTime.now();

        // 같은 토큰이 이미 있으면 새 row를 만들지 않고 소유자와 기기 정보를 최신화한다
        FcmToken token = fcmTokenRepository.findByTokenValue(request.getToken())
                .orElseGet(() -> FcmToken.builder()
                        .userId(userId)
                        .complexId(complexId)
                        .tokenValue(request.getToken())
                        .deviceType(parseDeviceType(request.getDeviceType()))
                        .browserType(request.getBrowserType())
                        .appVersion(request.getAppVersion())
                        .isActive(true)
                        .lastUsedAt(now)
                        .build());

        token.updateRegistration(
                userId,
                complexId,
                request.getToken(),
                parseDeviceType(request.getDeviceType()),
                request.getBrowserType(),
                request.getAppVersion(),
                null,
                now
        );
        fcmTokenRepository.save(token);

        return NotificationFcmTokenPostRes.builder()
                .tokenRegistered(true)
                .updatedAt(now)
                .build();
    }

    @Transactional
    public NotificationFcmTokenDeleteRes deleteFcmToken(Long userId, NotificationFcmTokenDeleteReq request) {
        // 다른 사용자의 토큰을 끄지 않도록 userId와 tokenValue를 함께 확인한다
        validateUserId(userId);
        validateToken(request == null ? null : request.getToken());
        FcmToken token = fcmTokenRepository.findByUserIdAndTokenValue(userId, request.getToken())
                .orElseThrow(() -> new BusinessException(NotificationErrorCode.FCM_TOKEN_NOT_FOUND));
        token.deactivate();

        return NotificationFcmTokenDeleteRes.builder()
                .tokenDeleted(true)
                .build();
    }

    @Transactional
    public NotificationFcmTokenPatchRes updateFcmToken(Long userId, Long complexId, NotificationFcmTokenPatchReq request) {
        // 토큰 갱신도 현재 로그인 사용자와 단지 컨텍스트를 기준으로 처리한다
        validateUserContext(userId, complexId);
        if (request == null) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }
        validateToken(request.getOldToken());
        validateToken(request.getNewToken());
        LocalDateTime now = LocalDateTime.now();
        FcmToken oldToken = fcmTokenRepository.findByUserIdAndTokenValue(userId, request.getOldToken())
                .orElseThrow(() -> new BusinessException(NotificationErrorCode.FCM_TOKEN_NOT_FOUND));

        // 새 토큰이 이미 다른 row에 있으면 그 row를 재사용하고 기존 토큰은 비활성화한다
        FcmToken targetToken = fcmTokenRepository.findByTokenValue(request.getNewToken())
                .filter(existing -> !existing.getId().equals(oldToken.getId()))
                .orElse(oldToken);

        if (!targetToken.getId().equals(oldToken.getId())) {
            oldToken.deactivate();
        }

        targetToken.updateRegistration(
                userId,
                complexId,
                request.getNewToken(),
                parseDeviceType(request.getDeviceType()),
                request.getBrowserType(),
                request.getAppVersion(),
                null,
                now
        );

        return NotificationFcmTokenPatchRes.builder()
                .tokenUpdated(true)
                .updatedAt(now)
                .build();
    }

    private void validateUserContext(Long userId, Long complexId) {
        validateUserId(userId);
        if (complexId == null) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }
    }

    private void validateUserId(Long userId) {
        if (userId == null) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }
    }

    private void validateToken(String token) {
        if (token == null || token.isBlank()) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }
    }

    private FcmDeviceType parseDeviceType(String deviceType) {
        // FCM 준비 API는 deviceType을 선택값으로 받아 WEB/MOBILE 코드값만 저장한다
        if (deviceType == null || deviceType.isBlank()) {
            return null;
        }
        try {
            return FcmDeviceType.from(deviceType);
        } catch (RuntimeException exception) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }
    }
}
