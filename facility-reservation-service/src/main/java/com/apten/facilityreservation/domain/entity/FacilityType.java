package com.apten.facilityreservation.domain.entity;

import com.apten.common.entity.BaseEntity;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 시설 타입 엔티티
// 독서실, 헬스장, 골프장, GX 같은 분류 기준을 관리한다
@Entity
@Table(name = "facility_type")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacilityType extends BaseEntity {

    // 시설 타입 내부 PK
    @Id
    @Tsid
    @Column(name = "id", nullable = false)
    private Long id;

    // 타입 코드
    @Column(name = "type_code", nullable = false, length = 30, unique = true)
    private String typeCode;

    // 타입 이름
    @Column(name = "type_name", nullable = false, length = 50)
    private String typeName;

    // 설명
    @Column(name = "description", length = 255)
    private String description;

    // 활성 여부
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;
}
