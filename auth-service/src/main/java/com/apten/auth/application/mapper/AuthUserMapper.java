package com.apten.auth.application.mapper;

import com.apten.auth.domain.entity.AuthUser;
import com.apten.auth.security.UserPrincipal;
import com.apten.common.security.UserContext;
import java.util.Map;
import org.springframework.stereotype.Component;

// 인증 흐름에서 엔티티, principal, 공통 컨텍스트 사이를 연결하는 변환기
// 각 계층이 서로의 내부 표현을 직접 알지 않게 하려는 목적의 매퍼다
@Component
public class AuthUserMapper {

    // 저장된 사용자를 Spring Security가 쓰는 principal 형태로 바꾼다
    // OAuth2 로그인 완료 후 Security 컨텍스트에 담길 사용자 표현을 만드는 단계다
    public UserPrincipal toUserPrincipal(AuthUser authUser, Map<String, Object> attributes, String nameAttributeKey) {
        return UserPrincipal.builder()
                .userId(authUser.getId())
                .email(authUser.getEmail())
                .role(authUser.getRole())
                .nameAttributeKey(nameAttributeKey)
                .attributes(attributes)
                .build();
    }

    // principal을 common의 UserContext로 바꿔 JWT 발급이나 헤더 전달에 재사용한다
    // auth-service와 gateway가 같은 사용자 구조를 공유할 수 있게 만드는 연결 지점이다
    public UserContext toUserContext(UserPrincipal userPrincipal) {
        return UserContext.builder()
                .userId(userPrincipal.getUserId())
                .userRole(userPrincipal.getRole())
                .build();
    }
}
