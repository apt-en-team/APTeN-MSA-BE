package com.apten.board.domain.entity;

import com.apten.board.domain.enums.HouseholdMemberRole;
import com.apten.common.entity.BaseEntity;
import com.apten.common.kafka.payload.HouseholdMemberEventPayload;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 세대주 투표 권한 검증에 사용하는 세대원 캐시 엔티티이다.
@Entity
@Table(
        name = "household_member_cache",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_household_member_cache_user", columnNames = "user_id")
        },
        indexes = {
                @Index(name = "idx_household_member_cache_household_id", columnList = "household_id"),
                @Index(name = "idx_household_member_cache_role", columnList = "role"),
                @Index(name = "idx_household_member_cache_is_active", columnList = "is_active")
        }
)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HouseholdMemberCache extends BaseEntity {

    // Household Service 원본 세대원 ID이다.
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    // 세대 ID이다.
    @Column(name = "household_id", nullable = false)
    private Long householdId;

    // 사용자 ID이다.
    @Column(name = "user_id", nullable = false)
    private Long userId;

    // 단지 ID이다.
    @Column(name = "complex_id", nullable = false)
    private Long complexId;

    // 세대 내 역할이다.
    @Builder.Default
    @Column(name = "role", nullable = false, length = 20)
    private HouseholdMemberRole role = HouseholdMemberRole.MEMBER;

    // 활성 여부이다.
    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // 수신한 세대원 이벤트를 캐시에 반영한다.
    public void apply(HouseholdMemberEventPayload payload, Long complexId) {
        this.id = payload.getHouseholdMemberId();
        this.householdId = payload.getHouseholdId();
        this.userId = payload.getUserId();
        this.complexId = complexId;
        if (payload.getMemberRole() != null) {
            this.role = HouseholdMemberRole.valueOf(payload.getMemberRole());
        }
        this.isActive = !"REMOVED".equals(payload.getStatus());
    }
}
