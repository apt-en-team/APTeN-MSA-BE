package com.apten.board.application.service;

import com.apten.board.domain.repository.ComplexFeatureCacheRepository;
import com.apten.board.exception.BoardErrorCode;
import com.apten.common.enums.FeatureCode;
import com.apten.common.exception.BusinessException;
import com.apten.common.exception.CommonErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 단지별 기능 사용 가능 여부를 조회하고 차단하는 서비스이다.
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FeatureAccessService {

    private final ComplexFeatureCacheRepository complexFeatureCacheRepository;

    // 캐시가 없으면 기존 단지 기능을 막지 않기 위해 사용 가능으로 본다.
    public boolean isEnabled(Long complexId, FeatureCode featureCode) {
        return complexFeatureCacheRepository.findByComplexIdAndFeatureCode(complexId, featureCode)
                .map(feature -> Boolean.TRUE.equals(feature.getEnabled()))
                .orElse(true);
    }

    // 기능이 꺼진 단지는 전자투표 API 접근을 차단한다.
    public void validateEnabled(Long complexId, FeatureCode featureCode) {
        if (complexId == null || featureCode == null) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        if (!isEnabled(complexId, featureCode)) {
            throw new BusinessException(BoardErrorCode.FEATURE_DISABLED);
        }
    }
}
