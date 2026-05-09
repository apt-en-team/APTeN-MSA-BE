package com.apten.parkingvehicle.application.service;

import com.apten.parkingvehicle.application.model.request.VehiclePolicyPutReq;
import com.apten.parkingvehicle.application.model.request.VisitorPolicyPutReq;
import com.apten.parkingvehicle.application.model.response.VehiclePolicyListRes;
import com.apten.parkingvehicle.application.model.response.VehiclePolicyPutRes;
import com.apten.parkingvehicle.application.model.response.VisitorPolicyGetRes;
import com.apten.parkingvehicle.application.model.response.VisitorPolicyPutRes;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

// 차량 정책과 방문차량 정책 원본 관리 API를 담당하는 응용 서비스이다.
@Service
public class ParkingPolicyService {

    // 차량 정책은 단지 기준 목록 전체를 교체하는 방식으로 저장한다.
    public VehiclePolicyPutRes updateVehiclePolicy(Long complexId, VehiclePolicyPutReq req) {
        //TODO 관리자 소속 단지 확인
        //TODO 요청 목록 null 여부 검증
        //TODO carCount 중복 여부 검증
        //TODO 월 요금 음수 여부 검증
        //TODO 기존 vehicle_policy 전체 삭제
        //TODO 새 vehicle_policy 목록 저장
        List<VehiclePolicyPutRes.VehiclePolicyItem> responsePolicies = req == null || req.getPolicies() == null
                ? List.of()
                : req.getPolicies().stream()
                .map(policy -> VehiclePolicyPutRes.VehiclePolicyItem.builder()
                        .carCount(policy.getCarCount())
                        .monthlyFee(policy.getMonthlyFee())
                        .build())
                .toList();
        return VehiclePolicyPutRes.builder()
                .complexId(complexId)
                .policies(responsePolicies)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // 차량 정책 목록을 조회한다.
    public VehiclePolicyListRes getVehiclePolicies(Long complexId) {
        //TODO 관리자 소속 단지 확인
        //TODO complexId 기준 vehicle_policy 목록 조회
        return VehiclePolicyListRes.builder()
                .complexId(complexId)
                .policies(List.of())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // 방문차량 정책은 단지당 1건만 유지하므로 upsert 방식으로 저장한다.
    public VisitorPolicyPutRes updateVisitorPolicy(Long complexId, VisitorPolicyPutReq req) {
        //TODO 관리자 소속 단지 확인
        //TODO freeMinutes, hourFee, monthlyLimitHours 음수 여부 검증
        //TODO 기존 visitor_policy 조회
        //TODO 없으면 신규 생성하고 있으면 갱신
        return VisitorPolicyPutRes.builder()
                .complexId(complexId)
                .freeMinutes(req != null ? req.getFreeMinutes() : 0)
                .hourFee(req != null ? req.getHourFee() : null)
                .monthlyLimitHours(req != null ? req.getMonthlyLimitHours() : 300)
                .isActive(req != null ? req.getIsActive() : true)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // 방문차량 정책을 조회한다.
    public VisitorPolicyGetRes getVisitorPolicy(Long complexId) {
        //TODO 관리자 소속 단지 확인
        //TODO complexId 기준 visitor_policy 조회
        return VisitorPolicyGetRes.builder()
                .complexId(complexId)
                .freeMinutes(0)
                .hourFee(null)
                .monthlyLimitHours(300)
                .isActive(true)
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
