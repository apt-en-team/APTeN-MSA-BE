package com.apten.apartmentcomplex.domain.entity;

import com.apten.common.entity.BaseEntity;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 단지별 세대 유형을 저장하는 엔티티
// 세대 등록 시 선택할 평형 타입 마스터를 표현한다
@Entity
@Table(
        name = "household_type",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_household_type_complex_type_name", columnNames = {"complex_id", "type_name"})
        },
        indexes = {
                @Index(name = "idx_household_type_complex_id", columnList = "complex_id"),
                @Index(name = "idx_household_type_type_name", columnList = "type_name")
        }
)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HouseholdType extends BaseEntity {

    // 세대 유형 내부 PK
    @Id
    @Tsid
    @Column(name = "id", nullable = false)
    private Long id;

    // 세대 유형이 속한 단지 ID
    @Column(name = "complex_id", nullable = false)
    private Long complexId;

    // 세대 유형명
    @Column(name = "type_name", nullable = false)
    private String typeName;

    // 세대 유형 설명
    @Column(name = "description")
    private String description;

    // 사용 여부
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    // 세대 유형 정보를 수정할 때 사용한다
    public void apply(String typeName, String description, Boolean isActive) {
        this.typeName = typeName;
        this.description = description;
        this.isActive = isActive;
    }
}
