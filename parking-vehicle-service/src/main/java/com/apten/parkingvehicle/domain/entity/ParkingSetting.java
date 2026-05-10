package com.apten.parkingvehicle.domain.entity;

import com.apten.common.entity.BaseEntity;
import com.apten.parkingvehicle.domain.enums.ParkingType;
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

// 단지별 주차 운영 타입 설정을 관리하는 엔티티
@Entity
@Table(
        name = "parking_setting",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_parking_setting_complex_id", columnNames = "complex_id")
        }
)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParkingSetting extends BaseEntity {

    // 주차 설정 내부 PK
    @Id
    @Tsid
    @Column(name = "id", nullable = false)
    private Long id;

    // 소속 단지 ID (단지당 1개 보장)
    @Column(name = "complex_id", nullable = false)
    private Long complexId;

    // 주차 운영 타입 (NONE / BASIC / SENSOR)
    @Builder.Default
    @Column(name = "parking_type", nullable = false, length = 20)
    private ParkingType parkingType = ParkingType.NONE;

    // 주차 운영 타입을 변경한다.
    public void changeType(ParkingType parkingType) {
        this.parkingType = parkingType;
    }
}