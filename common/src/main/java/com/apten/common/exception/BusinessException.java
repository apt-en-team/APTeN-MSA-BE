package com.apten.common.exception;

import lombok.Getter;

// 서비스 코드에서 의도적으로 던지는 공통 비즈니스 예외
// auth-service나 각 도메인 서비스가 ErrorCode와 함께 던지면 공통 응답 구조로 변환된다
@Getter
public class BusinessException extends RuntimeException {

    // 어떤 ErrorCode로 응답해야 하는지 담는 핵심 정보
    private final ErrorCode errorCode;

    // 서비스 계층에서 ErrorCode만 넘기면 메시지까지 함께 세팅한다
    // GlobalExceptionHandler는 이 값을 읽어 표준 실패 응답을 만든다
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
