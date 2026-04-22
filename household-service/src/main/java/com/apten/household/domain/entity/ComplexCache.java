package com.apten.household.domain.entity;

import com.apten.common.entity.BaseEntity;
import com.apten.household.domain.enums.ComplexCacheStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// Kafka로 받은 단지 기본 정보를 로컬 캐시에 저장하는 엔티티
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "complex_cache",
        indexes = {
                @Index(name = "idx_complex_cache_status", columnList = "status")
        }
)
public class ComplexCache extends BaseEntity {

    // 외부 원본 단지 ID
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    // 단지명
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    // 단지 주소
    @Column(name = "address", nullable = false, length = 255)
    private String address;

    // 단지 상태
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ComplexCacheStatus status;

    // 단지 캐시 내용을 갱신한다
    public void apply(String name, String address, ComplexCacheStatus status) {
        this.name = name;
        this.address = address;
        this.status = status;
    }
}
