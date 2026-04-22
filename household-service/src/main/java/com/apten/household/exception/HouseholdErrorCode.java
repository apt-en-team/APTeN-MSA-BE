package com.apten.household.exception;

import com.apten.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

// 세대 서비스 에러코드
@Getter
@RequiredArgsConstructor
public enum HouseholdErrorCode implements ErrorCode {

    HOUSEHOLD_NOT_FOUND(HttpStatus.NOT_FOUND, "HHD_404_01", "세대를 찾을 수 없습니다."),
    DUPLICATE_HOUSEHOLD(HttpStatus.CONFLICT, "HHD_409_01", "이미 존재하는 세대입니다."),
    HOUSEHOLD_HAS_MEMBER(HttpStatus.BAD_REQUEST, "HHD_400_01", "소속 회원이 있어 삭제할 수 없습니다."),
    HOUSEHOLD_ALREADY_LINKED(HttpStatus.BAD_REQUEST, "HHD_400_02", "이미 세대가 연동되어 있습니다."),
    HOUSEHOLD_MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "HHD_404_02", "세대원을 찾을 수 없습니다."),
    HOUSEHOLD_MATCH_FAILED(HttpStatus.NOT_FOUND, "HHD_404_03", "일치하는 세대가 없습니다."),
    BILL_NOT_CONFIRMED(HttpStatus.FORBIDDEN, "HHD_403_01", "확정되지 않은 청구 내역입니다."),
    BILL_ALREADY_CONFIRMED(HttpStatus.BAD_REQUEST, "HHD_400_03", "이미 확정된 청구 내역입니다."),
    INVALID_BILL_AMOUNT(HttpStatus.BAD_REQUEST, "HHD_400_04", "유효하지 않은 금액입니다.");

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
