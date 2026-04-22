package com.apten.household.application.service;

import com.apten.household.application.model.request.HouseholdBillCalcReq;
import com.apten.household.application.model.request.HouseholdBillPolicyPutReq;
import com.apten.household.application.model.request.HouseholdBillSearchReq;
import com.apten.household.application.model.response.HouseholdBillCalcRes;
import com.apten.household.application.model.response.HouseholdBillConfirmRes;
import com.apten.household.application.model.response.HouseholdBillPolicyPutRes;
import com.apten.household.application.model.response.HouseholdBillRes;
import com.apten.household.infrastructure.mapper.HouseholdBillQueryMapper;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

// 세대 비용 도메인 응용 서비스
// 비용 정책, 산정, 조회 시그니처만 이 서비스에 모아둔다
@Service
@RequiredArgsConstructor
public class HouseholdBillService {

    // 세대 비용 도메인 조회용 MyBatis 매퍼
    private final ObjectProvider<HouseholdBillQueryMapper> householdBillQueryMapperProvider;

    // 기본 관리비 정책 설정 서비스
    public HouseholdBillPolicyPutRes updateBasicBillPolicy(HouseholdBillPolicyPutReq request) {
        // TODO: 기본 관리비 정책 설정 로직 구현
        return HouseholdBillPolicyPutRes.builder().updatedAt(LocalDateTime.now()).build();
    }

    // 차량 비용 산정 서비스
    public HouseholdBillCalcRes calculateVehicleBill(HouseholdBillCalcReq request) {
        // TODO: 차량 비용 산정 로직 구현
        return HouseholdBillCalcRes.builder().calculatedAt(LocalDateTime.now()).build();
    }

    // 시설 비용 반영 서비스
    public HouseholdBillCalcRes calculateFacilityBill(HouseholdBillCalcReq request) {
        // TODO: 시설 비용 반영 로직 구현
        return HouseholdBillCalcRes.builder().calculatedAt(LocalDateTime.now()).build();
    }

    // 방문차량 이용시간 반영 서비스
    public HouseholdBillCalcRes calculateVisitorUsageBill(HouseholdBillCalcReq request) {
        // TODO: 방문차량 이용시간 반영 로직 구현
        return HouseholdBillCalcRes.builder().calculatedAt(LocalDateTime.now()).build();
    }

    // 월별 비용 확정 서비스
    public HouseholdBillConfirmRes confirmHouseholdBill(String billUid) {
        // TODO: 월별 비용 확정 로직 구현
        return HouseholdBillConfirmRes.builder().confirmedAt(LocalDateTime.now()).build();
    }

    // 입주민 본인 세대 비용 조회 서비스
    public List<HouseholdBillRes> getMyBills(HouseholdBillSearchReq request) {
        // TODO: 입주민 세대 비용 조회 로직 구현
        return List.of();
    }
}
