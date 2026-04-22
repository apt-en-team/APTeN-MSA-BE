package com.apten.parkingvehicle.domain.entity;

import com.apten.common.entity.BaseEntity;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 단지별 주차 층 기준 정보를 관리하는 엔티티
@Entity
@Table(
        name = "parking_floor",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_parking_floor_complex_floor_name", columnNames = {"complex_id", "floor_name"})
        }
)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParkingFloor extends BaseEntity {

    // 주차 층 내부 PK
    @Id
    @Tsid
    @Column(name = "id", nullable = false)
    private Long id;

    // 소속 단지 ID
    @Column(name = "complex_id", nullable = false)
    private Long complexId;

    // 층 이름
    @Column(name = "floor_name", nullable = false, length = 20)
    private String floorName;

    // 전체 면수
    @Column(name = "total_slots", nullable = false)
    private Integer totalSlots;

    // 활성 여부
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;
}
