package com.apten.common.exception;

import lombok.Getter;

// 공통 비즈니스 예외
@Getter
public class BusinessException extends RuntimeException {

    // 에러코드 정보
    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
