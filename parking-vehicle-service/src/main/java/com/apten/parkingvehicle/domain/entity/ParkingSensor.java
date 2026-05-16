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

// 단지별 자리 단위 주차 센서 정보를 관리하는 엔티티
@Entity
@Table(
        name = "parking_sensor",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_parking_sensor_complex_code", columnNames = {"complex_id", "sensor_code"}),
                @UniqueConstraint(name = "uk_parking_sensor_zone_spot", columnNames = {"zone_id", "spot_number"})
        },
        indexes = {
                @Index(name = "idx_parking_sensor_zone", columnList = "zone_id"),
                @Index(name = "idx_parking_sensor_active", columnList = "is_active")
        }
)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParkingSensor extends BaseEntity {

    // 주차 센서 내부 PK
    @Id
    @Tsid
    @Column(name = "id", nullable = false)
    private Long id;

    // 소속 단지 ID
    @Column(name = "complex_id", nullable = false)
    private Long complexId;

    // 소속 주차 구역 ID (parking_zone 참조, FK 어노테이션은 두지 않고 plain Long으로 유지)
    @Column(name = "zone_id", nullable = false)
    private Long zoneId;

    // 센서 식별 코드 (단지 내 unique)
    @Column(name = "sensor_code", nullable = false, length = 50)
    private String sensorCode;

    // 자리 번호 (zone 내 unique)
    @Column(name = "spot_number", nullable = false, length = 20)
    private String spotNumber;

    // 활성 여부
    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // 자리 부가 설명 (예: 엘리베이터 앞)
    @Column(name = "description", length = 100)
    private String description;
}
