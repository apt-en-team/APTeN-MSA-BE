package com.apten.common.response;

import lombok.Builder;
import lombok.Getter;

// 공통 응답 객체
@Getter
@Builder
public class ResultResponse<T> {

    // 성공 여부
    private final boolean success;

    // 응답 코드
    private final String code;

    // 응답 메시지
    private final String message;

    // 응답 데이터
    private final T data;

    public static <T> ResultResponse<T> success(String message, T data) {
        return ResultResponse.<T>builder()
                .success(true)
                .code("SUCCESS")
                .message(message)
                .data(data)
                .build();
    }

    public static ResultResponse<?> fail(String code, String message) {
        return ResultResponse.builder()
                .success(false)
                .code(code)
                .message(message)
                .data(null)
                .build();
    }
}