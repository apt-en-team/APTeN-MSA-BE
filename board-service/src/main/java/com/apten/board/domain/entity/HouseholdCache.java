package com.apten.board.domain.entity;

import com.apten.common.entity.BaseEntity;
import com.apten.common.kafka.payload.HouseholdEventPayload;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// board-service가 세대 참조 데이터를 로컬 캐시 테이블로 유지하는 엔티티
@Entity
@Getter
@NoArgsConstructor
@Table(
        name = "household_cache",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_household_cache_complex_building_unit", columnNames = {"apartment_complex_id", "building_no", "unit_no"})
        },
        indexes = {
                @Index(name = "idx_household_cache_complex_id", columnList = "apartment_complex_id"),
                @Index(name = "idx_household_cache_status", columnList = "status")
        }
)
public class HouseholdCache extends BaseEntity {

    // 원본 세대 ID를 그대로 캐시 PK로 사용한다
    @Id
    @Column(name = "id", nullable = false)
    private Long householdId;

    // 세대가 속한 단지 식별자
    @Column(name = "apartment_complex_id", nullable = false)
    private Long apartmentComplexId;

    // 동 정보
    @Column(name = "building_no", nullable = false, length = 10)
    private String buildingNo;

    // 호 정보
    @Column(name = "unit_no", nullable = false, length = 10)
    private String unitNo;

    // soft delete를 포함한 상태값
    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Builder
    private HouseholdCache(Long householdId, Long apartmentComplexId, String buildingNo, String unitNo, String status) {
        this.householdId = householdId;
        this.apartmentComplexId = apartmentComplexId;
        this.buildingNo = buildingNo;
        this.unitNo = unitNo;
        this.status = status;
    }

    // 같은 이벤트를 다시 받아도 같은 상태가 되도록 필드를 덮어쓴다
    public void apply(HouseholdEventPayload payload) {
        this.householdId = payload.getHouseholdId();
        this.apartmentComplexId = payload.getApartmentComplexId();
        this.buildingNo = payload.getBuildingNo();
        this.unitNo = payload.getUnitNo();
        this.status = payload.getStatus();
    }
}
