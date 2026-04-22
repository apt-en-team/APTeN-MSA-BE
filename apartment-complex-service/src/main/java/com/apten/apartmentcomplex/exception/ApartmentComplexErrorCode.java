package com.apten.apartmentcomplex.exception;

import com.apten.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

// 단지 서비스 에러코드

@Getter
@RequiredArgsConstructor
public enum ApartmentComplexErrorCode implements ErrorCode {

    APARTMENT_COMPLEX_NOT_FOUND(HttpStatus.NOT_FOUND, "CPLX_404_01", "단지를 찾을 수 없습니다."),
    BUILDING_NOT_FOUND(HttpStatus.NOT_FOUND, "CPLX_404_02", "동 정보를 찾을 수 없습니다."),
    UNIT_NOT_FOUND(HttpStatus.NOT_FOUND, "CPLX_404_03", "호 정보를 찾을 수 없습니다."),
    DUPLICATE_APARTMENT_COMPLEX(HttpStatus.CONFLICT, "CPLX_409_01", "이미 존재하는 단지입니다.");

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
