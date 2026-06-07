package com.apten.notification.domain.repository;

import com.apten.notification.domain.entity.FcmToken;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

// FCM 토큰 준비 구현에서 토큰 원문과 사용자 소유권 기준 조회를 맡는다
public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {

    // 토큰 원문 기준 조회
    Optional<FcmToken> findByTokenValue(String tokenValue);

    // 사용자 + 토큰 원문 기준 조회
    Optional<FcmToken> findByUserIdAndTokenValue(Long userId, String tokenValue);

    // 사용자 활성 토큰 목록 조회
    List<FcmToken> findByUserIdAndIsActive(Long userId, Boolean isActive);

    // 사용자 활성 토큰 오래된 순 조회 (초과분 비활성화용)
    List<FcmToken> findByUserIdAndIsActiveTrueOrderByLastUsedAtAsc(Long userId);
}
