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

// OAuth2 로그인 사용자 정보
@Getter
@Builder
public class CustomUserPrincipal implements OAuth2User {

    private final Long userId;
    private final String email;
    private final UserRole role;
    private final String nameAttributeKey;
    private final Map<String, Object> attributes;

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getName() {
        Object attributeValue = attributes.get(nameAttributeKey);
        return attributeValue == null ? String.valueOf(userId) : String.valueOf(attributeValue);
    }
}
