package com.apten.board.filter;

import com.apten.common.constants.HeaderConstants;
import com.apten.common.security.UserContext;
import com.apten.common.security.UserContextHolder;
import com.apten.common.security.UserRole;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

// gateway가 전달한 사용자 헤더를 읽어 현재 요청의 UserContext를 복원하는 필터
// board-service는 JWT를 직접 해석하지 않고 이 필터로 공통 사용자 정보를 받아 사용한다
@Slf4j
@Component
public class UserContextFilter extends OncePerRequestFilter {

    @Override
    // 모든 요청에서 헤더를 먼저 확인하고 UserContextHolder에 저장한 뒤 컨트롤러와 서비스로 넘긴다
    // 요청이 끝나면 finally에서 clear를 호출해 다음 요청과 사용자 정보가 섞이지 않게 막는다
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            UserContext userContext = resolveUserContext(request);
            if (userContext != null) {
                UserContextHolder.set(userContext);
            }

            filterChain.doFilter(request, response);
        } finally {
            UserContextHolder.clear();
        }
    }

    // gateway가 추가한 X-User-Id와 X-User-Role 헤더를 읽어 공통 UserContext로 바꾼다
    // 공개 경로나 사용자 헤더가 없는 요청은 null을 반환해 익명 요청으로 그대로 통과시킨다
    private UserContext resolveUserContext(HttpServletRequest request) {
        String userIdHeader = request.getHeader(HeaderConstants.X_USER_ID);
        String userRoleHeader = request.getHeader(HeaderConstants.X_USER_ROLE);

        if (userIdHeader == null || userRoleHeader == null) {
            return null;
        }

        try {
            return UserContext.builder()
                    .userId(Long.valueOf(userIdHeader))
                    .userRole(UserRole.valueOf(userRoleHeader))
                    .build();
        } catch (IllegalArgumentException exception) {
            log.warn("Invalid user headers. userId={}, userRole={}", userIdHeader, userRoleHeader);
            return null;
        }
    }
}
