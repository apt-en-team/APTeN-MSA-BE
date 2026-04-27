package com.apten.household.application.service;

import com.apten.household.application.model.request.HouseholdBillCalcReq;
import com.apten.household.application.model.request.HouseholdBillSearchReq;
import com.apten.household.application.model.response.HouseholdBillCalcRes;
import com.apten.household.application.model.response.HouseholdBillConfirmRes;
import com.apten.household.application.model.response.HouseholdBillRes;
import com.apten.household.infrastructure.mapper.HouseholdBillQueryMapper;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

// 세대 비용 도메인 응용 서비스
// 청구 계산과 확정, 조회 흐름만 이 서비스에 모아둔다
@Service
@RequiredArgsConstructor
public class HouseholdBillService {

    // 세대 비용 도메인 조회용 MyBatis 매퍼
    private final ObjectProvider<HouseholdBillQueryMapper> householdBillQueryMapperProvider;

    // 차량 비용 산정 서비스
    public HouseholdBillCalcRes calculateVehicleBill(HouseholdBillCalcReq request) {
        // TODO: bill_policy에서 complexId 기준 활성 정책을 조회한다.
        // TODO: parking-vehicle-service가 전달한 차량 비용 결과 스냅샷을 household_bill_item에 반영한다.
        return HouseholdBillCalcRes.builder().calculatedAt(LocalDateTime.now()).build();
    }

    // 시설 비용 반영 서비스
    public HouseholdBillCalcRes calculateFacilityBill(HouseholdBillCalcReq request) {
        // TODO: facility_usage_snapshot.usage_fee를 합산해 household_bill_item에 반영한다.
        return HouseholdBillCalcRes.builder().calculatedAt(LocalDateTime.now()).build();
    }

    // 방문차량 이용시간 반영 서비스
    public HouseholdBillCalcRes calculateVisitorUsageBill(HouseholdBillCalcReq request) {
        // TODO: parking-vehicle-service가 계산한 방문차량 이용시간 또는 비용 결과를 반영한다.
        return HouseholdBillCalcRes.builder().calculatedAt(LocalDateTime.now()).build();
    }

    // 월별 비용 확정 서비스
    public HouseholdBillConfirmRes confirmHouseholdBill(String billUid) {
        // TODO: household_bill과 household_bill_item을 확정 상태로 변경한다.
        return HouseholdBillConfirmRes.builder().confirmedAt(LocalDateTime.now()).build();
    }

    // 입주민 본인 세대 비용 조회 서비스
    public List<HouseholdBillRes> getMyBills(HouseholdBillSearchReq request) {
        // TODO: 입주민 세대 비용 조회 로직 구현
        return List.of();
    }
}
