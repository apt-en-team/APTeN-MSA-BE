package com.apten.facilityreservation.application.service;

import com.apten.common.enums.FeatureCode;
import com.apten.common.exception.BusinessException;
import com.apten.common.exception.CommonErrorCode;
import com.apten.facilityreservation.application.model.request.FacilityPolicyListReq;
import com.apten.facilityreservation.application.model.request.FacilityPolicyPutReq;
import com.apten.facilityreservation.application.model.response.FacilityPolicyListRes;
import com.apten.facilityreservation.application.model.response.FacilityPolicyPutRes;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.apten.facilityreservation.domain.entity.Facility;
import com.apten.facilityreservation.domain.entity.FacilityPolicy;
import com.apten.facilityreservation.domain.entity.FacilityType;
import com.apten.facilityreservation.domain.enums.ComplexCacheStatus;
import com.apten.facilityreservation.domain.enums.FacilityFeeType;
import com.apten.facilityreservation.domain.enums.FacilityUsageUnitType;
import com.apten.facilityreservation.domain.enums.FacilityTypeCode;
import com.apten.facilityreservation.domain.enums.ReservationType;
import com.apten.facilityreservation.domain.repository.ComplexCacheRepository;
import com.apten.facilityreservation.domain.repository.FacilityRepository;
import com.apten.facilityreservation.domain.repository.FacilityPolicyRepository;
import com.apten.facilityreservation.domain.repository.FacilityTypeRepository;
import com.apten.facilityreservation.exception.FacilityReservationErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 시설 정책 관리
@Service
@RequiredArgsConstructor
public class FacilityPolicyService {

    private static final int DAY_SLOT_MINUTES = 1440;

    private final FeatureAccessService featureAccessService;
    private final FacilityRepository facilityRepository;
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
    private FacilityUsageUnitType resolveUsageUnitType(FacilityPolicyPutReq req) {
        if (req == null || req.getUsageUnitType() == null) {
            return FacilityUsageUnitType.MINUTE;
        }
        return req.getUsageUnitType();
    }

    // 시설 정책 요청 검증
    private void validatePolicyReq(FacilityPolicyPutReq req, FacilityUsageUnitType usageUnitType) {
        if (req == null || req.getFacilityId() == null) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        if (req.getBaseFee() == null || req.getBaseFee().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(FacilityReservationErrorCode.INVALID_FACILITY_POLICY);
        }

        if (usageUnitType == FacilityUsageUnitType.MINUTE
                && (req.getSlotMin() == null || req.getSlotMin() <= 0)) {
            throw new BusinessException(FacilityReservationErrorCode.INVALID_FACILITY_POLICY);
        }

        if (req.getCancelDeadlineHours() == null || req.getCancelDeadlineHours() < 0) {
            throw new BusinessException(FacilityReservationErrorCode.INVALID_FACILITY_POLICY);
        }

        if (req.getMaxReservationCount() != null && req.getMaxReservationCount() <= 0) {
            throw new BusinessException(FacilityReservationErrorCode.INVALID_FACILITY_POLICY);
        }
    }

    // 정책 대상 시설 검증
    private Facility validatePolicyTargetFacility(Long complexId, FacilityPolicyPutReq req) {
        Facility facility = facilityRepository.findByIdAndComplexIdAndIsDeletedFalse(req.getFacilityId(), complexId)
                .orElseThrow(() -> new BusinessException(FacilityReservationErrorCode.FACILITY_NOT_FOUND));

        FacilityType facilityType = facilityTypeRepository.findById(facility.getTypeId())
                .orElseThrow(() -> new BusinessException(FacilityReservationErrorCode.FACILITY_TYPE_NOT_FOUND));

        if (facilityType.getTypeCode() == FacilityTypeCode.GX) {
            throw new BusinessException(FacilityReservationErrorCode.FACILITY_POLICY_GX_NOT_ALLOWED);
        }

        if ((facility.getReservationType() == ReservationType.COUNT
                || facility.getReservationType() == ReservationType.APPROVAL)
                && (reqMaxCountMissing(req))) {
            throw new BusinessException(FacilityReservationErrorCode.INVALID_FACILITY_POLICY);
        }

        return facility;
    }

    private boolean reqMaxCountMissing(FacilityPolicyPutReq req) {
        return req.getMaxReservationCount() == null || req.getMaxReservationCount() <= 0;
    }

    private int resolveSlotMin(FacilityPolicyPutReq req, FacilityUsageUnitType usageUnitType, FacilityPolicy existingPolicy) {
        if (usageUnitType == FacilityUsageUnitType.DAY) {
            return DAY_SLOT_MINUTES;
        }

        if (req.getSlotMin() != null) {
            return req.getSlotMin();
        }

        if (existingPolicy != null && existingPolicy.getSlotMin() != null && existingPolicy.getSlotMin() > 0) {
            return existingPolicy.getSlotMin();
        }

        throw new BusinessException(FacilityReservationErrorCode.INVALID_FACILITY_POLICY);
    }

