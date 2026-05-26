package com.apten.household.domain.entity;

import com.apten.common.entity.BaseEntity;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
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
        name = "building_line_type",
        indexes = {
                @Index(name = "idx_building_line_type_complex_building", columnList = "complex_id, building")
        }
)
public class BuildingLineType extends BaseEntity {

    @Id
    @Tsid
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "complex_id", nullable = false)
    private Long complexId;

    @Column(name = "building", nullable = false, length = 10)
    private String building;

    @Column(name = "line_start", nullable = false)
    private Integer lineStart;

    @Column(name = "line_end", nullable = false)
    private Integer lineEnd;

    @Column(name = "type_id", nullable = false)
    private Long typeId;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    public void update(Integer lineStart, Integer lineEnd, Long typeId, Boolean isActive) {
        this.lineStart = lineStart;
        this.lineEnd = lineEnd;
        this.typeId = typeId;
        this.isActive = isActive;
    }

    public void deactivate() {
        this.isActive = false;
    }
}
