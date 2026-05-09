package com.apten.parkingvehicle.domain.entity;

import com.apten.common.entity.BaseEntity;
import com.apten.common.kafka.payload.ApartmentComplexEventPayload;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 주차 서비스에서 사용하는 단지 기본 정보 캐시 엔티티이다.
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

    // 단지 상태 문자열이다.
    @Column(name = "status", nullable = false, length = 20)
    private String status;

    // 단지 기본 정보 캐시를 최신 이벤트 기준으로 갱신한다.
    public void updateFromPayload(ApartmentComplexEventPayload payload) {
        this.id = payload.getApartmentComplexId();
        this.name = payload.getName();
        this.address = payload.getAddress();
        this.status = payload.getStatus();
    }
}
