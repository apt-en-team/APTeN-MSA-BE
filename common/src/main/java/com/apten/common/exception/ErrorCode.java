package com.apten.common.exception;

import org.springframework.http.HttpStatus;

// auth-service, gateway-service, 각 도메인 서비스가 같은 방식으로 에러를 표현하기 위한 공통 규약
// 서비스별 enum이 이 인터페이스를 구현하면 GlobalExceptionHandler가 공통 응답으로 변환할 수 있다
public interface ErrorCode {

    // 클라이언트와 약속할 서비스별 에러 코드 문자열
    String getCode();

    // 응답 본문에 담길 기본 에러 메시지
    String getMessage();

    // HTTP 응답 상태로 변환할 상태값
    HttpStatus getStatus();
}
