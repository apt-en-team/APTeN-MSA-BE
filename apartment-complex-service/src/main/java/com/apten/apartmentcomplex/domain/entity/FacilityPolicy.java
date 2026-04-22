package com.apten.apartmentcomplex.domain.entity;

import com.apten.common.entity.BaseEntity;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 시설 정책을 저장하는 엔티티
// 시설 유형별 기본 요금과 예약 단위를 관리한다
@Entity
@Table(name = "facility_policy")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacilityPolicy extends BaseEntity {

    // 시설 정책 내부 PK
    @Id
    @Tsid
    @Column(name = "id", nullable = false)
    private Long id;

    // 정책이 속한 단지 ID
    @Column(name = "complex_id", nullable = false)
    private Long complexId;

    // 시설 유형 코드
    @Column(name = "facility_type_code", nullable = false)
    private String facilityTypeCode;

    // 기본 요금
    @Column(name = "base_fee", nullable = false)
    private BigDecimal baseFee;

    // 예약 단위 분
    @Column(name = "slot_min", nullable = false)
    private Integer slotMin;

    // 정책 활성 여부
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    // 시설 정책 값을 갱신할 때 사용한다
    public void apply(String facilityTypeCode, BigDecimal baseFee, Integer slotMin, Boolean isActive) {
        this.facilityTypeCode = facilityTypeCode;
        this.baseFee = baseFee;
        this.slotMin = slotMin;
        this.isActive = isActive;
    }
}
