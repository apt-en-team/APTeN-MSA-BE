package com.apten.facilityreservation.domain.entity;

import com.apten.common.entity.BaseEntity;
import com.apten.common.kafka.payload.ApartmentComplexEventPayload;
import com.apten.facilityreservation.domain.enums.ComplexCacheStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 단지 기본 정보를 저장하는 로컬 캐시 엔티티
// 시설 등록과 예약 시 단지 검증과 표시용으로 사용한다
@Entity
@Table(name = "complex_cache")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplexCache extends BaseEntity {

    // 외부 원본 단지 ID
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    // 단지명
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    // 주소
    @Column(name = "address", nullable = false, length = 255)
    private String address;

    // 단지 상태
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ComplexCacheStatus status;

    // 이벤트 기반 캐시 값을 덮어쓸 때 사용한다
    public void apply(ApartmentComplexEventPayload payload) {
        this.id = payload.getApartmentComplexId();
        this.name = payload.getName();
        this.address = payload.getName();
        this.status = payload.getStatus() == null ? null : ComplexCacheStatus.valueOf(payload.getStatus());
    }
}
