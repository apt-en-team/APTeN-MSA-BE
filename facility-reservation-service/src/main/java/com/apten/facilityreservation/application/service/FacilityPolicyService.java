package com.apten.facilityreservation.application.service;

import com.apten.common.exception.BusinessException;
import com.apten.common.exception.CommonErrorCode;
import com.apten.facilityreservation.application.model.request.FacilityPolicyPutReq;
import com.apten.facilityreservation.application.model.response.FacilityPolicyPutRes;
import com.apten.facilityreservation.domain.entity.ComplexCache;
import com.apten.facilityreservation.domain.entity.FacilityPolicy;
import com.apten.facilityreservation.domain.enums.ComplexCacheStatus;
import com.apten.facilityreservation.domain.repository.ComplexCacheRepository;
import com.apten.facilityreservation.domain.repository.FacilityPolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 시설 타입 기본 정책 원본 저장을 담당하는 응용 서비스이다.
// 단지와 시설 타입 기준으로 정책을 upsert 해서 시설 예약 계산의 기준값을 만든다.
@Service
@Transactional
@RequiredArgsConstructor
public class FacilityPolicyService {

    // 시설 정책 원본 저장소이다.
    private final FacilityPolicyRepository facilityPolicyRepository;

    // 단지 존재 여부와 활성 상태를 확인하는 캐시 저장소이다.
    private final ComplexCacheRepository complexCacheRepository;

    // 시설 정책 설정 서비스이다.
    public FacilityPolicyPutRes updateFacilityPolicy(Long complexId, FacilityPolicyPutReq req) {
        // 요청 객체와 시설 타입 코드를 검증한다.
        validateFacilityPolicyReq(req);

        // 단지 캐시에서 단지 존재 여부와 활성 상태를 확인한다.
        validateComplex(complexId);

        // 기존 정책이 있으면 수정하고 없으면 새로 생성한다.
        FacilityPolicy policy = facilityPolicyRepository
                .findByComplexIdAndFacilityTypeCode(complexId, req.getFacilityTypeCode())
                .orElseGet(() -> FacilityPolicy.builder()
                        .complexId(complexId)
                        .facilityTypeCode(req.getFacilityTypeCode())
                        .build());

        // 요청값을 정책에 반영한다.
        policy.apply(req);

        // 신규 정책은 INSERT, 기존 정책은 UPDATE 처리된다.
        FacilityPolicy savedPolicy = facilityPolicyRepository.save(policy);

        // 저장 결과를 응답한다.
        return FacilityPolicyPutRes.builder()
                .complexId(complexId)
                .facilityTypeCode(savedPolicy.getFacilityTypeCode())
                .baseFee(savedPolicy.getBaseFee())
                .slotMin(savedPolicy.getSlotMin())
                .cancelDeadlineHours(savedPolicy.getCancelDeadlineHours())
                .gxWaitingEnabled(savedPolicy.getGxWaitingEnabled())
                .isActive(savedPolicy.getIsActive())
                .updatedAt(savedPolicy.getUpdatedAt())
                .build();
    }

    // 시설 정책 요청값을 검증한다.
    private void validateFacilityPolicyReq(FacilityPolicyPutReq req) {
        // 요청 객체와 시설 타입 코드는 정책 저장의 최소 조건이다.
        if (req == null || req.getFacilityTypeCode() == null || req.getFacilityTypeCode().isBlank()) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        // 기본 요금은 값이 있을 때만 음수 여부를 확인한다.
        validatePositiveOrZeroIfPresent(req.getBaseFee());

        // 예약 단위는 값이 있을 때만 음수 여부를 확인한다.
        validatePositiveOrZeroIfPresent(req.getSlotMin());

        // 취소 마감 시간은 값이 있을 때만 음수 여부를 확인한다.
        validatePositiveOrZeroIfPresent(req.getCancelDeadlineHours());
    }

    // 값이 있을 때 음수 여부를 검증한다.
    private void validatePositiveOrZeroIfPresent(Number value) {
        if (value != null && value.doubleValue() < 0) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }
    }

    // 단지 캐시에서 단지 존재 여부와 활성 상태를 검증한다.
    private void validateComplex(Long complexId) {
        // 단지 식별자가 없으면 정책을 어느 단지에 적용할지 알 수 없다.
        if (complexId == null) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        // 단지 캐시에서 단지를 찾고 활성 상태인지 함께 확인한다.
        ComplexCache complex = complexCacheRepository.findById(complexId)
                .orElseThrow(() -> new BusinessException(CommonErrorCode.INVALID_PARAMETER));

        // 비활성 단지에는 시설 정책을 저장하지 않는다.
        if (complex.getStatus() != ComplexCacheStatus.ACTIVE) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }
    }
}
