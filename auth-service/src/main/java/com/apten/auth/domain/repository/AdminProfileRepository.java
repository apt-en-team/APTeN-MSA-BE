package com.apten.auth.domain.repository;

import com.apten.auth.domain.entity.AdminProfile;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

// MANAGER / ADMIN 단지 소속 정보 저장소
public interface AdminProfileRepository extends JpaRepository<AdminProfile, Long> {

    // userId로 admin_profile 조회 — 로그인 시 complexId 확인에 사용
    Optional<AdminProfile> findByUserId(Long userId);

    //추가 같은 단지의 관리자 프로필 조회에 사용한다.
    Optional<AdminProfile> findByUserIdAndComplexId(Long userId, Long complexId);
}
