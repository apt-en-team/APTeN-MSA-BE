package com.apten.auth.security;

import com.apten.auth.exception.AuthErrorCode;
import com.apten.common.constants.SecurityConstants;
import com.apten.common.exception.BusinessException;
import com.apten.common.security.UserContext;
import com.apten.common.security.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

// OAuth2 로그인으로 확보한 사용자 정보를 JWT로 바꾸고 다시 읽어오는 클래스
// auth-service가 토큰의 기준 서비스를 맡도록 발급과 검증 책임을 이곳에 모아둔다
@Component
public class JwtTokenProvider {

    // 사용자 역할을 JWT 안에 넣을 때 사용할 claim 키
    private static final String ROLE_CLAIM = "role";

    // 사용자가 속한 단지 ID를 JWT 안에 넣을 때 사용할 claim 키
    private static final String COMPLEX_ID_CLAIM = "complexId";

    // 서명과 검증에 사용할 비밀키
    private final SecretKey secretKey;

    // access token 만료 시간
    private final long accessTokenExpirationMillis;

    // refresh token 만료 시간
    private final long refreshTokenExpirationMillis;

    // 설정값을 받아 토큰 서명에 필요한 키와 만료 시간을 준비한다
    public JwtTokenProvider(
            @Value("${security.jwt.secret:apten-auth-service-secret-key-for-local-template-123456}") String secret,
            @Value("${security.jwt.access-token-expiration:3600000}") long accessTokenExpirationMillis,
            @Value("${security.jwt.refresh-token-expiration:1209600000}") long refreshTokenExpirationMillis
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpirationMillis = accessTokenExpirationMillis;
        this.refreshTokenExpirationMillis = refreshTokenExpirationMillis;
    }

    // 로그인 사용자의 ID, 역할, 단지 ID를 담은 access token을 발급한다
    public String issueAccessToken(UserContext userContext) {
        return issueToken(userContext.getUserId(), userContext.getUserRole(), userContext.getComplexId(), accessTokenExpirationMillis);
    }

    // 재발급 흐름에서 사용할 refresh token을 만든다
    public String issueRefreshToken(Long userId) {
        return issueToken(userId, null, null, refreshTokenExpirationMillis);
    }

    // 외부에서 전달받은 JWT가 만료되었는지 또는 위조되었는지 확인한다
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (ExpiredJwtException exception) {
            throw new BusinessException(AuthErrorCode.TOKEN_EXPIRED);
        } catch (JwtException | IllegalArgumentException exception) {
            throw new BusinessException(AuthErrorCode.INVALID_TOKEN);
        }
    }

    // JWT subject에 담긴 사용자 ID를 읽어 현재 사용자를 식별한다
    public Long getUserId(String token) {
        return Long.valueOf(parseClaims(token).getSubject());
    }

    // JWT claim에 담긴 역할 정보를 읽어 서비스 권한 판단에 넘긴다
    public UserRole getUserRole(String token) {
        String role = parseClaims(token).get(ROLE_CLAIM, String.class);
        if (role == null) {
            throw new BusinessException(AuthErrorCode.INVALID_TOKEN);
        }
        return UserRole.valueOf(role);
    }

    // JWT claim에 담긴 단지 ID를 읽어 단지 기준 데이터 접근 제어에 사용한다
    public Long getComplexId(String token) {
        return parseClaims(token).get(COMPLEX_ID_CLAIM, Long.class);
    }

    // JWT 만료 시각 반환 — 로그아웃 시 블랙리스트 TTL 계산에 사용
    public Date getExpiration(String token) {
        return parseClaims(token).getExpiration();
    }

    // Authorization 헤더에서 Bearer 접두사를 제거하고 실제 토큰 문자열만 꺼낸다
    public String resolveToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith(SecurityConstants.BEARER_PREFIX)) {
            throw new BusinessException(AuthErrorCode.INVALID_TOKEN);
        }
        return authorizationHeader.substring(SecurityConstants.BEARER_PREFIX.length());
    }

    // 응답 DTO에 넣을 토큰 타입 문자열을 통일한다
    public String resolveTokenType(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith(SecurityConstants.BEARER_PREFIX)) {
            return SecurityConstants.BEARER_PREFIX.trim();
        }
        return SecurityConstants.BEARER_PREFIX.trim();
    }

    // 공통 로직으로 access token과 refresh token을 모두 만든다
    // 역할과 단지 ID는 access token에만 넣고 refresh token에는 넣지 않는다
    private String issueToken(Long userId, UserRole userRole, Long complexId, long expirationMillis) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationMillis);

        var builder = Jwts.builder()
                .subject(String.valueOf(userId))
                .issuedAt(now)
                .expiration(expiration)
                .signWith(secretKey);

        if (userRole != null) {
            builder.claim(ROLE_CLAIM, userRole.name());
        }

        // 단지 ID가 있을 때만 claim에 추가한다 (MASTER는 null일 수 있다)
        if (complexId != null) {
            builder.claim(COMPLEX_ID_CLAIM, complexId);
        }

        return builder.compact();
    }

    // JWT를 파싱해 claim을 읽는다
    // 발급 이후 사용자 ID와 역할을 다시 꺼낼 때 모든 메서드가 이 단계를 공통으로 사용한다
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}