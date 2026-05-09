package com.apten.auth.domain.repository;

import com.apten.auth.domain.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

// 회원 기본 저장과 조회를 맡는 저장소
// 인증 응용 서비스는 이 저장소를 통해 회원 원본을 다룬다
public interface UserRepository extends JpaRepository<User, Long> {

    // 이메일 기준으로 회원을 찾을 때 사용한다
    Optional<User> findByEmail(String email);
}
