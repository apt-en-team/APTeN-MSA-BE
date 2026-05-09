package com.apten.board.domain.repository;

import com.apten.board.domain.entity.UserCache;
import com.apten.board.domain.enums.UserCacheStatus;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

// board-service의 user cache 저장소
public interface UserCacheRepository extends JpaRepository<UserCache, Long> {

    // 활성 사용자 검증에 사용한다.
    Optional<UserCache> findByIdAndStatus(Long id, UserCacheStatus status);
}
