package com.apten.auth.security;

import com.apten.common.constants.HeaderConstants;
import com.apten.common.constants.SecurityConstants;
import com.apten.common.security.UserContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

// OAuth2 로그인 성공 처리기
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {
        if (authentication.getPrincipal() instanceof CustomUserPrincipal principal) {
            UserContext userContext = UserContext.builder()
                    .userId(principal.getUserId())
                    .userRole(principal.getRole())
                    .build();

            String accessToken = jwtTokenProvider.issueAccessToken(userContext);

            response.setHeader(SecurityConstants.AUTHORIZATION_HEADER, SecurityConstants.BEARER_PREFIX + accessToken);
            response.setHeader(HeaderConstants.X_USER_ID, String.valueOf(principal.getUserId()));
            response.setHeader(HeaderConstants.X_USER_ROLE, principal.getRole().name());
        }

        clearAuthenticationAttributes(request);
    }
}
