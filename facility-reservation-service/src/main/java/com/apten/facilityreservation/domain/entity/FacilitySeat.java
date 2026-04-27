package com.apten.facilityreservation.domain.entity;

import com.apten.facilityreservation.application.model.request.FacilitySeatPatchReq;
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

// 좌석형 시설 좌석 엔티티이다.
// 독서실 좌석과 골프 타석 같은 좌석 자원을 관리한다.
@Entity
@Table(
        name = "facility_seat",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_facility_seat_no", columnNames = {"facility_id", "seat_no"})
        },
        indexes = {
                @Index(name = "idx_facility_seat_facility_id", columnList = "facility_id"),
                @Index(name = "idx_facility_seat_is_active", columnList = "is_active")
        }
)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacilitySeat extends BaseEntity {

    // 좌석 내부 PK이다.
    @Id
    @Tsid
    @Column(name = "id", nullable = false)
    private Long id;

    // 소속 시설 ID이다.
    @Column(name = "facility_id", nullable = false)
    private Long facilityId;

    // 좌석 번호이다.
    @Column(name = "seat_no", nullable = false)
    private Integer seatNo;

    // 좌석 표시명이다.
    @Column(name = "seat_name", length = 50)
    private String seatName;

    // 정렬 순서이다.
    @Builder.Default
    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

    // 활성 여부이다.
    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // 좌석 수정 요청을 엔티티에 반영한다.
    public void apply(FacilitySeatPatchReq req) {
        if (req.getSeatName() != null) {
            this.seatName = req.getSeatName();
        }
        if (req.getSortOrder() != null) {
            this.sortOrder = req.getSortOrder();
        }
        if (req.getIsActive() != null) {
            this.isActive = req.getIsActive();
        }
    }
}
