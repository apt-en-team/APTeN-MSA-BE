package com.apten.board.domain.entity;

import com.apten.common.entity.BaseEntity;
import com.apten.common.kafka.payload.ApartmentComplexEventPayload;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// board-service가 단지 참조 데이터를 로컬 캐시 테이블로 유지하는 엔티티
@Entity
@Getter
@NoArgsConstructor
@Table(
        name = "apartment_complex_cache",
        indexes = {
                @Index(name = "idx_apartment_complex_cache_status", columnList = "status")
        }
)
public class ApartmentComplexCache extends BaseEntity {

    // 원본 단지 ID를 그대로 캐시 PK로 사용한다
    @Id
    @Column(name = "id", nullable = false)
    private Long apartmentComplexId;

    // 게시판 화면에서 표시할 단지명
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    // 단지 주소이다.
    @Column(name = "address", nullable = false, length = 255)
    private String address;

    // soft delete를 포함한 상태값
    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Builder
    private ApartmentComplexCache(Long apartmentComplexId, String name, String address, String status) {
        this.apartmentComplexId = apartmentComplexId;
        this.name = name;
        this.address = address;
        this.status = status;
    }

    // 같은 이벤트를 다시 받아도 같은 상태가 되도록 필드를 덮어쓴다
    public void apply(ApartmentComplexEventPayload payload) {
        this.apartmentComplexId = payload.getApartmentComplexId();
        this.name = payload.getName();
        this.address = payload.getAddress();
        this.status = payload.getStatus();
    }
}
