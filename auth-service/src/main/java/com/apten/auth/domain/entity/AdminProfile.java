package com.apten.auth.domain.entity;

import com.apten.auth.domain.enums.UserRole;
import com.apten.common.entity.BaseEntity;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// MANAGER / ADMIN 계정의 단지 소속 정보를 관리하는 엔티티
// user 테이블은 로그인 정보만 가지고, 단지 관련 상세 정보는 여기서 관리한다
@Entity
@Table(name = "admin_profile")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminProfile extends BaseEntity {

    // PK
    @Id
    @Tsid
    @Column(name = "id", nullable = false)
    private Long id;

    // user 테이블 참조 — 같은 서비스 내 참조이므로 FK 허용
    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    // 담당 단지 ID — 타 서비스 FK 참조 금지, 스냅샷 방식으로 저장
    @Column(name = "complex_id", nullable = false)
    private Long complexId;

    // 관리자 역할 구분 — MANAGER 또는 ADMIN만 허용
    @Column(name = "admin_role", nullable = false, length = 20)
    private UserRole adminRole;

    // 이 계정을 생성한 사람의 user_id
    // MANAGER이면 → MASTER의 userId
    // ADMIN이면 → MANAGER의 userId
    @Column(name = "created_by", nullable = false)
    private Long createdBy;
}