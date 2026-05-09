package com.apten.auth.domain.entity;

import com.apten.auth.domain.enums.UserStatus;
import com.apten.common.entity.BaseEntity;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// USER 계정의 세대 상세 정보를 관리하는 엔티티
// user 테이블은 로그인/인증 공통 정보만 담고 단지/동/호수 등 입주민 전용 정보는 여기서 분리해서 관리한다
@Entity
@Table(name = "resident_profile")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResidentProfile extends BaseEntity {

    @Id
    @Tsid
    @Column(name = "id", nullable = false)
    private Long id;

    // user 테이블 참조 — 1:1 매핑, 같은 서비스 내 FK 허용
    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    // 소속 단지 ID — 타 서비스 FK 참조 금지, 논리 참조
    @Column(name = "complex_id", nullable = false)
    private Long complexId;

    // 생년월일 — 세대 매칭 시 사용
    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    // 동 번호
    @Column(name = "building", nullable = false, length = 10)
    private String building;

    // 호수
    @Column(name = "unit", nullable = false, length = 10)
    private String unit;

    // 입주민 상태 — PENDING(세대 매칭 대기), ACTIVE(승인), REJECTED(거절), DELETED(탈퇴)
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private UserStatus status;

    // 소프트 삭제 여부
    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private boolean isDeleted = false;

    // household-service에서 세대 승인 완료 후 ACTIVE로 변경
    public void activate() {
        this.status = UserStatus.ACTIVE;
    }

    // household-service에서 세대 매칭 거절 시 REJECTED로 변경
    public void reject() {
        this.status = UserStatus.REJECTED;
    }
}