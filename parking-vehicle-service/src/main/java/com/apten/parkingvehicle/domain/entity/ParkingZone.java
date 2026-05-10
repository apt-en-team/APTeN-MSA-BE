package com.apten.parkingvehicle.domain.entity;

import com.apten.common.entity.BaseEntity;
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

// 단지별 주차 구역 기준 정보를 관리하는 엔티티
@Entity
@Table(
        name = "parking_zone",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_parking_zone_complex_area_zone", columnNames = {"complex_id", "area_name", "zone_name"})
        },
        indexes = {
                @Index(name = "idx_parking_zone_complex_id", columnList = "complex_id"),
                @Index(name = "idx_parking_zone_is_active", columnList = "is_active")
        }
)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParkingZone extends BaseEntity {

    // 주차 구역 내부 PK
    @Id
    @Tsid
    @Column(name = "id", nullable = false)
    private Long id;

    // 소속 단지 ID
    @Column(name = "complex_id", nullable = false)
    private Long complexId;

    // 주차장 단위 이름 (예: B1, B2, 지상주차장)
    @Column(name = "area_name", nullable = false, length = 20)
    private String areaName;

    // 구역 단위 이름 (B타입은 NULL, C타입에서만 사용. 예: A구역, B구역)
    @Column(name = "zone_name", length = 20)
    private String zoneName;

    // 전체 면수
    @Builder.Default
    @Column(name = "total_slots", nullable = false)
    private Integer totalSlots = 0;

    // 활성 여부
    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // 주차 구역 기본 정보를 수정한다.
    public void update(String areaName, String zoneName, Integer totalSlots, Boolean isActive) {
        this.areaName = areaName;
        this.zoneName = zoneName;
        this.totalSlots = totalSlots;
        this.isActive = isActive;
    }
}