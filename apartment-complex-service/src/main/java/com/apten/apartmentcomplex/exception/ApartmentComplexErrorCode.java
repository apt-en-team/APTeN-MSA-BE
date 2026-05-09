package com.apten.apartmentcomplex.exception;

import com.apten.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

// 단지 서비스 에러코드

@Getter
@RequiredArgsConstructor
public enum ApartmentComplexErrorCode implements ErrorCode {

    COMPLEX_NOT_FOUND(HttpStatus.NOT_FOUND, "CPLX_404_01", "단지를 찾을 수 없습니다."),
    COMPLEX_POLICY_NOT_FOUND(HttpStatus.NOT_FOUND, "CPLX_404_02", "단지 정책을 찾을 수 없습니다."),
    HOUSEHOLD_TYPE_NOT_FOUND(HttpStatus.NOT_FOUND, "CPLX_404_03", "세대 유형을 찾을 수 없습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "CPLX_404_04", "사용자를 찾을 수 없습니다."),
    COMPLEX_ADMIN_NOT_FOUND(HttpStatus.NOT_FOUND, "CPLX_404_05", "단지 관리자 소속 정보를 찾을 수 없습니다."),
    ADMIN_PROFILE_NOT_FOUND(HttpStatus.NOT_FOUND, "CPLX_404_06", "관리자 프로필 정보를 찾을 수 없습니다."),

    DUPLICATE_COMPLEX(HttpStatus.CONFLICT, "CPLX_409_01", "이미 존재하는 단지입니다."),
    DUPLICATE_HOUSEHOLD_TYPE(HttpStatus.CONFLICT, "CPLX_409_02", "이미 존재하는 세대 유형입니다."),
    DUPLICATE_COMPLEX_ADMIN(HttpStatus.CONFLICT, "CPLX_409_03", "이미 등록된 단지 관리자입니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "CPLX_409_04", "이미 사용중인 이메일입니다."),

    INVALID_COMPLEX_STATUS(HttpStatus.BAD_REQUEST, "CPLX_400_01", "유효하지 않은 단지 상태입니다."),
    INVALID_ADMIN_ROLE(HttpStatus.BAD_REQUEST, "CPLX_400_02", "유효하지 않은 관리자 역할입니다."),
    INVALID_POLICY_DATE(HttpStatus.BAD_REQUEST, "CPLX_400_03", "유효하지 않은 정책 적용 기간입니다."),
    USER_NOT_ADMIN(HttpStatus.BAD_REQUEST, "CPLX_400_04", "관리자 권한이 없는 사용자입니다."),
    INACTIVE_COMPLEX(HttpStatus.BAD_REQUEST, "CPLX_400_05", "비활성화된 단지입니다."),
    INVALID_ADMIN_PASSWORD(HttpStatus.BAD_REQUEST, "CPLX_400_06", "비밀번호는 8자 이상이며 영문, 숫자, 특수문자를 포함해야 합니다."),
    EXTERNAL_ADDRESS_API_ERROR(HttpStatus.BAD_GATEWAY, "CPLX_502_01", "주소 검색 외부 API 호출 중 오류가 발생했습니다."),
    AUTH_INTERNAL_API_ERROR(HttpStatus.BAD_GATEWAY, "CPLX_502_02", "인증 서비스 내부 호출 중 오류가 발생했습니다.");

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
