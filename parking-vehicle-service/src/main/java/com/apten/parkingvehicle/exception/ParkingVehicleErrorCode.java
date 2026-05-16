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
    FEATURE_DISABLED(HttpStatus.FORBIDDEN, "PVH_403_04", "현재 단지에서 주차 현황 기능을 사용할 수 없습니다."),
    PARKING_ZONE_NOT_FOUND(HttpStatus.NOT_FOUND, "PVH_404_06", "주차 구역을 찾을 수 없습니다."),
    DUPLICATE_PARKING_ZONE(HttpStatus.CONFLICT, "PVH_409_02", "이미 등록된 주차 구역입니다."),
    DUPLICATE_IN_ENTRY(HttpStatus.CONFLICT, "PVH_409_03", "이미 입차 처리된 차량입니다."),
    // 이미 출차 처리된 차량에 다시 출차 등록을 시도한 경우
    DUPLICATE_OUT_ENTRY(HttpStatus.CONFLICT, "PVH_409_04", "이미 출차 처리된 차량입니다."),
    // 입차 기록 없이 출차 등록을 시도한 경우
    NO_IN_ENTRY_FOR_OUT(HttpStatus.CONFLICT, "PVH_409_05", "입차 기록이 없어 출차 처리할 수 없습니다."),
    // 비활성 상태인 주차 구역에 입출차 등록을 시도한 경우
    PARKING_ZONE_INACTIVE(HttpStatus.BAD_REQUEST, "PVH_400_05", "비활성 상태의 주차 구역입니다."),
    PARKING_TYPE_NONE(HttpStatus.BAD_REQUEST, "PVH_400_06", "주차 운영 타입이 미사용 상태입니다."),
    // 면수는 1 이상이어야 한다
    INVALID_TOTAL_SLOTS(HttpStatus.BAD_REQUEST, "PVH_400_07", "주차 면수는 1 이상이어야 합니다."),
    // PATCH로 활성→비활성 전환을 시도한 경우 (DELETE API 사용 유도)
    USE_DELETE_API_FOR_DEACTIVATION(HttpStatus.BAD_REQUEST, "PVH_400_08", "주차 구역 비활성화는 삭제 API로 처리해야 합니다."),
    // 단지의 마지막 활성 zone을 비활성화 시도한 경우
    LAST_ACTIVE_ZONE_CANNOT_BE_DEACTIVATED(HttpStatus.CONFLICT, "PVH_409_06", "마지막 활성 주차 구역은 비활성화할 수 없습니다."),
    // 현재 입차 중인 차량이 있는 zone을 비활성화 시도한 경우
    ZONE_HAS_PARKED_VEHICLES(HttpStatus.CONFLICT, "PVH_409_07", "현재 입차 중인 차량이 있어 비활성화할 수 없습니다."),
    // 변경 요청 면수가 현재 입차 차량 수보다 적은 경우
    TOTAL_SLOTS_LESS_THAN_PARKED(HttpStatus.CONFLICT, "PVH_409_08", "현재 입차 중인 차량 수보다 적은 면수로 변경할 수 없습니다."),
    EVENT_PUBLISH_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "PVH_500_01", "이벤트 발행에 실패했습니다."),
    PARKING_SETTING_NOT_FOUND(HttpStatus.NOT_FOUND, "PVH_404_07", "주차 설정을 찾을 수 없습니다."),
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
