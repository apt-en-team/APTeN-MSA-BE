package com.apten.household.domain.entity;

import com.apten.common.entity.BaseEntity;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "household_type",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_household_type_type_code", columnNames = "type_code")
        },
        indexes = {
                @Index(name = "idx_household_type_exclusive_area", columnList = "exclusive_area_m2"),
                @Index(name = "idx_household_type_is_active", columnList = "is_active")
        }
)
public class HouseholdType extends BaseEntity {

    @Id
    @Tsid
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "type_code", nullable = false, length = 30)
    private String typeCode;

    @Column(name = "type_name", nullable = false, length = 50)
    private String typeName;

    @Column(name = "exclusive_area_m2", nullable = false, precision = 5, scale = 2)
    private BigDecimal exclusiveAreaM2;

    @Column(name = "description")
    private String description;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    public void update(String typeName, BigDecimal exclusiveAreaM2, String description, Boolean isActive) {
        this.typeName = typeName;
        this.exclusiveAreaM2 = exclusiveAreaM2;
        this.description = description;
        this.isActive = isActive;
    }

    public void deactivate() {
        this.isActive = false;
    }
}
