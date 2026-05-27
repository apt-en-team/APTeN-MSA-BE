package com.apten.parkingvehicle.domain.entity;

import com.apten.parkingvehicle.application.model.request.VehicleRegistrationPolicyPutReq;
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

// 단지별 입주민 차량 등록 한도 정책 원본 엔티티이다.
// 세대당 등록 가능한 최대 차량 대수를 단지 단위로 관리한다.
@Entity
@Table(
        name = "vehicle_registration_policy",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_vehicle_registration_policy_complex_id", columnNames = "complex_id")
        },
        indexes = {
                @Index(name = "idx_vehicle_registration_policy_complex_id", columnList = "complex_id"),
                @Index(name = "idx_vehicle_registration_policy_is_active", columnList = "is_active")
        }
)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleRegistrationPolicy extends BaseEntity {

    // 등록 한도 정책 원본 PK이다.
    @Id
    @Tsid
    @Column(name = "id", nullable = false)
    private Long id;

    // 정책이 속한 단지 ID이다.
    @Column(name = "complex_id", nullable = false)
    private Long complexId;

    // 세대당 등록 가능 최대 차량 대수이다.
    @Column(name = "max_car_count", nullable = false)
    private Integer maxCarCount;

    // 현재 정책 활성 여부이다.
    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // 등록 한도 정책 요청값을 현재 엔티티에 반영한다.
    public void apply(VehicleRegistrationPolicyPutReq req) {
        // 최대 등록 대수가 들어오면 기존 값을 새 값으로 교체
        if (req.getMaxCarCount() != null) {
            this.maxCarCount = req.getMaxCarCount();
        }

        // 활성 여부가 들어오면 운영 여부를 함께 갱신
        if (req.getIsActive() != null) {
            this.isActive = req.getIsActive();
        }
    }
}
