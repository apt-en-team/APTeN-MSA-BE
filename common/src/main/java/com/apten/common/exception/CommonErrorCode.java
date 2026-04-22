package com.apten.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

// 특정 서비스에 속하지 않는 공통 오류 상황을 정의하는 기본 enum
// 잘못된 파라미터나 내부 서비스 장애처럼 여러 서비스에서 반복되는 상황에 사용한다
@Getter
@RequiredArgsConstructor
public enum CommonErrorCode implements ErrorCode {

    // 서비스 내부에서 처리하지 못한 예외가 마지막까지 올라온 경우 사용
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_500", "서버 오류가 발생했습니다."),

    // 컨트롤러나 서비스에서 잘못된 입력값을 감지했을 때 사용
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "COMMON_400", "잘못된 요청 파라미터입니다."),

    // MSA 내부 호출에서 대상 서비스를 아예 사용할 수 없을 때 사용
    INTERNAL_SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "COMMON_503", "내부 서비스와 통신할 수 없습니다."),

    // 내부 서비스는 응답했지만 처리 과정에서 오류가 발생했을 때 사용
    INTERNAL_SERVICE_ERROR(HttpStatus.BAD_GATEWAY, "COMMON_502", "내부 서비스 처리 중 오류가 발생했습니다.");

    // HTTP 응답으로 그대로 내려갈 상태값
    private final HttpStatus status;

    // 프런트와 로그에서 공통으로 식별할 코드
    private final String code;

    // 기본 실패 메시지
    private final String message;

}
