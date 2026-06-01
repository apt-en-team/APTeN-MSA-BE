package com.apten.notification.domain.repository;

import com.apten.notification.domain.entity.NotificationSetting;
import com.apten.notification.domain.enums.NotificationCategory;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

// notification_setting의 기본 row 생성과 category별 ON/OFF 조회를 담당한다
public interface NotificationSettingRepository extends JpaRepository<NotificationSetting, Long> {

    // 사용자 전체 설정 조회
    List<NotificationSetting> findByUserId(Long userId);

    // 사용자 복수 카테고리 설정 조회
    List<NotificationSetting> findByUserIdAndCategoryIn(Long userId, Collection<NotificationCategory> categories);

    // 사용자 단일 카테고리 설정 조회
    Optional<NotificationSetting> findByUserIdAndCategory(Long userId, NotificationCategory category);
}
