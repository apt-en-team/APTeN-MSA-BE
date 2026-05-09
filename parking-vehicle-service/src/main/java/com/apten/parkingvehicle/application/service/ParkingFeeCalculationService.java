package com.apten.parkingvehicle.application.service;

import com.apten.parkingvehicle.application.model.request.VehicleFeeCalculateReq;
import com.apten.parkingvehicle.application.model.request.VisitorFeeCalculateReq;
import com.apten.parkingvehicle.application.model.response.VehicleFeeCalculateRes;
import com.apten.parkingvehicle.application.model.response.VisitorFeeCalculateRes;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

// 차량 비용과 방문차량 비용 월 산정을 담당하는 응용 서비스이다.
@Service
public class ParkingFeeCalculationService {

    // 차량 비용을 월 기준으로 산정한다.
    public VehicleFeeCalculateRes calculateVehicleFees(VehicleFeeCalculateReq request) {
        //TODO complexId별 활성 차량 정책 조회
        //TODO 세대별 APPROVED 차량 수 집계
        //TODO carCount 기준 monthlyFee 계산
        //TODO vehicle_fee_monthly upsert
        //TODO Household Service로 차량 비용 이벤트 발행 outbox 적재
        //TODO isPublished/publishedAt 처리
        return VehicleFeeCalculateRes.builder()
                .complexId(request.getComplexId())
                .billYear(request.getBillYear())
                .billMonth(request.getBillMonth())
                .items(List.of())
                .published(false)
                .executedAt(LocalDateTime.now())
                .build();
    }

    // 방문차량 비용을 월 기준으로 산정한다.
    public VisitorFeeCalculateRes calculateVisitorFees(VisitorFeeCalculateReq request) {
        //TODO visitor_policy 조회
        //TODO parking_log 기준 세대별 월 방문차량 이용시간 집계
        //TODO freeMinutes, extraMinutes, visitorFee 계산
        //TODO visitor_usage_monthly upsert
        //TODO Household Service로 방문차량 비용 이벤트 발행 outbox 적재
        //TODO isPublished/publishedAt 처리
        return VisitorFeeCalculateRes.builder()
                .complexId(request.getComplexId())
                .billYear(request.getBillYear())
                .billMonth(request.getBillMonth())
                .items(List.of())
                .published(false)
                .executedAt(LocalDateTime.now())
                .build();
    }
}
