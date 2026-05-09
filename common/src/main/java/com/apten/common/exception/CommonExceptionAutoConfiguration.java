package com.apten.common.exception;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

// 공통 예외 처리기를 각 서비스가 자동으로 등록하도록 묶는 설정이다.
@AutoConfiguration
@Import(GlobalExceptionHandler.class)
public class CommonExceptionAutoConfiguration {
}
