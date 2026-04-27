package com.apten.apartmentcomplex.domain.entity;

import com.apten.common.entity.BaseEntity;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 단지 관리자 배정 정보를 저장하는 엔티티이다.
// 관리자 권한 종류는 Auth Service role이 관리하고 이 테이블은 배정 여부만 관리한다.
@Entity
@Table(
        name = "complex_admin",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_complex_admin_user", columnNames = {"complex_id", "admin_user_id"})
        },
        indexes = {
                @Index(name = "idx_complex_admin_complex_id", columnList = "complex_id"),
                @Index(name = "idx_complex_admin_user_id", columnList = "admin_user_id"),
                @Index(name = "idx_complex_admin_is_active", columnList = "is_active")
        }
)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplexAdmin extends BaseEntity {

    // 단지 관리자 내부 PK
    @Id
    @Tsid
    @Column(name = "id", nullable = false)
    private Long id;

    // 소속 단지 ID
    @Column(name = "complex_id", nullable = false)
    private Long complexId;

    // 관리자 사용자 ID
    @Column(name = "admin_user_id", nullable = false)
    private Long adminUserId;

    // 관리자 이름
    @Column(name = "admin_name", nullable = false)
    private String adminName;

    // 현재 소속 활성 여부이다.
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    // 단지에 소속된 시각이다.
    @Column(name = "assigned_at", nullable = false)
    private LocalDateTime assignedAt;

    // 단지 소속이 해제된 시각이다.
    @Column(name = "unassigned_at")
    private LocalDateTime unassignedAt;

    // 기존 비활성 배정을 다시 활성화할 때 사용한다.
    public void reassign(String adminName) {
        this.adminName = adminName;
        this.isActive = true;
        this.assignedAt = LocalDateTime.now();
        this.unassignedAt = null;
    }

    // 단지 관리자 배정을 해제할 때 사용한다.
    public void unassign() {
        this.isActive = false;
        this.unassignedAt = LocalDateTime.now();
    }
}
