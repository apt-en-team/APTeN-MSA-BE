package com.apten.facilityreservation.domain.entity;

import com.apten.common.entity.BaseEntity;
import com.apten.common.enums.FeatureCode;
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

// 단지별 기능 사용 여부를 저장하는 로컬 캐시 엔티티이다.
@Entity
@Table(
        name = "complex_feature_cache",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_complex_feature_cache_complex_feature",
                        columnNames = {"complex_id", "feature_code"}
                )
        },
        indexes = {
                @Index(name = "idx_complex_feature_cache_complex_id", columnList = "complex_id"),
                @Index(name = "idx_complex_feature_cache_feature_code", columnList = "feature_code")
        }
)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplexFeatureCache extends BaseEntity {

    // 캐시 내부 PK이다.
    @Id
    @Tsid
    @Column(name = "id", nullable = false)
    private Long id;

    // 단지 원본 ID를 논리 참조한다.
    @Column(name = "complex_id", nullable = false)
    private Long complexId;

    // 기능 코드이다.
    @Column(name = "feature_code", nullable = false, length = 50)
    private FeatureCode featureCode;

    // 기능 사용 여부이다.
    @Builder.Default
    @Column(name = "enabled", nullable = false)
    private Boolean enabled = true;

    // 기능 사용 여부를 변경한다.
    public void changeEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    // 기능 사용 여부를 boolean 기반으로 갱신한다.
    public void updateEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
