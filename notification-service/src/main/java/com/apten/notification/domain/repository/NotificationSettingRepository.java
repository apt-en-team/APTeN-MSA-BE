package com.apten.notification.domain.repository;

import com.apten.notification.domain.entity.NotificationSetting;
import com.apten.notification.domain.enums.NotificationCategory;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

// notification_setting의 기본 row 생성과 category별 ON/OFF 조회를 담당한다
public interface NotificationSettingRepository extends JpaRepository<NotificationSetting, Long> {

    // 설정 화면 조회 시 사용자의 전체 category 설정을 가져온다
    List<NotificationSetting> findByUserId(Long userId);

    // 여러 category를 한 번에 조회해야 할 때 사용하는 확장 메서드
    List<NotificationSetting> findByUserIdAndCategoryIn(Long userId, Collection<NotificationCategory> categories);

    // 알림 생성 직전에 해당 category가 OFF인지 확인한다
    Optional<NotificationSetting> findByUserIdAndCategory(Long userId, NotificationCategory category);
}
