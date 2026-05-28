package com.apten.notification.domain.repository;

import com.apten.notification.domain.entity.FcmToken;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

// FCM 토큰 준비 구현에서 토큰 원문과 사용자 소유권 기준 조회를 맡는다
public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {

    // 토큰 원문은 전역 unique라 중복 등록 여부를 먼저 확인할 수 있다
    Optional<FcmToken> findByTokenValue(String tokenValue);

    // 해제와 갱신은 본인 토큰만 처리해야 하므로 userId와 tokenValue를 함께 본다
    Optional<FcmToken> findByUserIdAndTokenValue(Long userId, String tokenValue);

    // HTTPS 활성화 후 실제 FCM 발송 대상 토큰을 조회할 때 사용한다
    List<FcmToken> findByUserIdAndIsActive(Long userId, Boolean isActive);
}
