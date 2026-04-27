package com.apten.apartmentcomplex.domain.entity;

import com.apten.apartmentcomplex.domain.enums.UserCacheRole;
import com.apten.apartmentcomplex.domain.enums.UserCacheStatus;
import com.apten.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// Auth Service의 user 원본 이벤트를 받아 단지 서비스 로컬에 보관하는 사용자 캐시 엔티티이다.
// 관리자 단지 소속 지정 시 실제 ADMIN 계정 존재 여부를 확인할 때 사용한다.
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

    // 사용자 소속 단지 ID이며 Auth Service 원본 기준으로 항상 함께 들어온다
    @Column(name = "complex_id", nullable = false)
    private Long complexId;

    // 사용자 이름
    @Column(name = "name", nullable = false, length = 50)
    private String name;

    // 사용자 권한은 converter를 통해 DB에는 code로 저장된다
    @Column(name = "role", nullable = false, length = 20)
    private UserCacheRole role;

    // 사용자 상태는 converter를 통해 DB에는 code로 저장된다
    @Column(name = "status", nullable = false, length = 20)
    private UserCacheStatus status;

    // 소프트 삭제 여부이며 MySQL TINYINT(1)로 저장된다
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    // user 이벤트를 반영해 캐시 값을 upsert할 때 사용한다
    public void apply(Long complexId, String name, UserCacheRole role, UserCacheStatus status, Boolean isDeleted) {
        this.complexId = complexId;
        this.name = name;
        this.role = role;
        this.status = status;
        this.isDeleted = isDeleted;
    }
}
