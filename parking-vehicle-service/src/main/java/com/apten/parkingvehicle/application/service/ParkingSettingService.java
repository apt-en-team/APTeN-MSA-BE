package com.apten.parkingvehicle.application.service;

import com.apten.common.enums.FeatureCode;
import com.apten.common.exception.BusinessException;
import com.apten.common.exception.CommonErrorCode;
import com.apten.parkingvehicle.application.model.request.ParkingSettingPatchReq;
import com.apten.parkingvehicle.application.model.response.ParkingSettingGetRes;
import com.apten.parkingvehicle.application.model.response.ParkingSettingPatchRes;
import com.apten.parkingvehicle.domain.entity.ParkingSetting;
import com.apten.common.enums.ParkingType;
import com.apten.parkingvehicle.domain.repository.ParkingSettingRepository;
import com.apten.parkingvehicle.exception.ParkingVehicleErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 단지별 주차 운영 타입 설정 서비스
@Service
@RequiredArgsConstructor
public class ParkingSettingService {

    private final ParkingSettingRepository parkingSettingRepository;
    private final FeatureAccessService featureAccessService;

    // 단지의 현재 주차 운영 타입을 조회한다.
    @Transactional(readOnly = true)
    public ParkingSettingGetRes getParkingSetting(
            String userRole,
            Long complexId,
            Long selectedComplexId
    ) {
        // 단지 컨텍스트 해석
        Long targetComplexId = resolveAdminContextComplexId(userRole, complexId, selectedComplexId);
        // 주차 기능 활성화 여부 확인
        featureAccessService.validateEnabled(targetComplexId, FeatureCode.PARKING_STATUS);

        // 단지의 주차 운영 타입 조회 (Kafka 연동으로 단지 생성 시 NONE row가 자동 생성됨)
        ParkingSetting setting = parkingSettingRepository.findByComplexId(targetComplexId)
                .orElseThrow(() -> new BusinessException(ParkingVehicleErrorCode.PARKING_SETTING_NOT_FOUND));

        return ParkingSettingGetRes.of(setting.getComplexId(), setting.getParkingType());
    }

    // 단지가 센서 운영 타입인지 검증한다.
    @Transactional(readOnly = true)
    public void validateSensorType(Long complexId) {
        if (complexId == null) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }
        ParkingSetting setting = parkingSettingRepository.findByComplexId(complexId)
                .orElseThrow(() -> new BusinessException(ParkingVehicleErrorCode.PARKING_SETTING_NOT_FOUND));
        if (setting.getParkingType() != ParkingType.SENSOR) {
            throw new BusinessException(ParkingVehicleErrorCode.PARKING_TYPE_NOT_SENSOR);
        }
    }

    // 단지가 주차 운영 활성 타입인지 검증한다 (BASIC 또는 SENSOR 허용, NONE 차단).
    @Transactional(readOnly = true)
    public void validateParkingEnabled(Long complexId) {
        if (complexId == null) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }
        ParkingSetting setting = parkingSettingRepository.findByComplexId(complexId)
                .orElseThrow(() -> new BusinessException(ParkingVehicleErrorCode.PARKING_SETTING_NOT_FOUND));
        if (setting.getParkingType() == ParkingType.NONE) {
            throw new BusinessException(ParkingVehicleErrorCode.PARKING_TYPE_NONE);
        }
    }

    // 단지의 주차 운영 타입을 변경한다.
    @Transactional
    public ParkingSettingPatchRes updateParkingSetting(
            ParkingSettingPatchReq request,
            String userRole,
            Long complexId,
            Long selectedComplexId
    ) {
        Long targetComplexId = resolveAdminContextComplexId(userRole, complexId, selectedComplexId);
        featureAccessService.validateEnabled(targetComplexId, FeatureCode.PARKING_STATUS);

        // 변경할 운영 타입 필수 검증
        if (request.getParkingType() == null) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        // 단지의 주차 설정 조회 (Kafka 연동으로 NONE row가 미리 생성됨)
        ParkingSetting setting = parkingSettingRepository.findByComplexId(targetComplexId)
                .orElseThrow(() -> new BusinessException(ParkingVehicleErrorCode.PARKING_SETTING_NOT_FOUND));

        // 도메인 메서드로 변경 (JPA dirty checking으로 자동 반영)
        setting.changeType(request.getParkingType());

        return ParkingSettingPatchRes.of(setting.getComplexId(), setting.getParkingType(), setting.getUpdatedAt());
    }

    // 관리자 단지 컨텍스트를 역할별 헤더 기준으로 해석한다.
    // MASTER는 선택 단지, 일반 관리자는 토큰 단지 기준으로 처리한다.
    private Long resolveAdminContextComplexId(String userRole, Long complexId, Long selectedComplexId) {
        if (userRole == null || userRole.isBlank()) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        if ("MASTER".equals(userRole)) {
            if (selectedComplexId == null) {
                throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
            }
            return selectedComplexId;
        }

        if ("MANAGER".equals(userRole) || "ADMIN".equals(userRole)) {
            if (complexId == null) {
                throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
            }
            return complexId;
        }

        throw new BusinessException(CommonErrorCode.FORBIDDEN);
    }
}