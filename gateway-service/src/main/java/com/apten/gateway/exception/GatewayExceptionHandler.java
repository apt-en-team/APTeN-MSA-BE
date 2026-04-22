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

// 게이트웨이 공통 예외 응답 처리기
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class GatewayExceptionHandler implements WebExceptionHandler {

    private final ObjectMapper objectMapper;

    @Override
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

    private byte[] toJsonBytes(Object body) {
        try {
            return objectMapper.writeValueAsBytes(body);
        } catch (JsonProcessingException exception) {
            return "{\"success\":false,\"code\":\"GATEWAY_500_01\",\"message\":\"게이트웨이 내부 오류가 발생했습니다.\",\"data\":null}"
                    .getBytes(StandardCharsets.UTF_8);
        }
    }
}
