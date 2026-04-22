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

// 토큰 인증과 사용자 헤더 전달 필터
@Component
@RequiredArgsConstructor
public class TokenAuthenticationFilter implements WebFilter {

    private static final String ROLE_CLAIM = "role";

    private final GatewayAuthProperties gatewayAuthProperties;
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
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

        UserRole userRole = UserRole.valueOf(role);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userId,
                token,
                List.of(new SimpleGrantedAuthority("ROLE_" + userRole.name()))
        );

        ServerHttpRequest mutatedRequest = exchange.getRequest()
                .mutate()
                .header(HeaderConstants.X_USER_ID, userId)
                .header(HeaderConstants.X_USER_ROLE, userRole.name())
                .build();

        SecurityContextImpl securityContext = new SecurityContextImpl(authentication);

        return chain.filter(exchange.mutate().request(mutatedRequest).build())
                .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)));
    }

    private boolean isExcludedPath(String path) {
        return gatewayAuthProperties.getExcludedPaths().stream()
                .anyMatch(pattern -> antPathMatcher.match(pattern, path));
    }

    private String resolveToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith(SecurityConstants.BEARER_PREFIX)) {
            throw new AuthenticationCredentialsNotFoundException(GatewayErrorCode.UNAUTHORIZED.getMessage());
        }

        return authorizationHeader.substring(SecurityConstants.BEARER_PREFIX.length());
    }

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

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(gatewayAuthProperties.getJwtSecret().getBytes(StandardCharsets.UTF_8));
    }
}
