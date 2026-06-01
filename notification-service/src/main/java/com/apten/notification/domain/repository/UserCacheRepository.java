package com.apten.notification.domain.repository;

import com.apten.notification.domain.entity.UserCache;
import com.apten.notification.domain.enums.UserCacheRole;
import com.apten.notification.domain.enums.UserCacheStatus;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

// notification-service의 user cache 저장소
public interface UserCacheRepository extends JpaRepository<UserCache, Long> {

    // 단지 내 역할 + 상태 기준 사용자 목록 조회
    List<UserCache> findByComplexIdAndRoleInAndStatus(
            Long complexId,
            Collection<UserCacheRole> roles,
            UserCacheStatus status
    );
}
