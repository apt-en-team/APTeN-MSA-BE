package com.apten.auth.security;

import com.apten.common.security.UserRole;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

// OAuth2 로그인 성공 후 Security가 들고 다니는 현재 사용자 정보
// 이후 SuccessHandler와 토큰 발급 단계가 이 객체를 기준으로 사용자 식별값을 읽는다
@Getter
@Builder
public class UserPrincipal implements OAuth2User {

    // auth-service 내부 사용자 식별값
    private final Long userId;

    // 외부 응답용 사용자 UID
    private final String userUid;

    // 후속 처리에서 참조할 기본 이메일 정보
    private final String email;

    // 응답에 내려줄 사용자 이름
    private final String displayName;

    // JWT와 내부 헤더에 실릴 사용자 역할
    private final UserRole role;

    // 로그인 응답에 내려줄 상태값
    private final String status;

    // OAuth2 공급자 응답에서 이름을 읽을 기준 키
    private final String nameAttributeKey;

    // OAuth2 공급자가 내려준 원본 사용자 속성
    private final Map<String, Object> attributes;

    @Override
    // Security 또는 후속 매핑 과정에서 원본 속성을 다시 읽을 때 사용
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    // 역할 정보를 Spring Security 권한 형식으로 바꿔 인증 상태에 담는다
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    // Security가 현재 사용자를 문자열로 식별할 때 사용하는 값
    // 공급자 이름 키가 없으면 내부 userId를 대신 사용한다
    public String getName() {
        Object attributeValue = attributes == null ? null : attributes.get(nameAttributeKey);
        if (attributeValue == null && displayName != null) {
            return displayName;
        }
        return attributeValue == null ? String.valueOf(userId) : String.valueOf(attributeValue);
    }
}
