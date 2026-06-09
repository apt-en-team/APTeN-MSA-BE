package com.apten.common.exception;

import com.apten.common.response.ResultResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.async.AsyncRequestNotUsableException;

@Slf4j
@RestControllerAdvice
// 서비스에서 발생한 예외를 공통 응답 형태로 바꿔 주는 전역 처리기
// auth-service, gateway-service, 각 도메인 서비스가 같은 실패 형식을 유지하도록 돕는다
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    // 서비스가 명시적으로 던진 BusinessException을 ResultResponse.fail 구조로 변환한다
    // 서비스별 ErrorCode를 그대로 읽어 상태 코드와 응답 코드를 통일한다
    public ResponseEntity<ResultResponse<?>> handleBusinessException(BusinessException e) {
        ErrorCode errorCode = e.getErrorCode();

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ResultResponse.fail(errorCode.getCode(), errorCode.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    // 컨트롤러나 서비스에서 잘못된 입력값이 들어왔을 때 공통 400 응답으로 내려준다
    // 초기에 검증 로직이 늘어나도 응답 형식이 흔들리지 않도록 묶어두는 단계다
    public ResponseEntity<ResultResponse<?>> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("IllegalArgumentException: {}", e.getMessage());

        return ResponseEntity
                .status(CommonErrorCode.INVALID_PARAMETER.getStatus())
                .body(ResultResponse.fail(
                        CommonErrorCode.INVALID_PARAMETER.getCode(),
                        CommonErrorCode.INVALID_PARAMETER.getMessage()
                ));
    }

    @ExceptionHandler(IOException.class)
    // SSE/스트리밍 연결에서 클라이언트가 끊으면 Broken pipe IOException이 발생한다.
    // 응답 본문을 쓰지 않고 무시해 HttpMessageNotWritableException 연쇄 발생을 막는다.
    public ResponseEntity<Void> handleIOException(IOException e) {
        log.debug("Client disconnected (Broken pipe): {}", e.getMessage());
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(Exception.class)
    // 처리되지 않은 예외를 마지막에서 잡아 공통 500 응답으로 변환한다
    // 예외가 그대로 HTML 오류 페이지나 서비스별 제각각인 형태로 새지 않게 막아준다
    public ResponseEntity<ResultResponse<?>> handleException(Exception e) {
        // SSE 연결 끊김으로 인한 AsyncRequestNotUsableException은 응답 쓰기가 불가능하므로 무시한다
        if (e instanceof AsyncRequestNotUsableException) {
            log.debug("SSE client disconnected: {}", e.getMessage());
            return null;
        }
        log.error("Unhandled exception", e);

        return ResponseEntity
                .status(CommonErrorCode.INTERNAL_SERVER_ERROR.getStatus())
                .body(ResultResponse.fail(
                        CommonErrorCode.INTERNAL_SERVER_ERROR.getCode(),
                        CommonErrorCode.INTERNAL_SERVER_ERROR.getMessage()
                ));
    }
}
