package com.apten.gateway.exception;

import com.apten.common.response.ResultResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.ConnectException;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

// gateway 내부에서 발생한 라우팅 오류와 연결 오류를 공통 응답으로 바꾸는 처리기
// auth 실패는 JwtAuthenticationEntryPoint가 맡고 그 외 gateway 예외는 여기서 정리한다
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class GatewayExceptionHandler implements WebExceptionHandler {

    // JSON 응답 직렬화에 사용하는 객체
    private final ObjectMapper objectMapper;

    @Override
    // 필터나 라우팅 단계에서 발생한 예외를 받아 gateway 전용 실패 응답으로 변환한다
    // downstream 서비스에 도달하지 못한 요청도 프런트가 같은 형식으로 해석할 수 있게 만든다
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        if (exchange.getResponse().isCommitted()) {
            return Mono.error(ex);
        }

        GatewayErrorCode errorCode = resolveErrorCode(ex);
        exchange.getResponse().setStatusCode(errorCode.getStatus());
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        byte[] responseBody = toJsonBytes(ResultResponse.fail(errorCode.getCode(), errorCode.getMessage()));

        return exchange.getResponse()
                .writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(responseBody)));
    }

    // 예외 타입에 따라 잘못된 경로인지 대상 서비스 장애인지 구분한다
    // gateway 역할에 맞는 실패 원인을 코드 단위로 나눠 응답하기 위한 단계다
    private GatewayErrorCode resolveErrorCode(Throwable ex) {
        if (ex instanceof NotFoundException) {
            return GatewayErrorCode.TARGET_SERVICE_UNAVAILABLE;
        }

        if (ex instanceof ConnectException) {
            return GatewayErrorCode.TARGET_SERVICE_UNAVAILABLE;
        }

        if (ex instanceof ResponseStatusException responseStatusException) {
            if (responseStatusException.getStatusCode() == HttpStatus.NOT_FOUND) {
                return GatewayErrorCode.INVALID_ROUTE;
            }

            if (responseStatusException.getStatusCode() == HttpStatus.SERVICE_UNAVAILABLE) {
                return GatewayErrorCode.TARGET_SERVICE_UNAVAILABLE;
            }
        }

        return GatewayErrorCode.GATEWAY_INTERNAL_ERROR;
    }

    // ResultResponse 객체를 JSON 바이트로 바꿔 WebFlux 응답 본문에 직접 쓴다
    // 직렬화 실패가 나도 최소한의 gateway 오류 응답은 내려가도록 fallback 문자열을 유지한다
    private byte[] toJsonBytes(Object body) {
        try {
            return objectMapper.writeValueAsBytes(body);
        } catch (JsonProcessingException exception) {
            return "{\"success\":false,\"code\":\"GATEWAY_500_01\",\"message\":\"게이트웨이 내부 오류가 발생했습니다.\",\"data\":null}"
                    .getBytes(StandardCharsets.UTF_8);
        }
    }
}
