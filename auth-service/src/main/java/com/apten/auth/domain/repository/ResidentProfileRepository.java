package com.apten.auth.domain.repository;

import com.apten.auth.domain.entity.ResidentProfile;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

// 입주민 프로필 저장소
public interface ResidentProfileRepository extends JpaRepository<ResidentProfile, Long> {

    // userId로 resident_profile 조회 — 로그인 시 complexId 확인에 사용
    Optional<ResidentProfile> findByUserId(Long userId);
}