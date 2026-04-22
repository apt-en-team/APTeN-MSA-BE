package com.apten.facilityreservation.exception;

import com.apten.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

// 시설 예약 서비스 에러코드
@Getter
@RequiredArgsConstructor
public enum FacilityReservationErrorCode implements ErrorCode {

    FACILITY_NOT_FOUND(HttpStatus.NOT_FOUND, "RSV_404_01", "시설을 찾을 수 없습니다."),
    FACILITY_INACTIVE(HttpStatus.BAD_REQUEST, "RSV_400_01", "현재 이용 불가능한 시설입니다."),
    SEAT_ALREADY_RESERVED(HttpStatus.CONFLICT, "RSV_409_01", "이미 예약된 좌석입니다."),
    TIME_ALREADY_RESERVED(HttpStatus.CONFLICT, "RSV_409_02", "이미 예약된 시간입니다."),
    GX_ALREADY_APPLIED(HttpStatus.CONFLICT, "RSV_409_03", "이미 신청한 프로그램입니다."),
    INVALID_DATE(HttpStatus.BAD_REQUEST, "RSV_400_02", "예약할 수 없는 날짜입니다."),
    RESERVATION_FULL(HttpStatus.CONFLICT, "RSV_409_04", "정원이 초과되었습니다."),
    RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, "RSV_404_02", "예약을 찾을 수 없습니다."),
    RESERVATION_WRITER_MISMATCH(HttpStatus.FORBIDDEN, "RSV_403_01", "본인의 예약만 취소할 수 있습니다."),
    CANCEL_TIME_EXPIRED(HttpStatus.BAD_REQUEST, "RSV_400_03", "예약 취소 가능 시간이 지났습니다."),
    GX_CAPACITY_FULL(HttpStatus.CONFLICT, "RSV_409_05", "GX 정원이 가득 찼습니다.");


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
