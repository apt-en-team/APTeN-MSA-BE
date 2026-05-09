package com.apten.household.domain.entity;

import com.apten.common.entity.BaseEntity;
import com.apten.household.domain.enums.VehicleSnapshotStatus;
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

// 비용 계산에 사용할 승인 차량 스냅샷 엔티티
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "vehicle_snapshot",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_vehicle_snapshot_id", columnNames = "id")
        },
        indexes = {
                @Index(name = "idx_vehicle_snapshot_household_id", columnList = "household_id"),
                @Index(name = "idx_vehicle_snapshot_complex_id", columnList = "complex_id"),
                @Index(name = "idx_vehicle_snapshot_status", columnList = "status")
        }
)
public class VehicleSnapshot extends BaseEntity {

    // 외부 원본 차량 ID
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    // 세대 ID
    @Column(name = "household_id", nullable = false)
    private Long householdId;

    // 단지 ID
    @Column(name = "complex_id", nullable = false)
    private Long complexId;

    // 차량 번호
    @Column(name = "license_plate", nullable = false, length = 20)
    private String licensePlate;

    // 차량 승인 상태
    @Builder.Default
    @Column(name = "status", nullable = false, length = 20)
    private VehicleSnapshotStatus status = VehicleSnapshotStatus.APPROVED;

    // 삭제 여부
    @Builder.Default
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    // 차량 스냅샷 내용을 갱신한다
    public void apply(
            Long householdId,
            Long complexId,
            String licensePlate,
            VehicleSnapshotStatus status,
            Boolean isDeleted
    ) {
        this.householdId = householdId;
        this.complexId = complexId;
        this.licensePlate = licensePlate;
        this.status = status;
        this.isDeleted = isDeleted;
    }
}
