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
        // TODO:
        // 1) usageYear/usageMonth 기준 COMPLETED 예약을 조회한다.
        // 2) reservation.householdId 기준으로 세대별 비용을 집계한다.
        // 3) facility override baseFee와 facility_policy.baseFee 우선순위를 적용한다.
        // 4) facility_usage_monthly를 upsert하고 isPublished=false로 저장한다.
        // 5) 특정 단지 대상 실행 여부는 추후 내부 API 계약 확정 후 결정한다.
        return FacilityFeeCalculateRes.builder()
                .usageYear(req.getUsageYear())
                .usageMonth(req.getUsageMonth())
                .processedCount(0)
                .calculatedAt(LocalDateTime.now())
                .build();
    }

    // 시설 이용 비용을 Household Service로 발행한다.
    public FacilityFeePublishRes publishFacilityFees(FacilityFeePublishReq req) {
        // TODO:
        // 1) usageYear/usageMonth 기준 미발행 facility_usage_monthly를 조회한다.
        // 2) Household Service로 비용 발행 outbox 적재는 2단계에서 구현한다.
        // 3) 발행 성공 후 isPublished=true, publishedAt 저장을 수행한다.
        // 4) 특정 단지 대상 발행 여부는 추후 내부 API 계약 확정 후 결정한다.
        return FacilityFeePublishRes.builder()
                .usageYear(req.getUsageYear())
                .usageMonth(req.getUsageMonth())
                .publishedCount(0)
                .published(false)
                .publishedAt(LocalDateTime.now())
                .build();
    }
}
