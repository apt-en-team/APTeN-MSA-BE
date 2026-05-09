package com.apten.auth.domain.repository;

import com.apten.auth.domain.entity.LoginHistory;
import org.springframework.data.jpa.repository.JpaRepository;

// 로그인 이력 저장소
public interface LoginHistoryRepository extends JpaRepository<LoginHistory, Long> {
}