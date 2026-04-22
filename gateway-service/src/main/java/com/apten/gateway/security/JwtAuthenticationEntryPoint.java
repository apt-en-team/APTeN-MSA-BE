package com.apten.gateway.security;

import com.apten.common.response.ResultResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.apten.gateway.exception.GatewayErrorCode;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

// 보호 경로에서 인증이 실패했을 때 공통 JSON 응답을 내려주는 진입점
// 토큰 없음 또는 잘못된 토큰 같은 상황을 gateway 전용 형식으로 정리한다
@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements ServerAuthenticationEntryPoint {

    // 인증 실패 응답을 JSON으로 만들 때 사용하는 객체
    private final ObjectMapper objectMapper;

    @Override
    // Security 체인에서 인증 예외가 발생하면 여기서 401 응답 본문을 만든다
    // 프런트가 HTML 오류 페이지 대신 공통 JSON 형식으로 실패를 받을 수 있게 하는 단계다
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException ex) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        byte[] responseBody = toJsonBytes(ResultResponse.fail(
                GatewayErrorCode.UNAUTHORIZED.getCode(),
                GatewayErrorCode.UNAUTHORIZED.getMessage()
        ));

        return exchange.getResponse()
                .writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(responseBody)));
    }

    // ResultResponse를 JSON 바이트로 바꿔 WebFlux 응답에 직접 쓴다
    // 직렬화 실패가 나도 최소한의 인증 실패 응답은 내려가도록 fallback 문자열을 유지한다
    private byte[] toJsonBytes(Object body) {
        try {
            return objectMapper.writeValueAsBytes(body);
        } catch (JsonProcessingException exception) {
            return "{\"success\":false,\"code\":\"GATEWAY_401_01\",\"message\":\"인증이 필요합니다.\",\"data\":null}"
                    .getBytes(StandardCharsets.UTF_8);
        }
    }
}
