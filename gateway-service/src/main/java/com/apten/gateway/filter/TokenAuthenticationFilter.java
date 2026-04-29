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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebExchangeDecorator;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class TokenAuthenticationFilter implements WebFilter {

    private static final String ROLE_CLAIM = "role";
    private static final String COMPLEX_ID_CLAIM = "complexId";

    private final GatewayAuthProperties gatewayAuthProperties;
    private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        // [공개 경로 확인] 인증 불필요 경로는 바로 통과
        String path = exchange.getRequest().getPath().value();
        if (isExcludedPath(path)) {
            return chain.filter(exchange);
        }

        // [토큰 추출] Authorization 헤더에서 Bearer 제거 후 순수 JWT 획득
        String token = resolveToken(
                exchange.getRequest().getHeaders().getFirst(SecurityConstants.AUTHORIZATION_HEADER)
        );

        // [토큰 파싱] 공유 secret key로 서명 검증 후 claim 맵 추출
        Claims claims = parseClaims(token);
        String userId = claims.getSubject();
        String role = claims.get(ROLE_CLAIM, String.class);

        // [필수 claim 검증] userId·role 중 하나라도 없으면 비정상 토큰으로 거부
        if (userId == null || role == null) {
            throw new BadCredentialsException(GatewayErrorCode.INVALID_TOKEN.getMessage());
        }

        // [role 변환] JWT role 문자열을 공통 UserRole enum으로 변환
        UserRole userRole = resolveUserRole(role);

        // [complexId 추출] Number 계열로 올 수 있으므로 longValue()로 안전하게 변환
        Object complexIdClaim = claims.get(COMPLEX_ID_CLAIM);
        final Long complexId = complexIdClaim instanceof Number number ? number.longValue() : null;

        // [블랙리스트 확인] Redis에서 로그아웃된 토큰인지 비동기 조회
        return reactiveRedisTemplate.hasKey("blacklist:" + token)
                .flatMap(isBlacklisted -> {

                    // [블랙리스트 차단] 로그아웃된 토큰이면 401 후 종료
                    if (Boolean.TRUE.equals(isBlacklisted)) {
                        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                        return exchange.getResponse().setComplete();
                    }

                    // [인증 객체 생성] Security 컨텍스트에 등록할 Authentication 생성
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userId,
                                    token,
                                    List.of(new SimpleGrantedAuthority("ROLE_" + userRole.name()))
                            );

                    // [요청 헤더 교체] ServerHttpRequestDecorator로 getHeaders()를 재정의
                    // exchange.mutate() / request.mutate() 는 내부적으로 ReadOnlyHttpHeaders를 건드려 터지므로 사용 금지
                    ServerHttpRequest decoratedRequest = new ServerHttpRequestDecorator(exchange.getRequest()) {
                        @Override
                        public HttpHeaders getHeaders() {
                            // [새 헤더맵] 기존 헤더를 복사해 쓰기 가능한 새 맵 생성
                            HttpHeaders headers = new HttpHeaders();
                            headers.addAll(super.getHeaders());

                            // [사용자 헤더 추가] downstream 서비스가 읽을 userId·role 세팅
                            headers.set(HeaderConstants.X_USER_ID, userId);
                            headers.set(HeaderConstants.X_USER_ROLE, userRole.name());

                            // [단지 ID 조건부 추가] claim 존재 시에만 추가
                            if (complexId != null) {
                                headers.set(HeaderConstants.X_COMPLEX_ID, String.valueOf(complexId));
                            }

                            return HttpHeaders.readOnlyHttpHeaders(headers);
                        }
                    };

                    // [exchange 교체] ServerWebExchangeDecorator로 getRequest()를 재정의
                    // exchange.mutate().build() 도 내부에서 헤더를 복사하므로 Decorator로 대체
                    ServerWebExchange decoratedExchange = new ServerWebExchangeDecorator(exchange) {
                        @Override
                        public ServerHttpRequest getRequest() {
                            return decoratedRequest;
                        }
                    };

                    // [컨텍스트 전파] 변경된 exchange와 인증 정보를 다음 필터·라우팅 단계로 전달
                    SecurityContextImpl securityContext = new SecurityContextImpl(authentication);
                    return chain.filter(decoratedExchange)
                            .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(
                                    Mono.just(securityContext)
                            ));
                });
    }

    // [공개 경로 판별] 설정 패턴과 현재 경로를 AntPathMatcher로 비교
    private boolean isExcludedPath(String path) {
        return gatewayAuthProperties.getExcludedPaths().stream()
                .anyMatch(pattern -> antPathMatcher.match(pattern, path));
    }

    // [Bearer 토큰 분리] 헤더 없거나 Bearer 아니면 즉시 인증 실패
    private String resolveToken(String authorizationHeader) {
        if (authorizationHeader == null
                || !authorizationHeader.startsWith(SecurityConstants.BEARER_PREFIX)) {
            throw new AuthenticationCredentialsNotFoundException(
                    GatewayErrorCode.UNAUTHORIZED.getMessage()
            );
        }
        return authorizationHeader.substring(SecurityConstants.BEARER_PREFIX.length());
    }

    // [JWT 파싱] 서명 검증 실패·만료 등 모든 JWT 예외를 BadCredentialsException으로 통일
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

    // [role 파싱] 알 수 없는 role이면 비정상 토큰으로 거부
    private UserRole resolveUserRole(String role) {
        try {
            return UserRole.valueOf(role);
        } catch (IllegalArgumentException exception) {
            throw new BadCredentialsException(GatewayErrorCode.INVALID_TOKEN.getMessage());
        }
    }

    // [서명 키 생성] auth-service와 동일한 secret key로 HMAC-SHA 검증 키 반환
    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(
                gatewayAuthProperties.getJwtSecret().getBytes(StandardCharsets.UTF_8)
        );
    }
}