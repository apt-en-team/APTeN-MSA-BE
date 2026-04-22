package com.apten.auth.entity;

import com.apten.common.entity.BaseEntity;
import com.apten.common.security.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 인증 사용자 모델
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthUser extends BaseEntity {

    private Long id;
    private String provider;
    private String providerUserId;
    private String email;
    private UserRole role;
}
