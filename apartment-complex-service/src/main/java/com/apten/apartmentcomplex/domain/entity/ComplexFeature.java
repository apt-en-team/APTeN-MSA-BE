package com.apten.apartmentcomplex.domain.entity;

import com.apten.common.entity.BaseEntity;
import com.apten.common.enums.FeatureCode;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 단지별 기능 사용 여부를 저장하는 원본 엔티티이다.
@Entity
@Table(
        name = "complex_feature",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_complex_feature_complex_id_feature_code", columnNames = {"complex_id", "feature_code"})
        },
        indexes = {
                @Index(name = "idx_complex_feature_complex_id", columnList = "complex_id"),
                @Index(name = "idx_complex_feature_feature_code", columnList = "feature_code")
        }
)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplexFeature extends BaseEntity {

    @Id
    @Tsid
    @Column(name = "id", nullable = false)
    private Long id;

    // 같은 서비스 내부 테이블이므로 complex.id FK를 직접 사용한다.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "complex_id", nullable = false, foreignKey = @jakarta.persistence.ForeignKey(name = "fk_complex_feature_complex"))
    private ApartmentComplex complex;

    @Column(name = "feature_code", nullable = false, length = 50)
    private FeatureCode featureCode;

    @Builder.Default
    @Column(name = "enabled", nullable = false)
    private boolean enabled = true;

    // 기능 사용 여부를 변경한다.
    public void updateEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