    // 시설 예약 정책 저장/수정
    @Transactional
    public FacilityPolicyPutRes updateFacilityPolicy(Long complexId, FacilityPolicyPutReq req) {
        // 시설 접근 검증
        validateAdminAccess(complexId);

        FacilityUsageUnitType usageUnitType = resolveUsageUnitType(req);

        // 정책 요청 검증
        validatePolicyReq(req, usageUnitType);

        // 정책 대상 시설 검증
        validatePolicyTargetFacility(complexId, req);

        // 기존 정책 조회
        Optional<FacilityPolicy> policyOptional =
                facilityPolicyRepository.findByComplexIdAndFacilityId(complexId, req.getFacilityId());

        FacilityPolicy policy;
        FacilityPolicy existingPolicy = policyOptional.orElse(null);
        int resolvedSlotMin = resolveSlotMin(req, usageUnitType, existingPolicy);

        if (policyOptional.isPresent()) {
            // 기존 정책 수정
            policy = policyOptional.get();
            policy.apply(FacilityPolicyPutReq.builder()
                    .facilityId(req.getFacilityId())
                    .baseFee(req.getBaseFee())
                    .usageUnitType(usageUnitType)
                    .slotMin(resolvedSlotMin)
                    .cancelDeadlineHours(req.getCancelDeadlineHours())
                    .maxReservationCount(req.getMaxReservationCount())
                    .feeType(req.getFeeType())
                    .includedPersonCount(req.getIncludedPersonCount())
                    .extraPersonFee(req.getExtraPersonFee())
                    .subscribeCutoffDay(req.getSubscribeCutoffDay())
                    .cancelCutoffDay(req.getCancelCutoffDay())
                    .build());
        } else {
            // 신규 정책 저장
            policy = facilityPolicyRepository.save(FacilityPolicy.builder()
                    .complexId(complexId)
                    .facilityId(req.getFacilityId())
                    .baseFee(req.getBaseFee())
                    .usageUnitType(usageUnitType)
                    .slotMin(resolvedSlotMin)
                    .cancelDeadlineHours(req.getCancelDeadlineHours())
                    .maxReservationCount(req.getMaxReservationCount())
                    .feeType(req.getFeeType() != null ? req.getFeeType() : FacilityFeeType.FLAT)
                    .includedPersonCount(req.getIncludedPersonCount())
                    .extraPersonFee(req.getExtraPersonFee())
                    .subscribeCutoffDay(req.getSubscribeCutoffDay())
                    .cancelCutoffDay(req.getCancelCutoffDay())
                    .isActive(true)
                    .build());
        }

        // 정책 저장 응답
        return FacilityPolicyPutRes.builder()
                .facilityPolicyId(policy.getId())
                .complexId(policy.getComplexId())
                .facilityId(policy.getFacilityId())
                .baseFee(policy.getBaseFee())
                .usageUnitType(policy.getUsageUnitType())
                .slotMin(policy.getSlotMin())
                .cancelDeadlineHours(policy.getCancelDeadlineHours())
                .maxReservationCount(policy.getMaxReservationCount())
                .feeType(policy.getFeeType())
                .includedPersonCount(policy.getIncludedPersonCount())
                .extraPersonFee(policy.getExtraPersonFee())
                .subscribeCutoffDay(policy.getSubscribeCutoffDay())
                .cancelCutoffDay(policy.getCancelCutoffDay())
                .isActive(policy.getIsActive())
                .updatedAt(policy.getUpdatedAt())
                .build();
    }

    // 시설 예약 정책 목록 조회
    public List<FacilityPolicyListRes> getFacilityPolicyList(Long complexId, FacilityPolicyListReq req) {
        // 시설 접근 검증
        validateAdminAccess(complexId);

        // 시설 조회 조건 정리
        Long facilityId = req == null ? null : req.getFacilityId();

        List<FacilityPolicy> policies = facilityPolicyRepository.findPolicies(complexId, facilityId);

        // 시설 정책 응답 변환
        return policies
                .stream()
                .filter(policy -> facilityRepository.findByIdAndComplexIdAndIsDeletedFalse(policy.getFacilityId(), complexId)
                        .flatMap(facility -> facilityTypeRepository.findById(facility.getTypeId()))
                        .map(type -> type.getTypeCode() != FacilityTypeCode.GX)
                        .orElse(false))
                .map(policy -> FacilityPolicyListRes.builder()
                        .facilityPolicyId(policy.getId())
                        .complexId(policy.getComplexId())
                        .facilityId(policy.getFacilityId())
                        .baseFee(policy.getBaseFee())
                        .usageUnitType(policy.getUsageUnitType())
                        .slotMin(policy.getSlotMin())
                        .cancelDeadlineHours(policy.getCancelDeadlineHours())
                        .maxReservationCount(policy.getMaxReservationCount())
                        .feeType(policy.getFeeType())
                        .includedPersonCount(policy.getIncludedPersonCount())
                        .extraPersonFee(policy.getExtraPersonFee())
                        .subscribeCutoffDay(policy.getSubscribeCutoffDay())
                .cancelCutoffDay(policy.getCancelCutoffDay())
                        .isActive(policy.getIsActive())
                        .updatedAt(policy.getUpdatedAt())
                        .build())
                .toList();
    }
}
