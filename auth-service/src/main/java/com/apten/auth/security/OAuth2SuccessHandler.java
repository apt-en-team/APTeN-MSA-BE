package com.apten.auth.security;

import com.apten.auth.application.model.response.AuthLoginPostRes;
import com.apten.auth.application.service.AuthService;
import com.apten.common.constants.HeaderConstants;
import com.apten.common.constants.SecurityConstants;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

// OAuth2 로그인 성공 직후 JWT 발급과 사용자 헤더 구성을 이어주는 처리기
// Security 설정에서 이 클래스로 연결되면 로그인 성공 흐름이 auth-service 응답 단계로 넘어온다
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    // principal을 받아 실제 토큰 응답을 조합하는 서비스
    private final AuthService authService;

    @Override
    // OAuth2 인증이 끝난 뒤 principal을 읽고 access token을 응답 헤더에 실어 보낸다
    // 이후 gateway는 이 JWT를 받아 검증하고 각 서비스로 사용자 헤더를 전달하게 된다
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {
        if (authentication.getPrincipal() instanceof UserPrincipal principal) {
            AuthLoginPostRes tokenResponse = authService.issueTokenResponse(principal);

            // 클라이언트가 바로 사용할 수 있도록 Authorization 헤더에 access token을 담는다
            response.setHeader(
                    SecurityConstants.AUTHORIZATION_HEADER,
                    SecurityConstants.BEARER_PREFIX + tokenResponse.getAccessToken()
            );

            // downstream 서비스가 사용자 식별과 권한을 재구성할 수 있도록 공통 헤더를 함께 내려준다
            response.setHeader(HeaderConstants.X_USER_ID, String.valueOf(principal.getUserId()));
            response.setHeader(HeaderConstants.X_USER_ROLE, principal.getRole().name());
        }

        // 인증 과정 중 임시로 사용한 속성은 성공 처리 후 정리한다
        clearAuthenticationAttributes(request);
    }
}
