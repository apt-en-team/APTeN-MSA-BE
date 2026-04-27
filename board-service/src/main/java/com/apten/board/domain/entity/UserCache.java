package com.apten.board.domain.entity;

import com.apten.board.domain.enums.UserCacheRole;
import com.apten.board.domain.enums.UserCacheStatus;
import com.apten.common.entity.BaseEntity;
import com.apten.common.kafka.payload.UserEventPayload;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 게시판 작성자 표시와 권한 검증에 사용하는 사용자 캐시 엔티티이다.
@Entity
@Table(
        name = "user_cache",
        indexes = {
                @Index(name = "idx_user_cache_complex_id", columnList = "complex_id"),
                @Index(name = "idx_user_cache_role", columnList = "role"),
                @Index(name = "idx_user_cache_status", columnList = "status")
        }
)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCache extends BaseEntity {

    // Auth 원본 사용자 ID이다.
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    // 사용자 소속 단지 ID이다.
    @Column(name = "complex_id", nullable = false)
    private Long complexId;

    // 사용자 이름이다.
    @Column(name = "name", nullable = false, length = 50)
    private String name;

    // 사용자 권한이다.
    @Builder.Default
    @Column(name = "role", nullable = false, length = 20)
    private UserCacheRole role = UserCacheRole.USER;

    // 사용자 상태이다.
    @Builder.Default
    @Column(name = "status", nullable = false, length = 20)
    private UserCacheStatus status = UserCacheStatus.ACTIVE;

    // 수신한 사용자 이벤트를 캐시에 반영한다.
    public void apply(UserEventPayload payload) {
        this.id = payload.getUserId();
        this.complexId = payload.getApartmentComplexId();
        this.name = payload.getName();
        if (payload.getRole() != null) {
            this.role = UserCacheRole.valueOf(payload.getRole());
        }
        if (payload.getStatus() != null) {
            this.status = UserCacheStatus.valueOf(payload.getStatus());
        }
    }
}
