package com.apten.apartmentcomplex.domain.entity;

import com.apten.apartmentcomplex.domain.enums.ComplexAdminRole;
import com.apten.common.entity.BaseEntity;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;

import java.time.LocalDateTime;

import lombok.*;

// 단지 관리자 소속 정보를 저장하는 엔티티
// 관리자 계정과 단지 운영 역할을 연결할 때 사용한다
@Entity
@Table(
        name = "complex_admin",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_complex_admin_user", columnNames = {"complex_id", "admin_user_id"})
        },
        indexes = {
                @Index(name = "idx_complex_admin_complex_id", columnList = "complex_id"),
                @Index(name = "idx_complex_admin_user_id", columnList = "admin_user_id"),
                @Index(name = "idx_complex_admin_role", columnList = "admin_role")
        }
)
@Getter
@Builder
@Setter
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

    // 단지 내 관리자 역할은 converter를 통해 DB에는 code로 저장된다
    @Column(name = "admin_role", nullable = false)
    private ComplexAdminRole adminRole;

    // 현재 소속 활성 여부
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    // 단지에 소속된 시각
    @Column(name = "assigned_at", nullable = false)
    private LocalDateTime assignedAt;

    // 단지 소속이 해제된 시각
    @Column(name = "unassigned_at")
    private LocalDateTime unassignedAt;

    // 관리자 역할이나 활성 상태를 갱신할 때 사용한다
    public void apply(String adminName, ComplexAdminRole adminRole, Boolean isActive, LocalDateTime unassignedAt) {
        this.adminName = adminName;
        this.adminRole = adminRole;
        this.isActive = isActive;
        this.unassignedAt = unassignedAt;
    }
}
