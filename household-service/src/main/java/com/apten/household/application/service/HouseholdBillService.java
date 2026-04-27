package com.apten.household.application.service;

import com.apten.household.application.model.request.FacilityFeeReflectReq;
import com.apten.household.application.model.request.MyBillListReq;
import com.apten.household.application.model.request.VehicleFeeReflectReq;
import com.apten.household.application.model.request.VisitorFeeReflectReq;
import com.apten.household.application.model.response.BillConfirmRes;
import com.apten.household.application.model.response.FacilityFeeReflectRes;
import com.apten.household.application.model.response.MyBillListRes;
import com.apten.household.application.model.response.VehicleFeeReflectRes;
import com.apten.household.application.model.response.VisitorFeeReflectRes;
import com.apten.household.domain.enums.HouseholdBillStatus;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

// 세대 청구 반영과 확정, 조회 시그니처를 모아두는 서비스이다.
@Service
@RequiredArgsConstructor
public class HouseholdBillService {

    // 차량 비용 반영 서비스이다.
    public VehicleFeeReflectRes reflectVehicleFee(VehicleFeeReflectReq request) {
        //TODO 월 청구 데이터 조회
        //TODO vehicle_snapshot 또는 외부 반영 결과 기준 차량 비용 계산
        //TODO household_bill과 household_bill_item 반영
        return VehicleFeeReflectRes.builder()
                .complexId(request.getComplexId())
                .billYear(request.getBillYear())
                .billMonth(request.getBillMonth())
                .affectedHouseholdCount(0)
                .reflectedAt(LocalDateTime.now())
                .build();
    }

    // 시설 비용 반영 서비스이다.
    public FacilityFeeReflectRes reflectFacilityFee(FacilityFeeReflectReq request) {
        //TODO facility_usage_snapshot.usage_fee 합산
        //TODO household_bill과 household_bill_item 반영
        return FacilityFeeReflectRes.builder()
                .complexId(request.getComplexId())
                .billYear(request.getBillYear())
                .billMonth(request.getBillMonth())
                .affectedHouseholdCount(0)
                .reflectedAt(LocalDateTime.now())
                .build();
    }

    // 방문차량 비용 반영 서비스이다.
    public VisitorFeeReflectRes reflectVisitorFee(VisitorFeeReflectReq request) {
        //TODO visitor_usage_snapshot 집계 결과 조회
        //TODO household_bill과 household_bill_item 반영
        return VisitorFeeReflectRes.builder()
                .complexId(request.getComplexId())
                .billYear(request.getBillYear())
                .billMonth(request.getBillMonth())
                .affectedHouseholdCount(0)
                .reflectedAt(LocalDateTime.now())
                .build();
    }

    // 월별 비용 확정 서비스이다.
    public BillConfirmRes confirmBill(Long billId) {
        //TODO 청구 존재 여부 확인
        //TODO 이미 확정된 청구인지 확인
        //TODO household_bill 상태를 CONFIRMED로 변경
        return BillConfirmRes.builder()
                .billId(billId)
                .status(HouseholdBillStatus.CONFIRMED)
                .confirmedAt(LocalDateTime.now())
                .build();
    }

    // 세대 비용 조회 서비스이다.
    public MyBillListRes getMyBills(MyBillListReq request) {
        //TODO 로그인 사용자 기준 세대 조회
        //TODO 확정된 청구 목록 조회
        return MyBillListRes.builder()
                .content(List.of())
                .page(request.getPage())
                .size(request.getSize())
                .totalElements(0L)
                .totalPages(0)
                .hasNext(false)
                .build();
    }
}
