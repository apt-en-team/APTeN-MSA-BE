package com.apten.notification.application.service;

import com.apten.common.exception.BusinessException;
import com.apten.common.exception.CommonErrorCode;
import com.apten.notification.application.model.request.NotificationSettingPatchReq;
import com.apten.notification.application.model.request.NotificationSettingReq;
import com.apten.notification.application.model.response.NotificationSettingGetRes;
import com.apten.notification.application.model.response.NotificationSettingItemRes;
import com.apten.notification.application.model.response.NotificationSettingPatchRes;
import com.apten.notification.domain.entity.NotificationSetting;
import com.apten.notification.domain.entity.UserCache;
import com.apten.notification.domain.enums.NotificationCategory;
import com.apten.notification.domain.repository.NotificationSettingRepository;
import com.apten.notification.domain.repository.UserCacheRepository;
import com.apten.notification.exception.NotificationErrorCode;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 사용자별 알림 카테고리 ON/OFF 설정을 관리하는 응용 서비스
@Service
@RequiredArgsConstructor
public class NotificationSettingService {

    private final NotificationSettingRepository notificationSettingRepository;
    private final UserCacheRepository userCacheRepository;
    private final NotificationSettingInitService notificationSettingInitService;

    // 알림 설정 조회 (누락 row 기본 ON 보정)
    @Transactional
    public NotificationSettingGetRes getSettings(Long userId, Long complexIdHeader) {
        // 설정 조회는 없는 category row를 기본 ON으로 보정한 뒤 반환한다
        Long complexId = resolveComplexId(userId, complexIdHeader);
        List<NotificationSetting> settings = ensureDefaultRows(userId, complexId);
        return NotificationSettingGetRes.builder()
                .settings(toItems(settings))
                .build();
    }

    // 알림 설정 변경
    @Transactional
    public NotificationSettingPatchRes updateSettings(Long userId, Long complexIdHeader, NotificationSettingPatchReq request) {
        // 요청 자체가 비어 있으면 어떤 category를 바꿀지 알 수 없으므로 거부한다
        if (request == null || request.getSettings() == null) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        Long complexId = resolveComplexId(userId, complexIdHeader);
        // 먼저 기본 row를 모두 만들어 두면 이후에는 category별 changeEnabled만 수행하면 된다
        Map<NotificationCategory, NotificationSetting> settingMap = toMap(ensureDefaultRows(userId, complexId));

        for (NotificationSettingReq item : request.getSettings()) {
            if (item == null || item.getEnabled() == null) {
                throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
            }
            NotificationCategory category = parseCategory(item.getCategory());
            settingMap.get(category).changeEnabled(item.getEnabled());
        }

        List<NotificationSetting> saved = notificationSettingRepository.saveAll(settingMap.values());
        return NotificationSettingPatchRes.builder()
                .updated(true)
                .settings(toItems(saved))
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // 카테고리 알림 활성 여부 확인
    @Transactional(readOnly = true)
    public boolean isEnabled(Long userId, NotificationCategory category) {
        // SYSTEM처럼 category가 없거나 설정 row가 없으면 정책상 기본 ON이다
        if (category == null) {
            return true;
        }
        return notificationSettingRepository.findByUserIdAndCategory(userId, category)
                .map(setting -> Boolean.TRUE.equals(setting.getEnabled()))
                .orElse(true);
    }

    private List<NotificationSetting> ensureDefaultRows(Long userId, Long complexId) {
        try {
            return notificationSettingInitService.createMissingDefaultRows(userId, complexId);
        } catch (DataIntegrityViolationException exception) {
            // 동시 첫 진입 시 같은 기본 설정 row를 동시에 만들 수 있으므로 unique 충돌 시 재조회한다
            return notificationSettingRepository.findByUserId(userId);
        }
    }

    private Map<NotificationCategory, NotificationSetting> toMap(List<NotificationSetting> settings) {
        Map<NotificationCategory, NotificationSetting> settingMap = new EnumMap<>(NotificationCategory.class);
        for (NotificationSetting setting : settings) {
            settingMap.put(setting.getCategory(), setting);
        }
        return settingMap;
    }

    private List<NotificationSettingItemRes> toItems(List<NotificationSetting> settings) {
        // 응답은 enum 선언 순서대로 내려 프론트 설정 화면 순서를 안정적으로 유지한다
        Map<NotificationCategory, NotificationSetting> settingMap = toMap(settings);
        return Arrays.stream(NotificationCategory.values())
                .map(category -> {
                    NotificationSetting setting = settingMap.get(category);
                    return NotificationSettingItemRes.builder()
                            .category(category.name())
                            .categoryCode(category.getCode())
                            .categoryValue(category.getValue())
                            .enabled(setting == null || Boolean.TRUE.equals(setting.getEnabled()))
                            .build();
                })
                .toList();
    }

    private NotificationCategory parseCategory(String category) {
        try {
            return NotificationCategory.from(category);
        } catch (RuntimeException exception) {
            throw new BusinessException(NotificationErrorCode.INVALID_NOTIFICATION_CATEGORY);
        }
    }

    private Long resolveComplexId(Long userId, Long complexIdHeader) {
        // Gateway 헤더가 없으면 user_cache에서 사용자의 단지 ID를 보완한다
        if (userId == null) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }
        if (complexIdHeader != null) {
            return complexIdHeader;
        }
        return userCacheRepository.findById(userId)
                .map(UserCache::getComplexId)
                .orElseThrow(() -> new BusinessException(NotificationErrorCode.USER_CACHE_NOT_FOUND));
    }
}
