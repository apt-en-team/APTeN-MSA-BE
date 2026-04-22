package com.apten.parkingvehicle.domain.entity;

import com.apten.common.entity.BaseEntity;
import com.apten.parkingvehicle.domain.enums.EntryType;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 차량 입출차 이력을 저장하는 엔티티
@Entity
@Table(name = "parking_log")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParkingLog extends BaseEntity {

    // 주차 로그 내부 PK
    @Id
    @Tsid
    @Column(name = "id", nullable = false)
    private Long id;

    // 소속 단지 ID
    @Column(name = "complex_id", nullable = false)
    private Long complexId;

    // 주차 층 ID
    @Column(name = "floor_id", nullable = false)
    private Long floorId;

    // 입주민 차량 ID
    @Column(name = "vehicle_id")
    private Long vehicleId;

    // 방문차량 ID
    @Column(name = "visitor_vehicle_id")
    private Long visitorVehicleId;

    // 고정 방문차량 ID
    @Column(name = "regular_visitor_vehicle_id")
    private Long regularVisitorVehicleId;

    // 세대 ID
    @Column(name = "household_id")
    private Long householdId;

    // 차량 번호
    @Column(name = "license_plate", nullable = false, length = 20)
    private String licensePlate;

    // 입출차 구분
    @Enumerated(EnumType.STRING)
    @Column(name = "entry_type", nullable = false, length = 10)
    private EntryType entryType;

    // 기록 일시
    @Column(name = "logged_at", nullable = false)
    private LocalDateTime loggedAt;

    // 비고
    @Column(name = "memo", length = 255)
    private String memo;
}
