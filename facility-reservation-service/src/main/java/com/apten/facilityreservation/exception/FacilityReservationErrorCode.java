package com.apten.facilityreservation.exception;

import com.apten.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

// 시설 예약 서비스 에러코드 모음이다.
@Getter
@RequiredArgsConstructor
public enum FacilityReservationErrorCode implements ErrorCode {

    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "FRS_400_01", "잘못된 요청입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "FRS_401_01", "인증이 필요합니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "FRS_401_02", "유효하지 않은 토큰입니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "FRS_401_03", "토큰이 만료되었습니다."),
    COMPLEX_NOT_FOUND(HttpStatus.NOT_FOUND, "FRS_404_01", "단지를 찾을 수 없습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "FRS_404_02", "사용자를 찾을 수 없습니다."),
    FACILITY_TYPE_NOT_FOUND(HttpStatus.NOT_FOUND, "FRS_404_03", "시설 타입을 찾을 수 없습니다."),
    DUPLICATE_FACILITY_TYPE(HttpStatus.CONFLICT, "FRS_409_01", "이미 존재하는 시설 타입입니다."),
    FACILITY_NOT_FOUND(HttpStatus.NOT_FOUND, "FRS_404_04", "시설을 찾을 수 없습니다."),
    FACILITY_INACTIVE(HttpStatus.BAD_REQUEST, "FRS_400_02", "비활성 시설입니다."),
    FACILITY_HAS_RESERVATION(HttpStatus.CONFLICT, "FRS_409_02", "예약이 있는 시설은 삭제할 수 없습니다."),
    FACILITY_SEAT_NOT_FOUND(HttpStatus.NOT_FOUND, "FRS_404_05", "시설 좌석을 찾을 수 없습니다."),
    DUPLICATE_SEAT(HttpStatus.CONFLICT, "FRS_409_03", "이미 존재하는 좌석 번호입니다."),
    INVALID_FACILITY_POLICY(HttpStatus.BAD_REQUEST, "FRS_400_03", "시설 정책 값이 올바르지 않습니다."),
    INVALID_RESERVATION_DATE(HttpStatus.BAD_REQUEST, "FRS_400_04", "예약할 수 없는 날짜입니다."),
    TIME_SLOT_NOT_AVAILABLE(HttpStatus.CONFLICT, "FRS_409_04", "예약 가능한 시간이 아닙니다."),
    SEAT_ALREADY_RESERVED(HttpStatus.CONFLICT, "FRS_409_05", "이미 예약된 좌석입니다."),
    SEAT_TEMP_HOLD_EXISTS(HttpStatus.CONFLICT, "FRS_409_06", "이미 임시 선점된 좌석입니다."),
    TEMP_HOLD_NOT_FOUND(HttpStatus.NOT_FOUND, "FRS_404_06", "임시 선점 정보를 찾을 수 없습니다."),
    TEMP_HOLD_EXPIRED(HttpStatus.BAD_REQUEST, "FRS_400_05", "임시 선점 시간이 만료되었습니다."),
    RESERVATION_FULL(HttpStatus.CONFLICT, "FRS_409_07", "예약 정원이 가득 찼습니다."),
    RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, "FRS_404_07", "예약을 찾을 수 없습니다."),
    RESERVATION_OWNER_MISMATCH(HttpStatus.FORBIDDEN, "FRS_403_01", "본인 예약만 처리할 수 있습니다."),
    CANCEL_TIME_EXPIRED(HttpStatus.BAD_REQUEST, "FRS_400_06", "취소 가능 시간이 지났습니다."),
    INVALID_RESERVATION_STATUS(HttpStatus.BAD_REQUEST, "FRS_400_07", "예약 상태가 올바르지 않습니다."),
    GX_PROGRAM_NOT_FOUND(HttpStatus.NOT_FOUND, "FRS_404_08", "GX 프로그램을 찾을 수 없습니다."),
    GX_PROGRAM_CANCELLED(HttpStatus.BAD_REQUEST, "FRS_400_08", "취소된 GX 프로그램입니다."),
    GX_ALREADY_APPLIED(HttpStatus.CONFLICT, "FRS_409_08", "이미 신청한 GX 프로그램입니다."),
    GX_RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, "FRS_404_09", "GX 예약을 찾을 수 없습니다."),
    GX_CAPACITY_FULL(HttpStatus.CONFLICT, "FRS_409_09", "GX 정원이 가득 찼습니다."),
    GX_WAITING_NOT_FOUND(HttpStatus.NOT_FOUND, "FRS_404_10", "GX 대기 정보를 찾을 수 없습니다."),
    EVENT_PUBLISH_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "FRS_500_01", "이벤트 발행에 실패했습니다."),
    INTERNAL_SERVICE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "FRS_500_02", "내부 처리 중 오류가 발생했습니다.");

    // HTTP 상태값이다.
    private final HttpStatus status;

    // 서비스 코드이다.
    private final String code;

    // 기본 메시지이다.
    private final String message;
}
