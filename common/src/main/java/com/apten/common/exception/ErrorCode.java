package com.apten.common.exception;

import org.springframework.http.HttpStatus;

// 공통 에러코드 계약
public interface ErrorCode {

    String getCode();
    String getMessage();
    HttpStatus getStatus();
}
