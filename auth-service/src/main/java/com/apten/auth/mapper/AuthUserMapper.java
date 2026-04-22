package com.apten.auth.mapper;

import com.apten.auth.entity.AuthUser;
import com.apten.auth.security.UserPrincipal;
import com.apten.common.security.UserContext;
import java.util.Map;
import org.springframework.stereotype.Component;

// 인증 사용자 변환기
@Component
public class AuthUserMapper {

    // 엔티티를 로그인 사용자 정보로 변환
    public UserPrincipal toUserPrincipal(AuthUser authUser, Map<String, Object> attributes, String nameAttributeKey) {
        return UserPrincipal.builder()
                .userId(authUser.getId())
                .email(authUser.getEmail())
                .role(authUser.getRole())
                .nameAttributeKey(nameAttributeKey)
                .attributes(attributes)
                .build();
    }

    // 로그인 사용자 정보를 공통 컨텍스트로 변환
    public UserContext toUserContext(UserPrincipal userPrincipal) {
        return UserContext.builder()
                .userId(userPrincipal.getUserId())
                .userRole(userPrincipal.getRole())
                .build();
    }
}
