package com.apten.facilityreservation.domain.entity;

import com.apten.facilityreservation.domain.enums.FacilityTypeCode;
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

// 시설 타입 마스터 엔티티이다.
// 시설과 정책이 참조하는 타입 코드와 노출 이름을 이 테이블이 관리한다.
@Entity
@Table(
        name = "facility_type",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_facility_type_code", columnNames = "type_code")
        },
        indexes = {
                @Index(name = "idx_facility_type_is_active", columnList = "is_active")
        }
)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacilityType extends BaseEntity {

    // 시설 타입 내부 PK이다.
    @Id
    @Tsid
    @Column(name = "id", nullable = false)
    private Long id;

    // 타입 코드이다.
    @Column(name = "type_code", nullable = false, length = 30)
    private FacilityTypeCode typeCode;

    // 타입명이다.
    @Column(name = "type_name", nullable = false, length = 50)
    private String typeName;

    // 타입 설명이다.
    @Column(name = "description", length = 255)
    private String description;

    // 활성 여부이다.
    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}
