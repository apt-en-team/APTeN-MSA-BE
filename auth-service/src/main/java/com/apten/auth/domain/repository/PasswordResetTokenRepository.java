package com.apten.auth.domain.repository;

import com.apten.auth.domain.entity.PasswordResetToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

// 비밀번호 재설정 토큰 저장소
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    // 토큰 해시로 조회 — 링크 클릭 시 검증에 사용
    Optional<PasswordResetToken> findByTokenHash(String tokenHash);
}