package com.apten.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

// 공통 기본 에러코드
@Getter
@RequiredArgsConstructor
public enum CommonErrorCode implements ErrorCode {

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_500", "서버 오류가 발생했습니다."),
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "COMMON_400", "잘못된 요청 파라미터입니다."),
    INTERNAL_SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "COMMON_503", "내부 서비스와 통신할 수 없습니다."),
    INTERNAL_SERVICE_ERROR(HttpStatus.BAD_GATEWAY, "COMMON_502", "내부 서비스 처리 중 오류가 발생했습니다.");

    // HTTP 상태값
    private final HttpStatus status;

    // 공통 코드
    private final String code;

    // 기본 메시지
    private final String message;

}
