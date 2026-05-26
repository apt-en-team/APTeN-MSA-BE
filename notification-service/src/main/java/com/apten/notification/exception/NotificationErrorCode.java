package com.apten.notification.exception;

import com.apten.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

// 알림 서비스 에러코드
@Getter
@RequiredArgsConstructor
public enum NotificationErrorCode implements ErrorCode {

    // 요청값 검증 실패
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "NTF_400_01", "잘못된 알림 요청입니다."),
    // 알림 type 문자열이 NotificationType으로 변환되지 않는 경우
    INVALID_NOTIFICATION_TYPE(HttpStatus.BAD_REQUEST, "NTF_400_02", "알림 유형이 올바르지 않습니다."),
    // 알림 대상 type 문자열이 NotificationTargetType으로 변환되지 않는 경우
    INVALID_NOTIFICATION_TARGET_TYPE(HttpStatus.BAD_REQUEST, "NTF_400_03", "알림 대상 유형이 올바르지 않습니다."),
    // 알림 설정 category 문자열이 NotificationCategory로 변환되지 않는 경우
    INVALID_NOTIFICATION_CATEGORY(HttpStatus.BAD_REQUEST, "NTF_400_04", "알림 카테고리가 올바르지 않습니다."),
    // 본인 알림이 아닌 데이터에 접근한 경우
    FORBIDDEN(HttpStatus.FORBIDDEN, "NTF_403_01", "알림 접근 권한이 없습니다."),
    // 요청한 notificationId가 없는 경우
    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "NTF_404_01", "알림을 찾을 수 없습니다."),
    // 해제/갱신하려는 FCM 토큰이 없는 경우
    FCM_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "NTF_404_02", "FCM 토큰을 찾을 수 없습니다."),
    // 설정 조회 시 단지 ID 보완에 필요한 user_cache가 없는 경우
    USER_CACHE_NOT_FOUND(HttpStatus.NOT_FOUND, "NTF_404_03", "사용자 캐시를 찾을 수 없습니다."),
    // fcm-enabled=false guard로 실제 Firebase 호출을 막은 경우
    PUSH_SEND_DISABLED(HttpStatus.OK, "NTF_200_01", "FCM 발송이 비활성화되어 있습니다."),
    // HTTPS 활성화 후 Firebase 발송 실패를 표현할 때 사용한다
    PUSH_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "NTF_500_01", "FCM 푸시 알림 발송에 실패했습니다.");

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
