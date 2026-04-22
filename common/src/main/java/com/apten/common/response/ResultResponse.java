package com.apten.common.response;

import lombok.Builder;
import lombok.Getter;

// 모든 서비스가 같은 JSON 응답 형태를 쓰도록 맞추는 공통 응답 객체
// 성공 응답과 실패 응답을 하나의 포맷으로 통일해 프런트와 gateway가 일관되게 해석할 수 있게 한다
@Getter
@Builder
public class ResultResponse<T> {

    // 요청이 성공했는지 실패했는지 나타내는 플래그
    private final boolean success;

    // SUCCESS 또는 서비스별 에러코드를 담는 응답 코드
    private final String code;

    // 사용자나 프런트가 바로 확인할 수 있는 메시지
    private final String message;

    // 실제 비즈니스 데이터가 담기는 위치
    private final T data;

    // 컨트롤러나 서비스가 성공 결과를 공통 포맷으로 내려줄 때 사용하는 팩토리 메서드
    // 각 서비스가 직접 JSON 구조를 만들지 않고 이 메서드를 통해 성공 응답을 맞춘다
    public static <T> ResultResponse<T> success(String message, T data) {
        return ResultResponse.<T>builder()
                .success(true)
                .code("SUCCESS")
                .message(message)
                .data(data)
                .build();
    }

    // 예외 처리기나 서비스가 실패 결과를 공통 포맷으로 내려줄 때 사용하는 팩토리 메서드
    // ErrorCode의 code와 message를 받아 실패 응답 형태를 일정하게 유지한다
    public static ResultResponse<?> fail(String code, String message) {
        return ResultResponse.builder()
                .success(false)
                .code(code)
                .message(message)
                .data(null)
                .build();
    }
}
