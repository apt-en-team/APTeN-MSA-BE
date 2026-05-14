package com.apten.facilityreservation.application.service;

import com.apten.common.enums.FeatureCode;
import com.apten.common.exception.BusinessException;
import com.apten.common.exception.CommonErrorCode;
import com.apten.facilityreservation.application.model.request.FacilityPolicyListReq;
import com.apten.facilityreservation.application.model.request.FacilityPolicyPutReq;
import com.apten.facilityreservation.application.model.response.FacilityPolicyListRes;
import com.apten.facilityreservation.application.model.response.FacilityPolicyPutRes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.apten.facilityreservation.domain.entity.FacilityPolicy;
import com.apten.facilityreservation.domain.enums.ComplexCacheStatus;
import com.apten.facilityreservation.domain.enums.FacilityTypeCode;
import com.apten.facilityreservation.domain.repository.ComplexCacheRepository;
import com.apten.facilityreservation.domain.repository.FacilityPolicyRepository;
import com.apten.facilityreservation.domain.repository.FacilityTypeRepository;
import com.apten.facilityreservation.exception.FacilityReservationErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 시설 정책 원본 관리 API 시그니처를 담당하는 서비스이다.
@Service
@RequiredArgsConstructor
public class FacilityPolicyService {

    private final FeatureAccessService featureAccessService;
    private final FacilityTypeRepository facilityTypeRepository;
    private final FacilityPolicyRepository facilityPolicyRepository;
    private final ComplexCacheRepository complexCacheRepository;

    // 시설 관리자 접근 검증
    private void validateAdminAccess(Long complexId) {
        featureAccessService.validateEnabled(complexId, FeatureCode.FACILITY);

        complexCacheRepository.findByIdAndStatus(complexId, ComplexCacheStatus.ACTIVE)
                .orElseThrow(() -> new BusinessException(FacilityReservationErrorCode.COMPLEX_NOT_FOUND));
    }

    // 시설 정책 요청 검증
    private void validatePolicyReq(FacilityPolicyPutReq req) {
        if (req == null || req.getFacilityTypeCode() == null) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        if (req.getBaseFee() == null || req.getBaseFee().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(FacilityReservationErrorCode.INVALID_FACILITY_POLICY);
        }

        if (req.getSlotMin() == null || req.getSlotMin() <= 0) {
            throw new BusinessException(FacilityReservationErrorCode.INVALID_FACILITY_POLICY);
        }

        if (req.getCancelDeadlineHours() == null || req.getCancelDeadlineHours() < 0) {
            throw new BusinessException(FacilityReservationErrorCode.INVALID_FACILITY_POLICY);
        }
    }

    // 시설 예약 정책 저장
    @Transactional
    public FacilityPolicyPutRes updateFacilityPolicy(Long complexId, FacilityPolicyPutReq req) {
        // 시설 접근 검증
        validateAdminAccess(complexId);

        // 정책 요청 검증
        validatePolicyReq(req);

        // 시설 타입 코드 검증
        facilityTypeRepository.findByTypeCode(req.getFacilityTypeCode())
                .orElseThrow(() -> new BusinessException(FacilityReservationErrorCode.FACILITY_TYPE_NOT_FOUND));

        // 기존 정책 조회
        Optional<FacilityPolicy> policyOptional =
                facilityPolicyRepository.findByComplexIdAndFacilityTypeCode(
                        complexId,
                        req.getFacilityTypeCode()
                );

        FacilityPolicy policy;

        if (policyOptional.isPresent()) {
            // 기존 정책 수정
            policy = policyOptional.get();
            policy.apply(req);
        } else {
            // 신규 정책 저장
            policy = facilityPolicyRepository.save(FacilityPolicy.builder()
                    .complexId(complexId)
                    .facilityTypeCode(req.getFacilityTypeCode())
                    .baseFee(req.getBaseFee())
                    .slotMin(req.getSlotMin())
                    .cancelDeadlineHours(req.getCancelDeadlineHours())
                    .gxWaitingEnabled(Boolean.TRUE.equals(req.getGxWaitingEnabled()))
                    .isActive(req.getIsActive() == null || req.getIsActive())
                    .build());
        }

        // 정책 저장 응답
        return FacilityPolicyPutRes.builder()
                .facilityPolicyId(policy.getId())
                .facilityTypeCode(policy.getFacilityTypeCode())
                .baseFee(policy.getBaseFee())
                .slotMin(policy.getSlotMin())
                .cancelDeadlineHours(policy.getCancelDeadlineHours())
                .gxWaitingEnabled(policy.getGxWaitingEnabled())
                .isActive(policy.getIsActive())
                .updatedAt(policy.getUpdatedAt())
                .build();
    }

    // 시설 예약 정책 목록 조회
    public List<FacilityPolicyListRes> getFacilityPolicyList(Long complexId, FacilityPolicyListReq req) {
        // 시설 접근 검증
        validateAdminAccess(complexId);

        // 시설 타입 조건 정리
        FacilityTypeCode facilityTypeCode = req == null ? null : req.getFacilityTypeCode();

        // 시설 정책 목록 조회 및 응답 변환
        return facilityPolicyRepository.findPolicies(complexId, facilityTypeCode)
                .stream()
                .map(policy -> FacilityPolicyListRes.builder()
                        .facilityPolicyId(policy.getId())
                        .facilityTypeCode(policy.getFacilityTypeCode())
                        .baseFee(policy.getBaseFee())
                        .slotMin(policy.getSlotMin())
                        .cancelDeadlineHours(policy.getCancelDeadlineHours())
                        .gxWaitingEnabled(policy.getGxWaitingEnabled())
                        .isActive(policy.getIsActive())
                        .updatedAt(policy.getUpdatedAt())
                        .build())
                .toList();
    }
}
