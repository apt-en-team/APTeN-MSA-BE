package com.apten.parkingvehicle.domain.entity;

import com.apten.common.entity.BaseEntity;
import com.apten.common.kafka.payload.ApartmentComplexEventPayload;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// parking-vehicle-service가 단지 참조 데이터를 로컬 캐시 테이블로 유지하는 엔티티
@Entity
@Getter
@NoArgsConstructor
@Table(name = "apartment_complex_cache")
public class ApartmentComplexCache extends BaseEntity {

    // 원본 단지 ID를 그대로 캐시 PK로 사용한다
    @Id
    private Long apartmentComplexId;

    // 차량 등록 대상 단지 표시명
    private String name;

    // soft delete를 포함한 상태값
    private String status;

    @Builder
    private ApartmentComplexCache(Long apartmentComplexId, String name, String status) {
        this.apartmentComplexId = apartmentComplexId;
        this.name = name;
        this.status = status;
    }

    // 같은 이벤트를 다시 받아도 같은 상태가 되도록 필드를 덮어쓴다
    public void apply(ApartmentComplexEventPayload payload) {
        this.apartmentComplexId = payload.getApartmentComplexId();
        this.name = payload.getName();
        this.status = payload.getStatus();
    }
}
