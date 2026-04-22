package com.apten.notification.exception;

import com.apten.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

// 알림 서비스 에러코드
@Getter
@RequiredArgsConstructor
public enum NotificationErrorCode implements ErrorCode {

    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "NTF_404_01", "알림을 찾을 수 없습니다."),
    PUSH_SUBSCRIPTION_NOT_FOUND(HttpStatus.NOT_FOUND, "NTF_404_02", "웹푸시 구독 정보를 찾을 수 없습니다."),
    PUSH_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "NTF_500_01", "웹푸시 알림 발송에 실패했습니다.");

    // HTTP 상태값
    private final HttpStatus status;

    // 서비스 코드
    private final String code;

    // 기본 메시지
    private final String message;

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public HttpStatus getStatus() {
        return status;
    }
}
