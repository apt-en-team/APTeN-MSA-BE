package com.apten.auth.security;

import com.apten.auth.application.model.response.AuthLoginPostRes;
import com.apten.auth.application.service.AuthService;
import com.apten.auth.domain.entity.ResidentProfile;
import com.apten.auth.domain.repository.ResidentProfileRepository;
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

    // USER 역할 입주민의 동/호 조회용
    private final ResidentProfileRepository residentProfileRepository;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {
        if (authentication.getPrincipal() instanceof UserPrincipal principal) {

            // 신규 소셜 사용자 — 추가 정보 입력 페이지로 리다이렉트
            if ("NEW".equals(principal.getStatus())) {
                String redirectUrl = "http://localhost:5173/social/signup"
                        + "?email=" + principal.getEmail()
                        + "&name=" + java.net.URLEncoder.encode(principal.getDisplayName(), "UTF-8");
                getRedirectStrategy().sendRedirect(request, response, redirectUrl);
                clearAuthenticationAttributes(request);
                return;
            }

//            // 신규 소셜 사용자 — 추가 정보 입력이 필요함을 JSON으로 응답한다 (프론트 미구현 시 임시)
//            if ("NEW".equals(principal.getStatus())) {
//                response.setContentType("application/json;charset=UTF-8");
//                response.setStatus(HttpServletResponse.SC_OK);
//                response.getWriter().write(
//                        "{\"isNewUser\":true,\"email\":\"" + principal.getEmail()
//                                + "\",\"name\":\"" + principal.getDisplayName() + "\"}"
//                );
//                clearAuthenticationAttributes(request);
//                return;
//            }

            // 기존 소셜 사용자 — JWT 발급 후 프론트로 리다이렉트
            AuthLoginPostRes tokenResponse = authService.issueTokenResponse(principal);

            // USER 역할인 경우에만 resident_profile에서 동/호 조회
            String building = "";
            String unit = "";
            if (principal.getRole() == com.apten.common.security.UserRole.USER && principal.getUserId() != null) {
                building = residentProfileRepository.findByUserId(principal.getUserId())
                        .map(ResidentProfile::getBuilding)
                        .orElse("");
                unit = residentProfileRepository.findByUserId(principal.getUserId())
                        .map(ResidentProfile::getUnit)
                        .orElse("");
            }

            // 토큰과 사용자 정보를 쿼리 파라미터로 프론트에 전달
            String redirectUrl = "http://localhost:5173/social/callback"
                    + "?accessToken=" + tokenResponse.getAccessToken()
                    + "&refreshToken=" + tokenResponse.getRefreshToken()
                    + "&userId=" + tokenResponse.getUserId()
                    + "&userUid=" + tokenResponse.getUserUid()
                    + "&name=" + java.net.URLEncoder.encode(tokenResponse.getName(), "UTF-8")
                    + "&role=" + tokenResponse.getRole()
                    + "&status=" + tokenResponse.getStatus()
                    + "&building=" + java.net.URLEncoder.encode(building, "UTF-8")
                    + "&unit=" + java.net.URLEncoder.encode(unit, "UTF-8")
                    + "&complexId=" + tokenResponse.getComplexId();

            getRedirectStrategy().sendRedirect(request, response, redirectUrl);
        }

        // 인증 과정 중 임시로 사용한 속성은 성공 처리 후 정리한다
        clearAuthenticationAttributes(request);
    }
}