package com.apten.household.domain.entity;

import com.apten.common.entity.BaseEntity;
import com.apten.household.domain.enums.UserCacheRole;
import com.apten.household.domain.enums.UserCacheStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 세대 매칭과 세대원 연결에 사용할 사용자 캐시 엔티티
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "user_cache",
        indexes = {
                @Index(name = "idx_user_cache_complex_id", columnList = "complex_id"),
                @Index(name = "idx_user_cache_role", columnList = "role"),
                @Index(name = "idx_user_cache_status", columnList = "status")
        }
)
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

    // 연락처
    @Column(name = "phone", nullable = false, length = 20)
    private String phone;

    // 생년월일
    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    // 사용자 권한
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private UserCacheRole role;

    // 사용자 상태
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private UserCacheStatus status;

    // 사용자 캐시 내용을 갱신한다
    public void apply(
            Long complexId,
            String name,
            String phone,
            LocalDate birthDate,
            UserCacheRole role,
            UserCacheStatus status
    ) {
        this.complexId = complexId;
        this.name = name;
        this.phone = phone;
        this.birthDate = birthDate;
        this.role = role;
        this.status = status;
    }
}
