package com.apten.board.domain.repository;

import com.apten.board.domain.entity.UserCache;
import org.springframework.data.jpa.repository.JpaRepository;

// board-service의 user cache 저장소
public interface UserCacheRepository extends JpaRepository<UserCache, Long> {
}
