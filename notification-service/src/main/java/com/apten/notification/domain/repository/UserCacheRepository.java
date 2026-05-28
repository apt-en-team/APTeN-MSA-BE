package com.apten.notification.domain.repository;

import com.apten.notification.domain.entity.UserCache;
import com.apten.notification.domain.enums.UserCacheRole;
import com.apten.notification.domain.enums.UserCacheStatus;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

// notification-service의 user cache 저장소
public interface UserCacheRepository extends JpaRepository<UserCache, Long> {

    // 관리자 알림 대상은 같은 단지의 ACTIVE ADMIN/MANAGER만 조회한다
    List<UserCache> findByComplexIdAndRoleInAndStatus(
            Long complexId,
            Collection<UserCacheRole> roles,
            UserCacheStatus status
    );
}
