package com.apten.facilityreservation.application.service;

import com.apten.common.enums.FeatureCode;
import com.apten.facilityreservation.application.model.request.FacilityPolicyListReq;
import com.apten.facilityreservation.application.model.request.FacilityPolicyPutReq;
import com.apten.facilityreservation.application.model.response.FacilityPolicyListRes;
import com.apten.facilityreservation.application.model.response.FacilityPolicyPutRes;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

// 시설 정책 원본 관리 API 시그니처를 담당하는 서비스이다.
@Service
@RequiredArgsConstructor
public class FacilityPolicyService {

    private final FeatureAccessService featureAccessService;

    // 시설 예약 정책을 저장한다. API-610
    public FacilityPolicyPutRes updateFacilityPolicy(Long complexId, FacilityPolicyPutReq req) {
        featureAccessService.validateEnabled(complexId, FeatureCode.FACILITY);
        // TODO:
        // 1) FeatureAccessService로 FACILITY 기능 활성 여부를 확인한다.
        // 2) facilityTypeCode와 complexId 기준 기존 정책을 조회한다.
        // 3) baseFee, slotMin, cancelDeadlineHours, gxWaitingEnabled의 유효성을 검증한다.
        // 4) facility_policy를 upsert하고 응답 DTO를 변환한다.
        return FacilityPolicyPutRes.builder()
                .facilityPolicyId(0L)
                .facilityTypeCode(req.getFacilityTypeCode())
                .baseFee(req.getBaseFee())
                .slotMin(req.getSlotMin())
                .cancelDeadlineHours(req.getCancelDeadlineHours())
                .gxWaitingEnabled(req.getGxWaitingEnabled())
                .isActive(req.getIsActive())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // 시설 예약 정책 목록을 조회한다. API-611
    public List<FacilityPolicyListRes> getFacilityPolicyList(Long complexId, FacilityPolicyListReq req) {
        featureAccessService.validateEnabled(complexId, FeatureCode.FACILITY);
        // TODO:
        // 1) FeatureAccessService로 FACILITY 기능 활성 여부를 확인한다.
        // 2) complexId와 facilityTypeCode 기준 정책 목록을 조회한다.
        // 3) 응답 DTO로 변환한다.
        return List.of();
    }
}
