package com.apten.auth.domain.entity;

import com.apten.auth.domain.enums.SignupType;
import com.apten.auth.domain.enums.UserRole;
import com.apten.auth.domain.enums.UserStatus;
import com.apten.common.entity.BaseEntity;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 회원 계정 원본 엔티티 — 로그인, 회원가입, 계정 상태 관리
// MASTER / MANAGER / ADMIN / USER 4단계 역할 체계를 지원한다
// MASTER는 complexId null 허용, MANAGER/ADMIN은 admin_profile 테이블에 상세 정보 저장
@Entity
@Table(name = "user")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity {

    // 로그인 실패 최대 허용 횟수
    private static final int MAX_LOGIN_FAIL_COUNT = 5;

    // 회원 내부 PK
    @Id
    @Tsid
    @Column(name = "id", nullable = false)
    private Long id;

    // 소속 단지 ID — MASTER는 null 허용, MANAGER/ADMIN은 admin_profile에서 관리
    @Column(name = "complex_id")
    private Long complexId;

    // 로그인 이메일
    @Column(name = "email", nullable = false, length = 100, unique = true)
    private String email;

    // 비밀번호 해시
    @Column(name = "password_hash", length = 255)
    private String passwordHash;

    // 사용자 이름 — MANAGER/ADMIN은 MASTER/MANAGER가 생성 시 입력
    @Column(name = "name", length = 50)
    private String name;

    // 휴대폰 번호 — USER는 SMS 인증 필수, MANAGER/ADMIN은 선택
    @Column(name = "phone", length = 20)
    private String phone;

    // 생년월일 — USER 전용 (MANAGER/ADMIN은 null)
    @Column(name = "birth_date")
    private LocalDate birthDate;

    // 동 정보 — USER 전용 (MANAGER/ADMIN은 null)
    @Column(name = "building", length = 10)
    private String building;

    // 호 정보 — USER 전용 (MANAGER/ADMIN은 null)
    @Column(name = "unit", length = 10)
    private String unit;

    // 사용자 권한은 converter를 통해 DB에는 code로 저장된다
    // MASTER / MANAGER / ADMIN / USER
    @Column(name = "role", nullable = false, length = 20)
    private UserRole role;

    // 회원 상태는 converter를 통해 DB에는 code로 저장된다
    @Column(name = "status", nullable = false, length = 20)
    private UserStatus status;

    // 가입 방식은 converter를 통해 DB에는 code로 저장된다
    // MANAGER/ADMIN은 CREATED_BY_ADMIN
    @Column(name = "signup_type", nullable = false, length = 20)
    private SignupType signupType;

    // 휴대폰 인증 여부
    @Column(name = "is_phone_verified", nullable = false)
    private Boolean isPhoneVerified;

    // 이메일 인증 여부
    @Column(name = "is_email_verified", nullable = false)
    private Boolean isEmailVerified;

    // 로그인 실패 횟수
    @Column(name = "login_fail_count", nullable = false)
    private Integer loginFailCount;

    // 계정 잠금 시각
    @Column(name = "locked_at")
    private LocalDateTime lockedAt;

    // 마지막 로그인 시각
    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    // 소프트 삭제 여부
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    // 삭제 시각
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // 회원 기본 정보 갱신
    public void apply(
            Long complexId,
            String email,
            String passwordHash,
            String name,
            String phone,
            LocalDate birthDate,
            String building,
            String unit,
            UserRole role,
            UserStatus status,
            SignupType signupType
    ) {
        this.complexId = complexId;
        this.email = email;
        this.passwordHash = passwordHash;
        this.name = name;
        this.phone = phone;
        this.birthDate = birthDate;
        this.building = building;
        this.unit = unit;
        this.role = role;
        this.status = status;
        this.signupType = signupType;
    }

    // 로그인 실패 횟수 증가 — 5회 이상이면 계정 잠금 처리
    public void incrementLoginFailCount() {
        this.loginFailCount++;
        if (this.loginFailCount >= MAX_LOGIN_FAIL_COUNT) {
            this.status = UserStatus.LOCKED;
            this.lockedAt = LocalDateTime.now();
        }
    }

    // 로그인 성공 시 실패 횟수 초기화 / 마지막 로그인 시각 갱신
    public void resetLoginFailCount() {
        this.loginFailCount = 0;
        this.lockedAt = null;
        this.lastLoginAt = LocalDateTime.now();
    }

    // 비밀번호 변경 — 새 해시값으로 교체
    public void changePassword(String newPasswordHash) {
        this.passwordHash = newPasswordHash;
    }
}