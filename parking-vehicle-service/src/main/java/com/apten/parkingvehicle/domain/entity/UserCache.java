package com.apten.parkingvehicle.domain.entity;

import com.apten.common.entity.BaseEntity;
import com.apten.common.kafka.payload.UserEventPayload;
import com.apten.parkingvehicle.domain.enums.UserCacheRole;
import com.apten.parkingvehicle.domain.enums.UserCacheStatus;
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

// 주차 차량 서비스에서 사용하는 사용자 캐시 엔티티
@Entity
@Table(name = "user_cache")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCache extends BaseEntity {

    // 원본 사용자 PK
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    // 사용자 소속 단지 ID
    @Column(name = "complex_id", nullable = false)
    private Long complexId;

    // 사용자 이름
    @Column(name = "name", nullable = false, length = 50)
    private String name;

    // 사용자 권한
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private UserCacheRole role;

    // 사용자 상태
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private UserCacheStatus status;

    // 사용자 이벤트로 캐시 내용을 갱신한다
    public void apply(UserEventPayload payload) {
        this.id = payload.getUserId();
        this.complexId = payload.getApartmentComplexId();
        this.name = payload.getName();
        this.role = payload.getRole() != null ? UserCacheRole.valueOf(payload.getRole()) : null;
        this.status = payload.getStatus() != null ? UserCacheStatus.valueOf(payload.getStatus()) : null;
    }
}
