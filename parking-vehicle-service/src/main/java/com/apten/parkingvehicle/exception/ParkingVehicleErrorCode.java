package com.apten.parkingvehicle.exception;

import com.apten.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

// 차량 서비스 에러코드
@Getter
@RequiredArgsConstructor
public enum ParkingVehicleErrorCode implements ErrorCode {

    VEHICLE_NOT_FOUND(HttpStatus.NOT_FOUND, "PVH_404_01", "차량을 찾을 수 없습니다."),
    DUPLICATE_LICENSE_PLATE(HttpStatus.CONFLICT, "PVH_409_01", "이미 등록된 차량번호입니다."),
    VEHICLE_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "PVH_400_01", "세대당 차량 등록 한도를 초과했습니다."),
    PARKING_LOT_NOT_FOUND(HttpStatus.NOT_FOUND, "PVH_404_02", "주차장을 찾을 수 없습니다."),
    PARKING_FLOOR_NOT_FOUND(HttpStatus.NOT_FOUND, "PVH_404_03", "주차층 정보를 찾을 수 없습니다."),
    VISITOR_VEHICLE_NOT_FOUND(HttpStatus.NOT_FOUND, "PVH_404_04", "방문 차량을 찾을 수 없습니다."),
    PAST_VISIT_DATE(HttpStatus.BAD_REQUEST, "PVH_400_02", "과거 날짜는 선택할 수 없습니다.");

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
