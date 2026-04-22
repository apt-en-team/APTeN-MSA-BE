package com.apten.facilityreservation.domain.entity;

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

// 좌석형 시설 좌석 엔티티
// 독서실 좌석과 골프 타석 같은 좌석 자원을 관리한다
@Entity
@Table(name = "facility_seat", uniqueConstraints = @UniqueConstraint(name = "uk_facility_seat_no", columnNames = {"facility_id", "seat_no"}))
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacilitySeat extends BaseEntity {

    // 좌석 내부 PK
    @Id
    @Tsid
    @Column(name = "id", nullable = false)
    private Long id;

    // 소속 시설 ID
    @Column(name = "facility_id", nullable = false)
    private Long facilityId;

    // 좌석 번호
    @Column(name = "seat_no", nullable = false)
    private Integer seatNo;

    // 좌석 이름
    @Column(name = "seat_name", length = 50)
    private String seatName;

    // 정렬 순서
    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    // 활성 여부
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;
}
