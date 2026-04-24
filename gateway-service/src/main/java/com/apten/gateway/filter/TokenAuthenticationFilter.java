package com.apten.gateway.filter;

import com.apten.common.constants.HeaderConstants;
import com.apten.common.constants.SecurityConstants;
import com.apten.common.security.UserRole;
import com.apten.gateway.config.GatewayAuthProperties;
import com.apten.gateway.exception.GatewayErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.List;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

// 보호 경로 요청에서 JWT를 확인하고 downstream 서비스용 사용자 헤더를 추가하는 필터
// gateway는 토큰을 발급하지 않고 auth-service가 만든 JWT를 읽어 전달 책임만 수행한다
@Component
@RequiredArgsConstructor
public class TokenAuthenticationFilter implements WebFilter {

    // auth-service가 access token 안에 넣어 주는 사용자 역할 claim 키
    private static final String ROLE_CLAIM = "role";

    // 공개 경로와 JWT 비밀키를 읽기 위한 설정 객체
    private final GatewayAuthProperties gatewayAuthProperties;

    // 로그아웃된 AT 블랙리스트 확인용 Redis 클라이언트
    private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

    // 제외 경로 패턴 매칭에 사용하는 유틸
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    // 요청 경로가 공개 경로인지 먼저 확인하고 아니면 JWT를 해석해 사용자 헤더를 붙인다
    // 이후 각 서비스는 X-User-Id와 X-User-Role만 읽어 현재 로그인 사용자를 식별할 수 있다
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getPath().value();
        if (isExcludedPath(path)) {
            return chain.filter(exchange);
        }

        String token = resolveToken(exchange.getRequest().getHeaders().getFirst(SecurityConstants.AUTHORIZATION_HEADER));
        Claims claims = parseClaims(token);
        String userId = claims.getSubject();
        String role = claims.get(ROLE_CLAIM, String.class);

        if (userId == null || role == null) {
            throw new BadCredentialsException(GatewayErrorCode.INVALID_TOKEN.getMessage());
        }

        UserRole userRole = resolveUserRole(role);

        // 로그아웃된 토큰인지 Redis 블랙리스트 확인 — 있으면 401 반환
        return reactiveRedisTemplate.hasKey("blacklist:" + token)
                .flatMap(isBlacklisted -> {
                    if (Boolean.TRUE.equals(isBlacklisted)) {
                        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                        return exchange.getResponse().setComplete();
                    }

                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userId,
                            token,
                            List.of(new SimpleGrantedAuthority("ROLE_" + userRole.name()))
                    );

                    // gateway를 통과한 뒤 각 서비스가 바로 읽을 수 있도록 공통 헤더를 요청에 추가한다
                    ServerHttpRequest mutatedRequest = exchange.getRequest()
                            .mutate()
                            .header(HeaderConstants.X_USER_ID, userId)
                            .header(HeaderConstants.X_USER_ROLE, userRole.name())
                            .build();

                    SecurityContextImpl securityContext = new SecurityContextImpl(authentication);

                    // Security 컨텍스트에도 현재 사용자 정보를 남겨 이후 보안 체인이 인증 완료 상태로 이어지게 한다
                    return chain.filter(exchange.mutate().request(mutatedRequest).build())
                            .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)));
                });
    }

    // 현재 요청 경로가 인증 없이 통과 가능한 공개 경로인지 확인한다
    // auth-service 로그인 경로와 oauth2 콜백 경로는 여기서 먼저 제외된다
    private boolean isExcludedPath(String path) {
        return gatewayAuthProperties.getExcludedPaths().stream()
                .anyMatch(pattern -> antPathMatcher.match(pattern, path));
    }

    // Authorization 헤더에서 Bearer 토큰만 분리한다
    // 보호 경로에 토큰이 없으면 여기서 바로 인증 실패로 처리한다
    private String resolveToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith(SecurityConstants.BEARER_PREFIX)) {
            throw new AuthenticationCredentialsNotFoundException(GatewayErrorCode.UNAUTHORIZED.getMessage());
        }

        return authorizationHeader.substring(SecurityConstants.BEARER_PREFIX.length());
    }

    // auth-service가 만든 JWT를 같은 비밀키 기준으로 파싱해 claim을 읽는다
    // gateway는 발급 기준 서비스가 아니므로 토큰 구조를 읽는 최소 책임만 가진다
    private Claims parseClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException | IllegalArgumentException exception) {
            throw new BadCredentialsException(GatewayErrorCode.INVALID_TOKEN.getMessage());
        }
    }

    // role claim 값을 common의 UserRole enum으로 맞춰 downstream 헤더 기준을 통일한다
    // 예상하지 못한 role 문자열이 오면 잘못된 토큰으로 본다
    private UserRole resolveUserRole(String role) {
        try {
            return UserRole.valueOf(role);
        } catch (IllegalArgumentException exception) {
            throw new BadCredentialsException(GatewayErrorCode.INVALID_TOKEN.getMessage());
        }
    }

    // auth-service와 맞춰 둔 비밀키로 JWT 서명 검증 키를 생성한다
    // 두 서비스가 같은 기준을 봐야 gateway가 발급된 토큰을 신뢰하고 해석할 수 있다
    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(gatewayAuthProperties.getJwtSecret().getBytes(StandardCharsets.UTF_8));
    }
}