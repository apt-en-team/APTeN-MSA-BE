package com.apten.facilityreservation.application.service;

import com.apten.common.enums.FeatureCode;
import com.apten.common.exception.BusinessException;
import com.apten.common.exception.CommonErrorCode;
import com.apten.facilityreservation.domain.repository.ComplexFeatureCacheRepository;
import com.apten.facilityreservation.exception.FacilityReservationErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 단지 기능 접근 제어
@Service
@Transactional(readOnly = true) //읽기 전용
@RequiredArgsConstructor
public class FeatureAccessService {

    // 단지 기능 캐시 조회
    private final ComplexFeatureCacheRepository complexFeatureCacheRepository;

    public boolean isEnabled(Long complexId, FeatureCode featureCode) {
        if (complexId == null || featureCode == null) {
            return false;
        }

        // 누락 캐시 기본 활성
        return complexFeatureCacheRepository.findByComplexIdAndFeatureCode(complexId, featureCode)
                .map(feature -> Boolean.TRUE.equals(feature.getEnabled()))
                .orElse(true);
    }

    // 단지 기능 활성 검증
    public void validateEnabled(Long complexId, FeatureCode featureCode) {
        if (complexId == null || featureCode == null) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        if (!isEnabled(complexId, featureCode)) {
            throw new BusinessException(FacilityReservationErrorCode.FEATURE_DISABLED);
        }
    }
}
