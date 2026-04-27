package com.apten.facilityreservation.application.service;

import com.apten.facilityreservation.application.model.request.FacilityPolicyListReq;
import com.apten.facilityreservation.application.model.request.FacilityPolicyPutReq;
import com.apten.facilityreservation.application.model.response.FacilityPolicyListRes;
import com.apten.facilityreservation.application.model.response.FacilityPolicyPutRes;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

// 시설 정책 원본 관리 API 시그니처를 담당하는 서비스이다.
@Service
public class FacilityPolicyService {

    // 시설 예약 정책을 저장한다.
    public FacilityPolicyPutRes updateFacilityPolicy(FacilityPolicyPutReq req) {
        //TODO facility_type_code 중복 정책 조회
        //TODO 기본요금, 예약단위, 취소마감시간 유효성 검증
        //TODO facility_policy upsert
        return FacilityPolicyPutRes.builder()
                .facilityPolicyId(0L)
                .complexId(req.getComplexId())
                .facilityTypeCode(req.getFacilityTypeCode())
                .baseFee(req.getBaseFee())
                .slotMin(req.getSlotMin())
                .cancelDeadlineHours(req.getCancelDeadlineHours())
                .gxWaitingEnabled(req.getGxWaitingEnabled())
                .isActive(req.getIsActive())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // 시설 예약 정책 목록을 조회한다.
    public List<FacilityPolicyListRes> getFacilityPolicyList(FacilityPolicyListReq req) {
        //TODO complexId와 facilityTypeCode 기준 정책 목록 조회
        return List.of();
    }
}
