package com.apten.board.domain.entity;

import com.apten.common.entity.BaseEntity;
import com.apten.common.kafka.payload.UserEventPayload;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// board-service가 사용자 참조 데이터를 로컬 캐시 테이블로 유지하는 엔티티
@Entity
@Getter
@NoArgsConstructor
@Table(name = "user_cache")
public class UserCache extends BaseEntity {

    // 원본 사용자 ID를 그대로 캐시 PK로 사용한다
    @Id
    private Long userId;

    // 작성자 표시용 이름
    private String name;

    // 권한 표시용 역할
    private String role;

    // soft delete를 포함한 상태값
    private String status;

    // 단지 소속 식별자
    private Long apartmentComplexId;

    @Builder
    private UserCache(Long userId, String name, String role, String status, Long apartmentComplexId) {
        this.userId = userId;
        this.name = name;
        this.role = role;
        this.status = status;
        this.apartmentComplexId = apartmentComplexId;
    }

    // 같은 이벤트를 다시 받아도 같은 상태가 되도록 필드를 덮어쓴다
    public void apply(UserEventPayload payload) {
        this.userId = payload.getUserId();
        this.name = payload.getName();
        this.role = payload.getRole();
        this.status = payload.getStatus();
        this.apartmentComplexId = payload.getApartmentComplexId();
    }
}
