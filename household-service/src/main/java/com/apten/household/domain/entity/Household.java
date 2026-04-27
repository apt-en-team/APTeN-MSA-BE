package com.apten.household.domain.entity;

import com.apten.common.entity.BaseEntity;
import com.apten.household.domain.enums.HouseholdStatus;
import io.hypersistence.utils.hibernate.id.Tsid;
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

// 세대 마스터를 저장하는 엔티티
// 단지, 동, 호 조합으로 한 세대를 식별한다
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "household",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_household_complex_building_unit", columnNames = {"complex_id", "building", "unit"})
        },
        indexes = {
                @Index(name = "idx_household_complex_id", columnList = "complex_id"),
                @Index(name = "idx_household_status", columnList = "status")
        }
)
public class Household extends BaseEntity {

    // 세대 PK
    @Id
    @Tsid
    @Column(name = "id", nullable = false)
    private Long id;

    // 단지 ID
    @Column(name = "complex_id", nullable = false)
    private Long complexId;

    // 동 정보
    @Column(name = "building", nullable = false, length = 10)
    private String building;

    // 호 정보
    @Column(name = "unit", nullable = false, length = 10)
    private String unit;

    // 세대 유형 ID
    @Column(name = "type_id")
    private Long typeId;

    // 세대 상태
    @Builder.Default
    @Column(name = "status", nullable = false, length = 20)
    private HouseholdStatus status = HouseholdStatus.VACANT;

    // 세대 기본 정보를 수정한다
    public void update(Long complexId, String building, String unit, Long typeId) {
        this.complexId = complexId;
        this.building = building;
        this.unit = unit;
        this.typeId = typeId;
    }

    // 세대 상태를 변경한다
    public void changeStatus(HouseholdStatus status) {
        this.status = status;
    }
}
