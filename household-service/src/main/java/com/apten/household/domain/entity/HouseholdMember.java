package com.apten.household.domain.entity;

import com.apten.common.entity.BaseEntity;
import com.apten.household.domain.enums.HouseholdMemberRole;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 세대주와 세대원을 저장하는 엔티티
// 서비스 간 연관관계 대신 ID 값만 유지한다
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "household_member",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_household_member_user_id", columnNames = "user_id")
        },
        indexes = {
                @Index(name = "idx_household_member_household_id", columnList = "household_id"),
                @Index(name = "idx_household_member_role", columnList = "role"),
                @Index(name = "idx_household_member_active", columnList = "is_active")
        }
)
public class HouseholdMember extends BaseEntity {

    // 세대원 PK
    @Id
    @Tsid
    private Long id;

    // 세대 ID
    @Column(name = "household_id", nullable = false)
    private Long householdId;

    // 사용자 ID
    @Column(name = "user_id", nullable = false)
    private Long userId;

    // 세대원 역할
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private HouseholdMemberRole role;

    // 활성 여부
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    // 세대원 정보를 수정한다
    public void update(HouseholdMemberRole role, Boolean isActive) {
        this.role = role;
        this.isActive = isActive;
    }

    // 세대원 활성 상태를 바꾼다
    public void changeActive(Boolean isActive) {
        this.isActive = isActive;
    }
}
