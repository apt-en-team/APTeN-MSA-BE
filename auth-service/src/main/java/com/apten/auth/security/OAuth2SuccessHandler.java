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
// 신규 사용자는 추가 정보 입력 페이지로, 기존 사용자는 JWT를 발급해 응답한다
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    // principal을 받아 실제 토큰 응답을 조합하는 서비스
    private final AuthService authService;

    @Override
    // OAuth2 인증이 끝난 뒤 신규/기존 사용자를 구분해 처리한다
    // 신규 사용자는 추가 정보 입력 페이지로 리다이렉트하고 기존 사용자는 JWT를 발급한다
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {
        if (authentication.getPrincipal() instanceof UserPrincipal principal) {

//            // 신규 소셜 사용자 — 추가 정보 입력 페이지로 리다이렉트
//            if ("NEW".equals(principal.getStatus())) {
//                String redirectUrl = "http://localhost:5173/social-signup"
//                        + "?email=" + principal.getEmail()
//                        + "&name=" + principal.getDisplayName();
//                getRedirectStrategy().sendRedirect(request, response, redirectUrl);
//                clearAuthenticationAttributes(request);
//                return;
//            }

            // 신규 소셜 사용자 — 추가 정보 입력이 필요함을 JSON으로 응답한다 (프론트 미구현 시 임시)
            if ("NEW".equals(principal.getStatus())) {
                response.setContentType("application/json;charset=UTF-8");
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write(
                        "{\"isNewUser\":true,\"email\":\"" + principal.getEmail()
                                + "\",\"name\":\"" + principal.getDisplayName() + "\"}"
                );
                clearAuthenticationAttributes(request);
                return;
            }

            // 기존 소셜 사용자 — JWT 발급 후 헤더에 담아 응답
            AuthLoginPostRes tokenResponse = authService.issueTokenResponse(principal);

            // 클라이언트가 바로 사용할 수 있도록 Authorization 헤더에 access token을 담는다
            response.setHeader(
                    SecurityConstants.AUTHORIZATION_HEADER,
                    SecurityConstants.BEARER_PREFIX + tokenResponse.getAccessToken()
            );

            // downstream 서비스가 사용자 식별과 권한을 재구성할 수 있도록 공통 헤더를 함께 내려준다
            response.setHeader(HeaderConstants.X_USER_ID, String.valueOf(principal.getUserId()));
            response.setHeader(HeaderConstants.X_USER_ROLE, principal.getRole().name());

            // 단지 ID가 있을 때만 헤더에 추가한다 (MASTER는 null이므로 추가하지 않는다)
            if (principal.getComplexId() != null) {
                response.setHeader(HeaderConstants.X_COMPLEX_ID, String.valueOf(principal.getComplexId()));
            }
        }

        // 인증 과정 중 임시로 사용한 속성은 성공 처리 후 정리한다
        clearAuthenticationAttributes(request);
    }
}