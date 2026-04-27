package com.apten.parkingvehicle.exception;

import com.apten.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

// 차량 서비스 에러코드
@Getter
@RequiredArgsConstructor
public enum ParkingVehicleErrorCode implements ErrorCode {

    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "PVH_400_00", "잘못된 요청입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "PVH_401_00", "인증이 필요합니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "PVH_401_01", "유효하지 않은 토큰입니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "PVH_401_02", "토큰이 만료되었습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "PVH_404_00", "사용자를 찾을 수 없습니다."),
    HOUSEHOLD_NOT_FOUND(HttpStatus.NOT_FOUND, "PVH_404_10", "세대를 찾을 수 없습니다."),
    DUPLICATE_LICENSE_PLATE(HttpStatus.CONFLICT, "PVH_409_01", "이미 등록된 차량번호입니다."),
    VEHICLE_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "PVH_400_01", "세대당 차량 등록 한도를 초과했습니다."),
    VEHICLE_NOT_FOUND(HttpStatus.NOT_FOUND, "PVH_404_01", "차량을 찾을 수 없습니다."),
    VEHICLE_OWNER_MISMATCH(HttpStatus.FORBIDDEN, "PVH_403_01", "차량 소유자가 아닙니다."),
    VEHICLE_STATUS_INVALID(HttpStatus.BAD_REQUEST, "PVH_400_02", "차량 상태가 올바르지 않습니다."),
    VEHICLE_POLICY_NOT_FOUND(HttpStatus.NOT_FOUND, "PVH_404_02", "차량 정책을 찾을 수 없습니다."),
    VISITOR_POLICY_NOT_FOUND(HttpStatus.NOT_FOUND, "PVH_404_03", "방문차량 정책을 찾을 수 없습니다."),
    VISIT_DATE_INVALID(HttpStatus.BAD_REQUEST, "PVH_400_03", "방문 예정일이 올바르지 않습니다."),
    VISITOR_VEHICLE_NOT_FOUND(HttpStatus.NOT_FOUND, "PVH_404_04", "방문차량을 찾을 수 없습니다."),
    VISITOR_VEHICLE_OWNER_MISMATCH(HttpStatus.FORBIDDEN, "PVH_403_02", "방문차량 소유자가 아닙니다."),
    VISITOR_VEHICLE_STATUS_INVALID(HttpStatus.BAD_REQUEST, "PVH_400_04", "방문차량 상태가 올바르지 않습니다."),
    REGULAR_VISITOR_NOT_FOUND(HttpStatus.NOT_FOUND, "PVH_404_05", "고정 방문차량을 찾을 수 없습니다."),
    REGULAR_VISITOR_OWNER_MISMATCH(HttpStatus.FORBIDDEN, "PVH_403_03", "고정 방문차량 소유자가 아닙니다."),
    PARKING_FLOOR_NOT_FOUND(HttpStatus.NOT_FOUND, "PVH_404_06", "주차층 정보를 찾을 수 없습니다."),
    DUPLICATE_PARKING_FLOOR(HttpStatus.CONFLICT, "PVH_409_02", "이미 등록된 주차층입니다."),
    DUPLICATE_IN_ENTRY(HttpStatus.CONFLICT, "PVH_409_03", "이미 입차 처리된 차량입니다."),
    EVENT_PUBLISH_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "PVH_500_01", "이벤트 발행에 실패했습니다."),
    INTERNAL_SERVICE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "PVH_500_02", "내부 서비스 처리 중 오류가 발생했습니다.");

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
