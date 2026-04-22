package com.apten.common.exception;

import com.apten.common.response.ResultResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
// 공통 예외 처리기
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    // 비즈니스 예외 처리
    public ResponseEntity<ResultResponse<?>> handleBusinessException(BusinessException e) {
        ErrorCode errorCode = e.getErrorCode();

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ResultResponse.fail(errorCode.getCode(), errorCode.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    // 잘못된 인자 예외 처리
    public ResponseEntity<ResultResponse<?>> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("IllegalArgumentException: {}", e.getMessage());

        return ResponseEntity
                .status(CommonErrorCode.INVALID_PARAMETER.getStatus())
                .body(ResultResponse.fail(
                        CommonErrorCode.INVALID_PARAMETER.getCode(),
                        CommonErrorCode.INVALID_PARAMETER.getMessage()
                ));
    }

    @ExceptionHandler(Exception.class)
    // 처리되지 않은 예외 처리
    public ResponseEntity<ResultResponse<?>> handleException(Exception e) {
        log.error("Unhandled exception", e);

        return ResponseEntity
                .status(CommonErrorCode.INTERNAL_SERVER_ERROR.getStatus())
                .body(ResultResponse.fail(
                        CommonErrorCode.INTERNAL_SERVER_ERROR.getCode(),
                        CommonErrorCode.INTERNAL_SERVER_ERROR.getMessage()
                ));
    }
}