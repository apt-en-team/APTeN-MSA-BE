package com.apten.parkingvehicle.domain.entity;

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

// 세대원 userId를 세대로 해석하기 위한 세대 구성원 캐시 엔티티
// 차량과 방문차량 등 세대 단위 조회에서 공용으로 재사용한다
@Entity
@Table(
        name = "household_member_cache",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_household_member_cache_household_user", columnNames = {"household_id", "user_id"})
        },
        indexes = {
                @Index(name = "idx_household_member_cache_user_id", columnList = "user_id"),
                @Index(name = "idx_household_member_cache_household_id", columnList = "household_id"),
                @Index(name = "idx_household_member_cache_active", columnList = "is_active")
        }
)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HouseholdMemberCache extends BaseEntity {

    // 원본 세대 구성원 PK
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    // 소속 세대 ID
    @Column(name = "household_id", nullable = false)
    private Long householdId;

    // 연결된 사용자 ID
    @Column(name = "user_id", nullable = false)
    private Long userId;

    // 세대 내 역할 (HEAD/MEMBER 원본 값)
    @Column(name = "member_role", nullable = false, length = 20)
    private String memberRole;

    // 대표 구성원 여부
    @Builder.Default
    @Column(name = "is_primary", nullable = false)
    private Boolean isPrimary = false;

    // 활성 여부 (조회 시 활성 구성원만 사용)
    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // 세대원 이벤트로 캐시 내용을 갱신한다
    public void apply(HouseholdMemberEventPayload payload) {
        this.id = payload.getHouseholdMemberId();
        this.householdId = payload.getHouseholdId();
        this.userId = payload.getUserId();
        this.memberRole = payload.getMemberRole();
        this.isPrimary = payload.isPrimary();
        // 활성 외 상태(탈퇴/거절 등)는 모두 비활성으로 보정해 이벤트 순서 꼬임에도 안전하게 처리
        this.isActive = "ACTIVE".equals(payload.getStatus());
    }
}
