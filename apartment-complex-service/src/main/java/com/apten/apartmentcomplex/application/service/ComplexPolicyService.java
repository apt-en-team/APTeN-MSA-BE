package com.apten.apartmentcomplex.application.service;

import com.apten.apartmentcomplex.application.model.request.ComplexPolicyPutReq;
import com.apten.apartmentcomplex.application.model.request.FacilityPolicyPutReq;
import com.apten.apartmentcomplex.application.model.request.VehiclePolicyPutReq;
import com.apten.apartmentcomplex.application.model.request.VisitorPolicyPutReq;
import com.apten.apartmentcomplex.application.model.response.ComplexPolicyPutRes;
import com.apten.apartmentcomplex.application.model.response.FacilityPolicyPutRes;
import com.apten.apartmentcomplex.application.model.response.VehiclePolicyPutRes;
import com.apten.apartmentcomplex.application.model.response.VisitorPolicyPutRes;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

// 단지 정책 응용 서비스
// 기본 정책과 차량, 시설, 방문차량 정책 시그니처를 이 서비스에 모아둔다
@Service
public class ComplexPolicyService {

    // 기본 정책 설정 서비스
    public ComplexPolicyPutRes updateBasicPolicy(String apartmentComplexUid, ComplexPolicyPutReq request) {
        // TODO: 기본 정책 설정 로직 구현
        return ComplexPolicyPutRes.builder()
                .apartmentComplexUid(apartmentComplexUid)
                .defaultMaintenanceFee(request.getDefaultMaintenanceFee())
                .defaultReservationSlotMin(request.getDefaultReservationSlotMin())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // 차량 정책 설정 서비스
    public VehiclePolicyPutRes updateVehiclePolicy(String apartmentComplexUid, VehiclePolicyPutReq request) {
        // TODO: 차량 정책 설정 로직 구현
        return VehiclePolicyPutRes.builder()
                .apartmentComplexUid(apartmentComplexUid)
                .maxVehicleCountPerHousehold(request.getMaxVehicleCountPerHousehold())
                .freeVehicleCount(request.getFreeVehicleCount())
                .extraVehicleFee(request.getExtraVehicleFee())
                .visitorFreeMinutes(request.getVisitorFreeMinutes())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // 시설 정책 설정 서비스
    public FacilityPolicyPutRes updateFacilityPolicy(String apartmentComplexUid, FacilityPolicyPutReq request) {
        // TODO: 시설 정책 설정 로직 구현
        return FacilityPolicyPutRes.builder()
                .apartmentComplexUid(apartmentComplexUid)
                .reservationSlotMin(request.getReservationSlotMin())
                .facilityCancelDeadlineHours(request.getFacilityCancelDeadlineHours())
                .gxWaitingEnabled(request.getGxWaitingEnabled())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // 방문차량 정책 설정 서비스
    public VisitorPolicyPutRes updateVisitorPolicy(String apartmentComplexUid, VisitorPolicyPutReq request) {
        // TODO: 방문차량 정책 설정 로직 구현
        return VisitorPolicyPutRes.builder()
                .apartmentComplexUid(apartmentComplexUid)
                .freeMinutes(request.getFreeMinutes())
                .extraFeePerUnit(request.getExtraFeePerUnit())
                .extraFeeUnitMinutes(request.getExtraFeeUnitMinutes())
                .dailyMaxFee(request.getDailyMaxFee())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
