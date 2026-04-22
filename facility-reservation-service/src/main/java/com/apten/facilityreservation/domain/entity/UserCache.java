package com.apten.facilityreservation.domain.entity;

import com.apten.common.entity.BaseEntity;
import com.apten.common.kafka.payload.UserEventPayload;
import com.apten.facilityreservation.domain.enums.UserCacheRole;
import com.apten.facilityreservation.domain.enums.UserCacheStatus;
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

// 예약자 기본 정보를 저장하는 로컬 사용자 캐시 엔티티
// 예약자 식별과 관리자 권한 검증, 단지 분리에 사용한다
@Entity
@Table(name = "user_cache")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCache extends BaseEntity {

    // 외부 원본 사용자 ID
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    // 소속 단지 ID
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

    // 이벤트 기반 캐시 값을 덮어쓸 때 사용한다
    public void apply(UserEventPayload payload) {
        this.id = payload.getUserId();
        this.complexId = payload.getApartmentComplexId();
        this.name = payload.getName();
        this.role = payload.getRole() == null ? null : UserCacheRole.valueOf(payload.getRole());
        this.status = payload.getStatus() == null ? null : UserCacheStatus.valueOf(payload.getStatus());
    }
}
