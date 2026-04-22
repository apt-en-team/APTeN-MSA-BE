package com.apten.board.domain.entity;

import com.apten.board.domain.enums.UserCacheRole;
import com.apten.board.domain.enums.UserCacheStatus;
import com.apten.common.entity.BaseEntity;
import com.apten.common.kafka.payload.UserEventPayload;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 게시판 작성자 표시용 사용자 캐시 엔티티
// Auth Service 이벤트를 받아 작성자 정보와 권한 표시 기준을 유지한다
@Entity
@Getter
@NoArgsConstructor
@Table(name = "user_cache")
public class UserCache extends BaseEntity {

    // 원본 사용자 ID를 그대로 캐시 PK로 사용한다
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    // 소속 단지 ID
    @Column(name = "complex_id", nullable = false)
    private Long complexId;

    // 작성자 표시용 이름
    @Column(name = "name", nullable = false, length = 50)
    private String name;

    // 권한 표시용 역할
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private UserCacheRole role;

    // soft delete를 포함한 상태값
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private UserCacheStatus status;

    @Builder
    private UserCache(Long id, Long complexId, String name, UserCacheRole role, UserCacheStatus status) {
        this.id = id;
        this.complexId = complexId;
        this.name = name;
        this.role = role;
        this.status = status;
    }

    // 같은 이벤트를 다시 받아도 같은 상태가 되도록 필드를 덮어쓴다
    public void apply(UserEventPayload payload) {
        this.id = payload.getUserId();
        this.complexId = payload.getApartmentComplexId();
        this.name = payload.getName();
        this.role = payload.getRole() == null ? null : UserCacheRole.valueOf(payload.getRole());
        this.status = payload.getStatus() == null ? null : UserCacheStatus.valueOf(payload.getStatus());
    }
}
