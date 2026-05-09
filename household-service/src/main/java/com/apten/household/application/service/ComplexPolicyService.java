package com.apten.household.application.service;

import com.apten.common.exception.BusinessException;
import com.apten.common.exception.CommonErrorCode;
import com.apten.household.application.model.request.ComplexPolicyPutReq;
import com.apten.household.application.model.response.ComplexPolicyPutRes;
import com.apten.household.domain.entity.ComplexCache;
import com.apten.household.domain.entity.ComplexPolicy;
import com.apten.household.domain.enums.ComplexCacheStatus;
import com.apten.household.domain.repository.ComplexCacheRepository;
import com.apten.household.domain.repository.ComplexPolicyRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 단지별 기본 관리비 정책 원본을 저장하는 서비스이다.
@Service
@Transactional
@RequiredArgsConstructor
public class ComplexPolicyService {

    // 단지별 기본 관리비 정책 저장소이다.
    private final ComplexPolicyRepository complexPolicyRepository;

    // 단지 활성 상태 확인용 캐시 저장소이다.
    private final ComplexCacheRepository complexCacheRepository;

    // 기본 관리비 정책을 저장하거나 갱신한다.
    public ComplexPolicyPutRes updateComplexPolicy(Long complexId, ComplexPolicyPutReq req) {
        // 단지 ID와 요청 본문 존재 여부를 검증한다.
        if (complexId == null || req == null) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        // 기본 관리비와 연체료율은 음수가 될 수 없다.
        validatePositiveOrZero(req.getBaseFee());
        validatePositiveOrZero(req.getLateFeeRate());

        // 납부기한일은 1일부터 31일 사이만 허용한다.
        validateDueDay(req.getDueDay());

        // 단지 캐시에서 단지 활성 상태를 먼저 확인한다.
        ComplexCache complexCache = complexCacheRepository.findById(complexId)
                .filter(cache -> cache.getStatus() == ComplexCacheStatus.ACTIVE)
                .orElseThrow(() -> new BusinessException(CommonErrorCode.INVALID_PARAMETER));

        // 기존 정책이 있으면 수정하고 없으면 새 원본 정책을 만든다.
        ComplexPolicy complexPolicy = complexPolicyRepository.findByComplexId(complexId)
                .orElseGet(() -> ComplexPolicy.builder()
                        .complexId(complexCache.getId())
                        .build());

        // 요청값을 원본 정책에 반영한다.
        complexPolicy.apply(req);

        // TODO 정책 변경 이력 또는 감사 로그 저장이 필요하면 여기서 확장한다.
        ComplexPolicy savedPolicy = complexPolicyRepository.save(complexPolicy);

        return ComplexPolicyPutRes.builder()
                .complexId(savedPolicy.getComplexId())
                .baseFee(savedPolicy.getBaseFee())
                .dueDay(savedPolicy.getDueDay())
                .lateFeeRate(savedPolicy.getLateFeeRate())
                .isActive(savedPolicy.getIsActive())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // null 또는 음수 값을 검증한다.
    private void validatePositiveOrZero(Number value) {
        if (value == null || value.doubleValue() < 0) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }
    }

    // 납부기한일 범위를 검증한다.
    private void validateDueDay(Integer dueDay) {
        if (dueDay == null || dueDay < 1 || dueDay > 31) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }
    }
}
