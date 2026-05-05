package com.apten.auth.security;

import com.apten.auth.domain.entity.AdminProfile;
import com.apten.auth.domain.entity.ResidentProfile;
import com.apten.auth.domain.entity.User;
import com.apten.auth.domain.enums.UserRole;
import com.apten.auth.domain.repository.AdminProfileRepository;
import com.apten.auth.domain.repository.ResidentProfileRepository;
import com.apten.auth.domain.repository.UserRepository;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

// 소셜 로그인 성공 후 OAuth2 공급자로부터 사용자 정보를 읽어 내부 User와 연결하는 서비스
// 신규 소셜 사용자는 DB에 저장하지 않고 추가 정보 입력 페이지로 리다이렉트한다
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    // 이메일 기준으로 기존 사용자를 조회하는 저장소
    private final UserRepository userRepository;
    private final ResidentProfileRepository residentProfileRepository;
    private final AdminProfileRepository adminProfileRepository;

    private Long resolveComplexId(User user) {
        if (user == null) {
            return null;
        }

        if (user.getRole() == UserRole.USER) {
            return residentProfileRepository.findByUserId(user.getId())
                    .map(ResidentProfile::getComplexId)
                    .orElse(null);
        }

        if (user.getRole() == UserRole.MANAGER || user.getRole() == UserRole.ADMIN) {
            return adminProfileRepository.findByUserId(user.getId())
                    .map(AdminProfile::getComplexId)
                    .orElse(null);
        }

        return null;
    }

    @Override
    // OAuth2 공급자 응답에서 이메일을 추출해 기존 사용자와 연결하거나 신규 사용자로 표시한다
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 공급자 이름 (google, kakao, naver)
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        // 공급자별 이메일과 이름 추출
        String email = extractEmail(registrationId, oAuth2User.getAttributes());
        String name = extractName(registrationId, oAuth2User.getAttributes());

        // 이메일로 기존 사용자 조회 — 없으면 신규 사용자로 표시 (DB 저장 X)
        // 신규 사용자는 추가 정보 입력 완료 후 socialSignup()에서 저장한다
        var user = userRepository.findByEmail(email).orElse(null);
        boolean isNewUser = (user == null);

        // 신규 사용자는 USER 역할로, 기존 사용자는 DB 역할로 설정한다
        com.apten.common.security.UserRole commonRole = isNewUser
                ? com.apten.common.security.UserRole.USER
                : user.getRole().toCommonUserRole();

        return UserPrincipal.builder()
                .userId(isNewUser ? null : user.getId())
                .userUid(isNewUser ? null : String.valueOf(user.getId()))
                .email(email)
                .displayName(name)
                .role(commonRole)
                // 신규 사용자는 "NEW" 상태로 표시해 SuccessHandler에서 리다이렉트 여부를 판단한다
                .status(isNewUser ? "NEW" : user.getStatus().getValue())
                .complexId(isNewUser ? null : resolveComplexId(user))
                .nameAttributeKey(resolveNameAttributeKey(registrationId))
                .attributes(oAuth2User.getAttributes())
                .build();
    }

    // 공급자별 이메일 추출 — 카카오/네이버는 중첩 구조라 별도 처리 필요
    @SuppressWarnings("unchecked")
    private String extractEmail(String registrationId, Map<String, Object> attributes) {
        return switch (registrationId) {
            case "kakao" -> {
                Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
                yield (String) kakaoAccount.get("email");
            }
            case "naver" -> {
                Map<String, Object> response = (Map<String, Object>) attributes.get("response");
                yield (String) response.get("email");
            }
            default -> (String) attributes.get("email"); // google
        };
    }

    // 공급자별 이름 추출
    @SuppressWarnings("unchecked")
    private String extractName(String registrationId, Map<String, Object> attributes) {
        return switch (registrationId) {
            case "kakao" -> {
                Map<String, Object> profile = (Map<String, Object>) attributes.get("properties");
                yield (String) profile.get("nickname");
            }
            case "naver" -> {
                Map<String, Object> response = (Map<String, Object>) attributes.get("response");
                yield (String) response.get("name");
            }
            default -> (String) attributes.get("name"); // google
        };
    }

    // 공급자별 사용자 식별 기준 키 반환
    private String resolveNameAttributeKey(String registrationId) {
        return switch (registrationId) {
            case "kakao" -> "id";
            case "naver" -> "response";
            default -> "sub"; // google
        };
    }
}