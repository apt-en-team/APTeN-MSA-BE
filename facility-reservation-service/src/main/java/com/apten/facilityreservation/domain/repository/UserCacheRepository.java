package com.apten.facilityreservation.domain.repository;

import com.apten.facilityreservation.domain.entity.UserCache;
import com.apten.facilityreservation.domain.enums.UserCacheRole;
import com.apten.facilityreservation.domain.enums.UserCacheStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

// 사용자 캐시 저장소이다.
public interface UserCacheRepository extends JpaRepository<UserCache, Long> {

    // 특정 단지의 활성 사용자 목록을 조회할 때 사용한다.
    List<UserCache> findByComplexIdAndStatus(Long complexId, UserCacheStatus status);

    // 특정 권한의 사용자 존재 여부를 확인할 때 사용한다.
    Optional<UserCache> findByIdAndRoleAndStatus(Long id, UserCacheRole role, UserCacheStatus status);
}
