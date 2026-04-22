package com.apten.board.domain.entity;

import com.apten.board.domain.enums.HouseholdMemberRole;
import com.apten.common.entity.BaseEntity;
import com.apten.common.kafka.payload.HouseholdMemberEventPayload;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 세대주 권한 확인용 세대원 캐시 엔티티
// Household Service 이벤트를 받아 세대 소속과 역할을 로컬에 유지한다
@Entity
@Getter
@NoArgsConstructor
@Table(
        name = "household_member_cache",
        uniqueConstraints = @UniqueConstraint(name = "uk_household_member_cache_user", columnNames = "user_id")
)
public class HouseholdMemberCache extends BaseEntity {

    // 원본 세대 구성원 ID를 그대로 캐시 PK로 사용한다
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    // 연결된 세대 식별자
    @Column(name = "household_id", nullable = false)
    private Long householdId;

    // 연결된 사용자 식별자
    @Column(name = "user_id", nullable = false)
    private Long userId;

    // 소속 단지 ID
    @Column(name = "complex_id", nullable = false)
    private Long complexId;

    // 세대 내 역할
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private HouseholdMemberRole role;

    // 현재 활성 여부
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Builder
    private HouseholdMemberCache(
            Long id,
            Long householdId,
            Long userId,
            Long complexId,
            HouseholdMemberRole role,
            Boolean isActive
    ) {
        this.id = id;
        this.householdId = householdId;
        this.userId = userId;
        this.complexId = complexId;
        this.role = role;
        this.isActive = isActive;
    }

    // 같은 이벤트를 다시 받아도 같은 상태가 되도록 필드를 덮어쓴다
    public void apply(HouseholdMemberEventPayload payload) {
        this.id = payload.getHouseholdMemberId();
        this.householdId = payload.getHouseholdId();
        this.userId = payload.getUserId();
        this.role = payload.getMemberRole() == null ? null : HouseholdMemberRole.valueOf(payload.getMemberRole());
        this.isActive = !"REMOVED".equals(payload.getStatus());
    }
}
