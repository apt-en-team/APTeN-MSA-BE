package com.apten.notification.application.service;

import com.apten.notification.domain.entity.NotificationSetting;
import com.apten.notification.domain.enums.NotificationCategory;
import com.apten.notification.domain.repository.NotificationSettingRepository;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

// self-invocation 문제를 피하기 위해 REQUIRES_NEW 트랜잭션 로직을 별도 Bean으로 분리한다
@Service
@RequiredArgsConstructor
public class NotificationSettingInitService {

    private final NotificationSettingRepository notificationSettingRepository;

    // 누락된 기본 설정 row 생성 (REQUIRES_NEW 독립 트랜잭션)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<NotificationSetting> createMissingDefaultRows(Long userId, Long complexId) {
        // 조회 API 호출 시 빠진 category row를 생성해 이후 변경/조회 흐름을 단순하게 유지한다
        Map<NotificationCategory, NotificationSetting> settingMap =
                toMap(notificationSettingRepository.findByUserId(userId));

        List<NotificationSetting> missingSettings = Arrays.stream(NotificationCategory.values())
                .filter(category -> !settingMap.containsKey(category))
                .map(category -> NotificationSetting.builder()
                        .userId(userId)
                        .complexId(complexId)
                        .category(category)
                        .enabled(true)
                        .build())
                .toList();

        if (missingSettings.isEmpty()) {
            return settingMap.values().stream().toList();
        }

        notificationSettingRepository.saveAllAndFlush(missingSettings);
        return notificationSettingRepository.findByUserId(userId);
    }

    private Map<NotificationCategory, NotificationSetting> toMap(List<NotificationSetting> settings) {
        Map<NotificationCategory, NotificationSetting> settingMap = new EnumMap<>(NotificationCategory.class);
        for (NotificationSetting setting : settings) {
            settingMap.put(setting.getCategory(), setting);
        }
        return settingMap;
    }
}
