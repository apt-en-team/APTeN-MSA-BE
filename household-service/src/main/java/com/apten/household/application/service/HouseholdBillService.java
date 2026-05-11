package com.apten.household.application.service;

import com.apten.household.application.model.request.AdminHouseholdBillListReq;
import com.apten.household.application.model.request.FacilityFeeReflectReq;
import com.apten.household.application.model.request.MyBillListReq;
import com.apten.household.application.model.request.VehicleFeeReflectReq;
import com.apten.household.application.model.request.VisitorFeeReflectReq;
import com.apten.household.application.model.response.AdminHouseholdBillDetailRes;
import com.apten.household.application.model.response.AdminHouseholdBillListRes;
import com.apten.household.application.model.response.BillConfirmRes;
import com.apten.household.application.model.response.BillUnconfirmRes;
import com.apten.household.application.model.response.FacilityFeeReflectRes;
import com.apten.household.application.model.response.MyBillListRes;
import com.apten.household.application.model.response.VehicleFeeReflectRes;
import com.apten.household.application.model.response.VisitorFeeReflectRes;
import com.apten.household.domain.entity.HouseholdBill;
import com.apten.household.domain.enums.HouseholdBillItemType;
import com.apten.household.domain.enums.HouseholdBillStatus;
import com.apten.household.domain.repository.HouseholdBillRepository;
import com.apten.household.exception.HouseholdErrorCode;
import com.apten.common.exception.BusinessException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

// 세대 청구 반영과 확정, 조회 시그니처를 모아두는 서비스이다.
@Service
@RequiredArgsConstructor
public class HouseholdBillService {

    // 세대 월 청구 저장소이다.
    private final HouseholdBillRepository householdBillRepository;

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
        //TODO 방문차량 비용 반영 이벤트 또는 내부 API 요청 수신
        //TODO Parking 서비스에서 산정한 방문차량 이용시간과 비용을 수신
        //TODO complexId, billYear, billMonth 기준 유효성 검증
        //TODO householdId별 visitor_usage_snapshot 기존 데이터 조회
        //TODO 기존 데이터가 있으면 totalMinutes, totalHours, freeMinutes, extraMinutes, visitorFee 갱신
        //TODO 기존 데이터가 없으면 visitor_usage_snapshot 신규 생성
        //TODO visitor_usage_snapshot에 월별 방문차량 비용 결과 upsert
        //TODO household_bill의 visitorFee와 totalFee에 반영
        //TODO 처리 완료 건수와 반영 시간을 응답
        return VisitorFeeReflectRes.builder()
                .complexId(request.getComplexId())
                .billYear(request.getBillYear())
                .billMonth(request.getBillMonth())
                .items(request.getItems() == null ? List.of() : request.getItems().stream()
                        .map(item -> VisitorFeeReflectRes.Item.builder()
                                .householdId(item.getHouseholdId())
                                .totalMinutes(item.getTotalMinutes())
                                .totalHours(item.getTotalHours())
                                .freeMinutes(item.getFreeMinutes())
                                .extraMinutes(item.getExtraMinutes())
                                .visitorFee(item.getVisitorFee())
                                .build())
                        .toList())
                .affectedHouseholdCount(request.getItems() == null ? 0 : request.getItems().size())
                .reflectedAt(LocalDateTime.now())
                .build();
    }

    // 월별 비용 확정 서비스이다.
    public BillConfirmRes confirmBill(Long complexId, Long billId) {
        //TODO billId가 현재 complexId 소속 청구인지 검증
        //TODO 청구 존재 여부 확인
        //TODO 이미 확정된 청구인지 확인
        //TODO household_bill 상태를 CONFIRMED로 변경
        return BillConfirmRes.builder()
                .billId(billId)
                .status(HouseholdBillStatus.CONFIRMED)
                .confirmedAt(LocalDateTime.now())
                .build();
    }

    // 월별 비용 확정 취소 서비스이다 (FR-426)
    public BillUnconfirmRes unconfirmBill(Long complexId, Long billId) {
        // billId가 현재 complexId 소속 청구인지 검증한다.
        HouseholdBill bill = householdBillRepository.findById(billId)
                .orElseThrow(() -> new BusinessException(HouseholdErrorCode.BILL_NOT_FOUND));
        if (!bill.getComplexId().equals(complexId)) {
            throw new BusinessException(HouseholdErrorCode.BILL_NOT_FOUND);
        }
        // CONFIRMED 상태인지 확인한다.
        if (bill.getStatus() != HouseholdBillStatus.CONFIRMED) {
            throw new BusinessException(HouseholdErrorCode.BILL_NOT_CONFIRMED);
        }
        // DRAFT로 롤백하고 confirmedAt을 초기화한다.
        bill.unconfirm();
        householdBillRepository.save(bill);

        return BillUnconfirmRes.builder()
                .billId(bill.getId())
                .status(bill.getStatus())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // 관리자 관리비 목록 조회 서비스이다.
    public AdminHouseholdBillListRes getAdminBills(Long complexId, AdminHouseholdBillListReq request) {
        //TODO Header에서 해석한 complexId 기준으로 billYear, billMonth, status, building, unit 조건 조회
        //TODO request.complexId는 더 이상 외부 관리자 조회 기준으로 사용하지 않는다.
        //TODO 페이지 메타데이터 계산
        return AdminHouseholdBillListRes.builder()
                .content(List.of())
                .page(request.getPage())
                .size(request.getSize())
                .totalElements(0L)
                .totalPages(0)
                .hasNext(false)
                .build();
    }

    // 관리자 관리비 상세 조회 서비스이다.
    public AdminHouseholdBillDetailRes getAdminBillDetail(Long complexId, Long billId) {
        //TODO billId가 현재 complexId 소속 청구인지 검증
        //TODO billId 기준 관리비 헤더 조회
        //TODO 세대 동호수와 청구 항목 목록 조회
        return AdminHouseholdBillDetailRes.builder()
                .billId(billId)
                .baseFee(BigDecimal.ZERO)
                .vehicleFee(BigDecimal.ZERO)
                .facilityFee(BigDecimal.ZERO)
                .visitorFee(BigDecimal.ZERO)
                .totalFee(BigDecimal.ZERO)
                .status(HouseholdBillStatus.DRAFT)
                .items(List.of(
                        AdminHouseholdBillDetailRes.Item.builder()
                                .itemType(HouseholdBillItemType.BASE_FEE)
                                .itemName("기본관리비")
                                .amount(BigDecimal.ZERO)
                                .calcMemo(null)
                                .build()
                ))
                .confirmedAt(null)
                .build();
    }

    // 세대 비용 조회 서비스이다.
    public MyBillListRes getMyBills(Long userId, Long complexId, MyBillListReq request) {
        //TODO 로그인 사용자 기준 활성 세대원과 household를 조회한다.
        //TODO household의 complexId와 Header에서 해석한 complexId가 일치하는지 검증한다.
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
