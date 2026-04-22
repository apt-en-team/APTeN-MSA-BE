package com.apten.gateway.exception;

import com.apten.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

// gateway에서만 발생하는 인증 실패와 라우팅 실패 상황을 구분하기 위한 전용 에러코드
// auth-service의 인증 기준을 따르되 응답 책임은 gateway 내부에서 분리해 관리한다
@Getter
@RequiredArgsConstructor
public enum GatewayErrorCode implements ErrorCode {

    // 보호 경로인데 토큰이 아예 없는 경우 사용
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "GATEWAY_401_01", "인증이 필요합니다."),

    // 토큰 형식이 잘못됐거나 서명 검증에 실패한 경우 사용
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "GATEWAY_401_02", "유효하지 않은 토큰입니다."),

    // 어떤 서비스 라우트에도 매칭되지 않는 경로 요청에 사용
    INVALID_ROUTE(HttpStatus.NOT_FOUND, "GATEWAY_404_01", "요청 경로를 찾을 수 없습니다."),

    // 라우팅은 됐지만 대상 서비스에 실제로 연결할 수 없을 때 사용
    TARGET_SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "GATEWAY_503_01", "대상 서비스와 연결할 수 없습니다."),

    // gateway 내부 처리 중 예기치 않은 오류가 발생했을 때 사용하는 최종 오류 코드
    GATEWAY_INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "GATEWAY_500_01", "게이트웨이 내부 오류가 발생했습니다.");

    // HTTP 응답 상태
    private final HttpStatus status;

    // 프런트와 로그에서 식별할 게이트웨이 전용 코드
    private final String code;

    // 응답 본문에 내려줄 기본 메시지
    private final String message;
}
