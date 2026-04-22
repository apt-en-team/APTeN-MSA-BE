package com.apten.auth.domain.entity;

import com.apten.auth.domain.enums.ComplexCacheStatus;
import com.apten.common.entity.BaseEntity;
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

// 단지 서비스에서 전달받은 단지 캐시 엔티티
// 회원가입 시 단지 선택과 검증에 사용할 로컬 캐시 데이터다
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

    // 단지 주소
    @Column(name = "address", nullable = false, length = 255)
    private String address;

    // 단지 상태
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ComplexCacheStatus status;

    // 캐시 값을 갱신할 때 사용한다
    public void apply(String name, String address, ComplexCacheStatus status) {
        this.name = name;
        this.address = address;
        this.status = status;
    }
}
