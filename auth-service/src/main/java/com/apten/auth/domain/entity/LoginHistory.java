package com.apten.auth.domain.entity;

import com.apten.auth.domain.enums.LoginResult;
import com.apten.common.entity.BaseEntity;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 로그인 결과 이력을 저장하는 엔티티
// 로그인 성공과 실패 기록을 남길 때 사용한다
@Entity
@Table(name = "login_history")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginHistory extends BaseEntity {

    // 로그인 이력 내부 PK
    @Id
    @Tsid
    @Column(name = "id", nullable = false)
    private Long id;

    // 로그인 대상 회원 ID
    @Column(name = "user_id")
    private Long userId;

    // 시도한 이메일
    @Column(name = "email", nullable = false, length = 100)
    private String email;

    // 로그인 결과
    @Enumerated(EnumType.STRING)
    @Column(name = "result", nullable = false, length = 20)
    private LoginResult result;

    // 로그인 결과를 갱신할 때 사용한다
    public void apply(Long userId, String email, LoginResult result) {
        this.userId = userId;
        this.email = email;
        this.result = result;
    }
}
