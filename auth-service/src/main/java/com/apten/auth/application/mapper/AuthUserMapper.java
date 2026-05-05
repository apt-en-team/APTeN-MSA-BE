package com.apten.auth.application.mapper;

import com.apten.auth.application.model.dto.AuthDto;
import com.apten.auth.application.model.response.AuthLoginPostRes;
import com.apten.auth.domain.entity.User;
import com.apten.auth.security.UserPrincipal;
import com.apten.common.security.UserContext;
import java.util.Map;
import org.springframework.stereotype.Component;

// 인증 흐름에서 엔티티와 principal, 응답 DTO를 이어주는 변환기
// 보안 계층과 응용 계층이 같은 객체를 직접 공유하지 않게 만든다
@Component
public class AuthUserMapper {

    // 회원 엔티티를 Security principal로 바꾼다
    public UserPrincipal toUserPrincipal(User user, Map<String, Object> attributes, String nameAttributeKey) {
        return UserPrincipal.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .displayName(user.getName())
                .role(user.getRole().toCommonUserRole())
                .status(user.getStatus().name())
//                .complexId(user.getComplexId())
                .nameAttributeKey(nameAttributeKey)
                .attributes(attributes)
                .build();
    }

    // 회원 엔티티를 내부 전달용 DTO로 바꾼다
    public AuthDto toDto(User user) {
        return AuthDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole())
                .status(user.getStatus())
                .signupType(user.getSignupType())
                .build();
    }

    // principal을 공통 사용자 컨텍스트로 바꾼다
    public UserContext toUserContext(UserPrincipal userPrincipal) {
        return UserContext.builder()
                .userId(userPrincipal.getUserId())
                .userRole(userPrincipal.getRole())
                .complexId(userPrincipal.getComplexId())
                .build();
    }

    // 토큰 발급 결과를 로그인 응답 객체로 바꾼다
    public AuthLoginPostRes toLoginResponse(
            String accessToken,
            String refreshToken,
            UserPrincipal userPrincipal
    ) {
        return AuthLoginPostRes.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(userPrincipal.getUserId())
                .userUid(userPrincipal.getUserUid())
                .name(userPrincipal.getDisplayName())
                .role(userPrincipal.getRole().name())
                .status(userPrincipal.getStatus())
                .build();
    }
}
