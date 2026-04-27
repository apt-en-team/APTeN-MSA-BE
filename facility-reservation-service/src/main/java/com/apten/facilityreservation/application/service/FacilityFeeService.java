package com.apten.facilityreservation.application.service;

import com.apten.facilityreservation.application.model.request.FacilityFeeCalculateReq;
import com.apten.facilityreservation.application.model.request.FacilityFeePublishReq;
import com.apten.facilityreservation.application.model.response.FacilityFeeCalculateRes;
import com.apten.facilityreservation.application.model.response.FacilityFeePublishRes;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

// 시설 이용 비용 산정과 발행 API 시그니처를 담당하는 서비스이다.
@Service
public class FacilityFeeService {

    // 시설 이용 비용을 산정한다.
    public FacilityFeeCalculateRes calculateFacilityFees(FacilityFeeCalculateReq req) {
        //TODO usageYear/usageMonth 기준 COMPLETED 예약 조회
        //TODO 예약별 facility.baseFee 또는 facility_policy.baseFee 적용
        //TODO userId로 householdId 매핑 필요
        //TODO 세대별 facilityFee 합산
        //TODO facility_usage_monthly upsert
        //TODO isPublished=false로 저장
        return FacilityFeeCalculateRes.builder()
                .complexId(req.getComplexId())
                .usageYear(req.getUsageYear())
                .usageMonth(req.getUsageMonth())
                .calculatedCount(0)
                .calculatedAt(LocalDateTime.now())
                .build();
    }

    // 시설 이용 비용을 Household Service로 발행한다.
    public FacilityFeePublishRes publishFacilityFees(FacilityFeePublishReq req) {
        //TODO usageYear/usageMonth 기준 미발행 facility_usage_monthly 조회
        //TODO Household Service로 시설 비용 이벤트 발행 outbox 적재
        //TODO 발행 성공 시 isPublished=true, publishedAt 저장
        return FacilityFeePublishRes.builder()
                .complexId(req.getComplexId())
                .usageYear(req.getUsageYear())
                .usageMonth(req.getUsageMonth())
                .publishedCount(0)
                .publishedAt(LocalDateTime.now())
                .build();
    }
}
