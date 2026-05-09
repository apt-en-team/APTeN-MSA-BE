package com.apten.parkingvehicle.domain.entity;

import com.apten.common.entity.BaseEntity;
import com.apten.common.kafka.payload.HouseholdEventPayload;
import com.apten.parkingvehicle.domain.enums.HouseholdCacheStatus;
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

// 차량 등록 검증과 조회에 사용하는 세대 캐시 엔티티
@Entity
@Table(
        name = "household_cache",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_household_cache_complex_building_unit", columnNames = {"complex_id", "building", "unit"})
        },
        indexes = {
                @Index(name = "idx_household_cache_complex_id", columnList = "complex_id"),
                @Index(name = "idx_household_cache_status", columnList = "status")
        }
)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HouseholdCache extends BaseEntity {

    // 원본 세대 PK
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    // 소속 단지 ID
    @Column(name = "complex_id", nullable = false)
    private Long complexId;

    // 동 정보
    @Column(name = "building", nullable = false, length = 10)
    private String building;

    // 호 정보
    @Column(name = "unit", nullable = false, length = 10)
    private String unit;

    // 세대 상태
    @Builder.Default
    @Column(name = "status", nullable = false, length = 20)
    private HouseholdCacheStatus status = HouseholdCacheStatus.OCCUPIED;

    // 세대주 사용자 ID
    @Column(name = "head_user_id")
    private Long headUserId;

    // 세대 이벤트로 캐시 내용을 갱신한다
    public void apply(HouseholdEventPayload payload) {
        this.id = payload.getHouseholdId();
        this.complexId = payload.getApartmentComplexId();
        this.building = payload.getBuildingNo();
        this.unit = payload.getUnitNo();
        this.status = payload.getStatus() != null ? HouseholdCacheStatus.valueOf(payload.getStatus()) : null;
    }

    // 세대주 사용자 식별자를 갱신한다.
    public void updateHeadUserId(Long headUserId) {
        this.headUserId = headUserId;
    }
}
