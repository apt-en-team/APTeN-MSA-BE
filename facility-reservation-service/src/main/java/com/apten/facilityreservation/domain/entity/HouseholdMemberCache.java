package com.apten.facilityreservation.domain.entity;

import com.apten.common.entity.BaseEntity;
import com.apten.common.kafka.payload.HouseholdMemberEventPayload;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// facility-reservation-service가 세대 구성원 참조 데이터를 로컬 캐시 테이블로 유지하는 엔티티
@Entity
@Getter
@NoArgsConstructor
@Table(name = "household_member_cache")
public class HouseholdMemberCache extends BaseEntity {

    // 원본 세대 구성원 ID를 그대로 캐시 PK로 사용한다
    @Id
    private Long householdMemberId;

    // 연결된 세대 식별자
    private Long householdId;

    // 연결된 사용자 식별자
    private Long userId;

    // 세대 내 역할
    private String memberRole;

    // soft delete를 포함한 상태값
    private String status;

    // 대표 구성원 여부
    private boolean isPrimary;

    @Builder
    private HouseholdMemberCache(
            Long householdMemberId,
            Long householdId,
            Long userId,
            String memberRole,
            String status,
            boolean isPrimary
    ) {
        this.householdMemberId = householdMemberId;
        this.householdId = householdId;
        this.userId = userId;
        this.memberRole = memberRole;
        this.status = status;
        this.isPrimary = isPrimary;
    }

    // 같은 이벤트를 다시 받아도 같은 상태가 되도록 필드를 덮어쓴다
    public void apply(HouseholdMemberEventPayload payload) {
        this.householdMemberId = payload.getHouseholdMemberId();
        this.householdId = payload.getHouseholdId();
        this.userId = payload.getUserId();
        this.memberRole = payload.getMemberRole();
        this.status = payload.getStatus();
        this.isPrimary = payload.isPrimary();
    }
}
