package com.apten.gateway.filter;

import com.apten.common.constants.HeaderConstants;
import com.apten.common.constants.SecurityConstants;
import com.apten.common.security.UserRole;
import com.apten.gateway.config.GatewayAuthProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

// JWT 기반 사용자 헤더 전달 필터
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private static final String ROLE_CLAIM = "role";

    private final GatewayAuthProperties gatewayAuthProperties;
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().value();
        if (isExcludedPath(path)) {
            return chain.filter(exchange);
        }

        String authorizationHeader = exchange.getRequest().getHeaders().getFirst(SecurityConstants.AUTHORIZATION_HEADER);
        if (authorizationHeader == null || !authorizationHeader.startsWith(SecurityConstants.BEARER_PREFIX)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        try {
            Claims claims = parseClaims(resolveToken(authorizationHeader));
            String userId = claims.getSubject();
            String role = claims.get(ROLE_CLAIM, String.class);

            if (userId == null || role == null) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            UserRole.valueOf(role);

            ServerHttpRequest mutatedRequest = exchange.getRequest()
                    .mutate()
                    .header(HeaderConstants.X_USER_ID, userId)
                    .header(HeaderConstants.X_USER_ROLE, role)
                    .build();

            return chain.filter(exchange.mutate().request(mutatedRequest).build());
        } catch (JwtException | IllegalArgumentException exception) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    @Override
    public int getOrder() {
        return -1;
    }

    private boolean isExcludedPath(String path) {
        return gatewayAuthProperties.getExcludedPaths().stream()
                .anyMatch(pattern -> antPathMatcher.match(pattern, path));
    }

    private String resolveToken(String authorizationHeader) {
        return authorizationHeader.substring(SecurityConstants.BEARER_PREFIX.length());
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(gatewayAuthProperties.getJwtSecret().getBytes(StandardCharsets.UTF_8));
    }
}
