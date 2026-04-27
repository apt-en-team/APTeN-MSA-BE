package com.apten.household.application.service;

import com.apten.common.exception.BusinessException;
import com.apten.common.exception.CommonErrorCode;
import com.apten.household.application.model.request.BillPolicyPutReq;
import com.apten.household.application.model.response.BillPolicyPutRes;
import com.apten.household.domain.entity.BillPolicy;
import com.apten.household.domain.entity.ComplexCache;
import com.apten.household.domain.enums.ComplexCacheStatus;
import com.apten.household.domain.repository.BillPolicyRepository;
import com.apten.household.domain.repository.ComplexCacheRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 세대 월 청구 기본 정책 원본 저장을 담당하는 응용 서비스이다.
// 청구 생성 전에 필요한 기본 관리비 정책을 단지 기준으로 upsert 한다.
@Service
@Transactional
@RequiredArgsConstructor
public class BillPolicyService {

    // 청구 기본 정책 원본 저장소이다.
    private final BillPolicyRepository billPolicyRepository;

    // 단지 존재 여부와 활성 상태를 확인하는 캐시 저장소이다.
    private final ComplexCacheRepository complexCacheRepository;

    // 단지별 청구 기본 정책을 저장하거나 갱신한다.
    public BillPolicyPutRes updateBillPolicy(Long complexId, BillPolicyPutReq req) {
        // 단지 ID와 요청 본문이 모두 있어야 정책 저장을 진행할 수 있다.
        if (complexId == null || req == null) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        // 기본 관리비는 필수값이며 음수가 되면 안 된다.
        validatePositiveOrZero(req.getBaseFee());

        // 납부기한일은 1일부터 31일까지만 허용한다.
        validatePaymentDueDay(req.getPaymentDueDay());

        // 연체료율은 필수값이며 음수가 되면 안 된다.
        validatePositiveOrZero(req.getLateFeeRate());

        // 청구 정책은 활성 단지에만 설정할 수 있도록 먼저 단지 상태를 확인한다.
        ComplexCache complexCache = complexCacheRepository.findById(complexId)
                .filter(cache -> cache.getStatus() == ComplexCacheStatus.ACTIVE)
                .orElseThrow(() -> new BusinessException(CommonErrorCode.INVALID_PARAMETER));

        // 기존 청구 정책이 있으면 갱신하고 없으면 새 정책을 만든다.
        BillPolicy billPolicy = billPolicyRepository.findByComplexId(complexId)
                .orElseGet(() -> BillPolicy.builder()
                        .complexId(complexCache.getId())
                        .build());

        // 요청값을 현재 정책 엔티티에 반영한다.
        billPolicy.apply(req);

        // 정책 원본을 저장한다.
        BillPolicy savedPolicy = billPolicyRepository.save(billPolicy);

        // 저장 결과를 응답 DTO로 변환한다.
        return BillPolicyPutRes.builder()
                .complexId(savedPolicy.getComplexId())
                .baseFee(savedPolicy.getBaseFee())
                .paymentDueDay(savedPolicy.getPaymentDueDay())
                .lateFeeRate(savedPolicy.getLateFeeRate())
                .lateFeeUnit(savedPolicy.getLateFeeUnit())
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

    // 납부기한일이 월별 달력 범위를 벗어나지 않도록 검증한다.
    private void validatePaymentDueDay(Integer paymentDueDay) {
        if (paymentDueDay == null || paymentDueDay < 1 || paymentDueDay > 31) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }
    }
}
