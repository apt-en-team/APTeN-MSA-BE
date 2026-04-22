package com.apten.gateway.exception;

import com.apten.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

// 게이트웨이 전용 에러코드
@Getter
@RequiredArgsConstructor
public enum GatewayErrorCode implements ErrorCode {

    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "GATEWAY_401_01", "인증이 필요합니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "GATEWAY_401_02", "유효하지 않은 토큰입니다."),
    INVALID_ROUTE(HttpStatus.NOT_FOUND, "GATEWAY_404_01", "요청 경로를 찾을 수 없습니다."),
    TARGET_SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "GATEWAY_503_01", "대상 서비스와 연결할 수 없습니다."),
    GATEWAY_INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "GATEWAY_500_01", "게이트웨이 내부 오류가 발생했습니다.");

    // 응답 상태
    private final HttpStatus status;

    // 에러 코드
    private final String code;

    // 에러 메시지
    private final String message;
}
