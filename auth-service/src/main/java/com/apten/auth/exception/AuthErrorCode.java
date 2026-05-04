package com.apten.auth.exception;

import com.apten.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

// 인증 서비스 에러코드
@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {

    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "AUTH_401_01", "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "AUTH_403_01", "권한이 없습니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "AUTH_401_02", "토큰이 만료되었습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_401_03", "유효하지 않은 토큰입니다."),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "AUTH_401_04", "리프레시 토큰이 만료되었습니다."),
    REFRESH_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "AUTH_401_05", "유효하지 않은 리프레시 토큰입니다."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "AUTH_401_06", "이메일 또는 비밀번호가 일치하지 않습니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "AUTH_409_01", "이미 사용중인 이메일입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "AUTH_404_01", "사용자를 찾을 수 없습니다."),
    MAIL_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "AUTH_500_01", "이메일 발송에 실패했습니다."),
    SMS_CODE_EXPIRED(HttpStatus.BAD_REQUEST, "AUTH_400_01", "인증코드가 만료되었습니다."),
    SMS_CODE_INVALID(HttpStatus.BAD_REQUEST, "AUTH_400_02", "인증코드가 일치하지 않습니다."),
    ACCOUNT_LOCKED(HttpStatus.UNAUTHORIZED, "AUTH_401_07", "로그인 실패 횟수 초과로 계정이 잠금 처리되었습니다."),
    ACCOUNT_NOT_ACTIVE(HttpStatus.UNAUTHORIZED, "AUTH_401_08", "활성화되지 않은 계정입니다."),
    PASSWORD_POLICY_INVALID(HttpStatus.BAD_REQUEST, "AUTH_400_03", "비밀번호는 8자 이상이며 영문, 숫자, 특수문자를 포함해야 합니다."),
    SOCIAL_ACCOUNT_NO_PASSWORD(HttpStatus.BAD_REQUEST, "AUTH_400_04", "소셜 로그인 전용 계정은 비밀번호 변경이 불가합니다."),
    RESET_TOKEN_INVALID(HttpStatus.BAD_REQUEST, "AUTH_400_05", "유효하지 않거나 만료된 재설정 토큰입니다.");

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
