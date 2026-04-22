package com.apten.apartmentcomplex.domain.entity;

import com.apten.common.entity.BaseEntity;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 단지 기본 운영 정책을 저장하는 엔티티
// 기본 관리비와 예약 기본 단위를 이 테이블이 가진다
@Entity
@Table(name = "complex_policy")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplexPolicy extends BaseEntity {

    // 정책 내부 PK
    @Id
    @Tsid
    @Column(name = "id", nullable = false)
    private Long id;

    // 정책이 속한 단지 ID
    @Column(name = "complex_id", nullable = false)
    private Long complexId;

    // 기본 관리비
    @Column(name = "base_fee", nullable = false)
    private BigDecimal baseFee;

    // 기본 예약 단위
    @Column(name = "default_slot_min", nullable = false)
    private Integer defaultSlotMin;

    // 정책 활성 여부
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    // 적용 시작일
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    // 적용 종료일
    @Column(name = "end_date")
    private LocalDate endDate;

    // 기본 정책 값을 갱신할 때 사용한다
    public void apply(BigDecimal baseFee, Integer defaultSlotMin, Boolean isActive, LocalDate startDate, LocalDate endDate) {
        this.baseFee = baseFee;
        this.defaultSlotMin = defaultSlotMin;
        this.isActive = isActive;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
