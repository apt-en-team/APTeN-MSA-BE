package com.apten.parkingvehicle.domain.entity;

import com.apten.common.entity.BaseEntity;
import com.apten.parkingvehicle.domain.enums.VehicleStatus;
import com.apten.parkingvehicle.domain.enums.VehicleType;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 입주민 차량 등록 신청과 승인 상태를 관리하는 엔티티
@Entity
@Table(
        name = "vehicle",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_vehicle_complex_license_plate", columnNames = {"complex_id", "license_plate"})
        },
        indexes = {
                @Index(name = "idx_vehicle_user_id", columnList = "user_id"),
                @Index(name = "idx_vehicle_household_id", columnList = "household_id"),
                @Index(name = "idx_vehicle_status", columnList = "status"),
                @Index(name = "idx_vehicle_complex_id", columnList = "complex_id")
        }
)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle extends BaseEntity {

    // 차량 내부 PK
    @Id
    @Tsid
    @Column(name = "id", nullable = false)
    private Long id;

    // 등록 사용자 ID
    @Column(name = "user_id", nullable = false)
    private Long userId;

    // 소속 세대 ID
    @Column(name = "household_id", nullable = false)
    private Long householdId;

    // 소속 단지 ID
    @Column(name = "complex_id", nullable = false)
    private Long complexId;

    // 차량 번호
    @Column(name = "license_plate", nullable = false, length = 20)
    private String licensePlate;

    // 차량명
    @Column(name = "model_name", nullable = false, length = 50)
    private String modelName;

    // 차량 종류
    @Builder.Default
    @Column(name = "vehicle_type", nullable = false, length = 20)
    private VehicleType vehicleType = VehicleType.CAR;

    // 승인 상태
    @Builder.Default
    @Column(name = "status", nullable = false, length = 20)
    private VehicleStatus status = VehicleStatus.PENDING;

    // 대표 차량 여부
    @Builder.Default
    @Column(name = "is_primary", nullable = false)
    private Boolean isPrimary = false;

    // 승인 일시
    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    // 거절 사유
    @Column(name = "reject_reason", length = 255)
    private String rejectReason;

    // 소프트 삭제 여부
    @Builder.Default
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    // 삭제 일시
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // 차량 기본 정보를 수정한다
    public void update(String modelName, VehicleType vehicleType, Boolean isPrimary) {
        this.modelName = modelName;
        this.vehicleType = vehicleType;
        this.isPrimary = isPrimary;
    }

    // 차량 승인 상태를 반영한다
    public void changeStatus(VehicleStatus status, LocalDateTime approvedAt, String rejectReason) {
        this.status = status;
        this.approvedAt = approvedAt;
        this.rejectReason = rejectReason;
    }

    // 차량을 소프트 삭제한다
    public void markDeleted(LocalDateTime deletedAt) {
        this.isDeleted = true;
        this.deletedAt = deletedAt;
        this.status = VehicleStatus.DELETED;
    }
}
