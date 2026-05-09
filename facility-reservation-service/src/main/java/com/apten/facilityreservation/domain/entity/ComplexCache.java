package com.apten.facilityreservation.domain.entity;

import com.apten.common.entity.BaseEntity;
import com.apten.common.kafka.payload.ApartmentComplexEventPayload;
import com.apten.facilityreservation.domain.enums.ComplexCacheStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 단지 기본 정보를 저장하는 로컬 캐시 엔티티이다.
// 시설 등록과 예약 시 단지 존재 여부와 활성 상태 확인에 사용한다.
@Entity
@Table(
        name = "complex_cache",
        indexes = {
                @Index(name = "idx_complex_cache_status", columnList = "status")
        }
)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplexCache extends BaseEntity {

    // Apartment Complex 원본 ID를 그대로 캐시 PK로 사용한다.
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    // 단지명이다.
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    // 단지 주소이다.
    @Column(name = "address", nullable = false, length = 255)
    private String address;

    // 단지 활성 상태이다.
    @Builder.Default
    @Column(name = "status", nullable = false, length = 20)
    private ComplexCacheStatus status = ComplexCacheStatus.ACTIVE;

    // 수신한 단지 이벤트를 캐시 테이블에 반영한다.
    public void apply(ApartmentComplexEventPayload payload) {
        this.id = payload.getApartmentComplexId();
        this.name = payload.getName();
        this.address = payload.getAddress();
        if (payload.getStatus() != null) {
            this.status = ComplexCacheStatus.valueOf(payload.getStatus());
        }
    }
}
