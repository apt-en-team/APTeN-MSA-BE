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

// JWT 발급과 검증 지원 클래스
@Component
public class JwtTokenProvider {

    private static final String ROLE_CLAIM = "role";

    private final SecretKey secretKey;
    private final long accessTokenExpirationMillis;
    private final long refreshTokenExpirationMillis;

    public JwtTokenProvider(
            @Value("${security.jwt.secret:apten-auth-service-secret-key-for-local-template-123456}") String secret,
            @Value("${security.jwt.access-token-expiration:3600000}") long accessTokenExpirationMillis,
            @Value("${security.jwt.refresh-token-expiration:1209600000}") long refreshTokenExpirationMillis
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpirationMillis = accessTokenExpirationMillis;
        this.refreshTokenExpirationMillis = refreshTokenExpirationMillis;
    }

    // 액세스 토큰 발급
    public String issueAccessToken(UserContext userContext) {
        return issueToken(userContext.getUserId(), userContext.getUserRole(), accessTokenExpirationMillis);
    }

    // 리프레시 토큰 발급
    public String issueRefreshToken(Long userId) {
        return issueToken(userId, null, refreshTokenExpirationMillis);
    }

    // 토큰 유효성 검사
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

    // 토큰에서 사용자 ID 조회
    public Long getUserId(String token) {
        return Long.valueOf(parseClaims(token).getSubject());
    }

    // 토큰에서 사용자 역할 조회
    public UserRole getUserRole(String token) {
        String role = parseClaims(token).get(ROLE_CLAIM, String.class);
        if (role == null) {
            throw new BusinessException(AuthErrorCode.INVALID_TOKEN);
        }
        return UserRole.valueOf(role);
    }

    // Authorization 헤더에서 토큰만 추출
    public String resolveToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith(SecurityConstants.BEARER_PREFIX)) {
            throw new BusinessException(AuthErrorCode.INVALID_TOKEN);
        }
        return authorizationHeader.substring(SecurityConstants.BEARER_PREFIX.length());
    }

    // Authorization 헤더에서 토큰 타입 확인
    public String resolveTokenType(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith(SecurityConstants.BEARER_PREFIX)) {
            return SecurityConstants.BEARER_PREFIX.trim();
        }
        return SecurityConstants.BEARER_PREFIX.trim();
    }

    private String issueToken(Long userId, UserRole userRole, long expirationMillis) {
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

        return builder.compact();
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
