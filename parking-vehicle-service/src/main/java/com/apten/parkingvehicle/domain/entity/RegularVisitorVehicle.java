package com.apten.parkingvehicle.domain.entity;

import com.apten.common.entity.BaseEntity;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 정기적으로 방문하는 차량 정보를 관리하는 엔티티
@Entity
@Table(
        name = "regular_visitor_vehicle",
        indexes = {
                @Index(name = "idx_regular_visitor_vehicle_user_id", columnList = "user_id"),
                @Index(name = "idx_regular_visitor_vehicle_household_id", columnList = "household_id"),
                @Index(name = "idx_regular_visitor_vehicle_complex_id", columnList = "complex_id"),
                @Index(name = "idx_regular_visitor_vehicle_is_active", columnList = "is_active")
        }
)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegularVisitorVehicle extends BaseEntity {

    // 고정 방문차량 내부 PK
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

    // 방문자 이름
    @Column(name = "visitor_name", length = 50)
    private String visitorName;

    // 연락처
    @Column(name = "phone", length = 20)
    private String phone;

    // 시작일
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    // 종료일
    @Column(name = "end_date")
    private LocalDate endDate;

    // 활성 여부
    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // 소프트 삭제 여부
    @Builder.Default
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    // 삭제 일시
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // 고정 방문차량을 소프트 삭제한다
    public void markDeleted(LocalDateTime deletedAt) {
        this.isDeleted = true;
        this.deletedAt = deletedAt;
        this.isActive = false;
    }
}
