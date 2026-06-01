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
import com.apten.household.domain.entity.FacilityUsageSnapshot;
import com.apten.household.domain.entity.Household;
import com.apten.household.domain.entity.HouseholdBill;
import com.apten.household.domain.entity.HouseholdBillItem;
import com.apten.household.domain.entity.HouseholdMember;
import com.apten.household.domain.entity.VisitorUsageSnapshot;
import com.apten.household.domain.enums.HouseholdBillItemType;
import com.apten.household.domain.enums.HouseholdBillStatus;
import com.apten.household.domain.repository.FacilityUsageSnapshotRepository;
import com.apten.household.domain.repository.HouseholdBillItemRepository;
import com.apten.household.domain.repository.HouseholdBillRepository;
import com.apten.household.domain.repository.HouseholdMemberRepository;
import com.apten.household.domain.repository.HouseholdRepository;
import com.apten.household.domain.repository.VisitorUsageSnapshotRepository;
import com.apten.household.exception.HouseholdErrorCode;
import com.apten.common.exception.BusinessException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 세대 청구 반영과 확정, 조회 시그니처를 모아두는 서비스이다.
@Service
@Transactional
@RequiredArgsConstructor
public class HouseholdBillService {

    // 세대 월 청구 저장소이다.
    private final HouseholdBillRepository householdBillRepository;

    // 세대 월 청구 상세 항목 저장소이다.
    private final HouseholdBillItemRepository householdBillItemRepository;

    // 세대 저장소이다.
    private final HouseholdRepository householdRepository;

    // 세대원 저장소이다.
    private final HouseholdMemberRepository householdMemberRepository;

    // 시설 이용 스냅샷 저장소이다.
    private final FacilityUsageSnapshotRepository facilityUsageSnapshotRepository;

    // 방문차량 이용 스냅샷 저장소이다.
    private final VisitorUsageSnapshotRepository visitorUsageSnapshotRepository;

    // 차량 비용 반영 서비스이다.
    public VehicleFeeReflectRes reflectVehicleFee(VehicleFeeReflectReq request) {
        // 주차 서비스의 월 차량 비용 산정 계약이 확정되면 청구 항목에 반영한다.
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
        LocalDate fromDate = LocalDate.of(request.getBillYear(), request.getBillMonth(), 1);
        LocalDate toDate = fromDate.plusMonths(1).minusDays(1);
        Map<Long, BigDecimal> feeByHouseholdId = facilityUsageSnapshotRepository
                .findByComplexIdAndUsageDateBetween(request.getComplexId(), fromDate, toDate)
                .stream()
                .collect(Collectors.groupingBy(
                        FacilityUsageSnapshot::getHouseholdId,
                        Collectors.reducing(BigDecimal.ZERO, FacilityUsageSnapshot::getUsageFee, BigDecimal::add)
                ));

        feeByHouseholdId.forEach((householdId, facilityFee) -> {
            Household household = getHouseholdForComplex(request.getComplexId(), householdId);
            HouseholdBill bill = getOrCreateBill(household, request.getBillYear(), request.getBillMonth());
            upsertBillItem(bill, HouseholdBillItemType.FACILITY_FEE, "시설이용비용", facilityFee, "시설 이용 스냅샷 합산");
            bill.updateAmounts(
                    bill.getBaseFee(),
                    bill.getVehicleFee(),
                    facilityFee,
                    bill.getVisitorFee(),
                    bill.getBaseFee().add(bill.getVehicleFee()).add(facilityFee).add(bill.getVisitorFee())
            );
            householdBillRepository.save(bill);
        });

        return FacilityFeeReflectRes.builder()
                .complexId(request.getComplexId())
                .billYear(request.getBillYear())
                .billMonth(request.getBillMonth())
                .affectedHouseholdCount(feeByHouseholdId.size())
                .reflectedAt(LocalDateTime.now())
                .build();
    }

    // 방문차량 비용 반영 서비스이다.
    public VisitorFeeReflectRes reflectVisitorFee(VisitorFeeReflectReq request) {
        List<VisitorFeeReflectReq.Item> requestItems = request.getItems() == null ? List.of() : request.getItems();
        requestItems.forEach(item -> {
            Household household = getHouseholdForComplex(request.getComplexId(), item.getHouseholdId());
            VisitorUsageSnapshot snapshot = visitorUsageSnapshotRepository
                    .findByHouseholdIdAndUsageYearAndUsageMonth(item.getHouseholdId(), request.getBillYear(), request.getBillMonth())
                    .orElseGet(() -> VisitorUsageSnapshot.builder().build());
            snapshot.apply(
                    item.getHouseholdId(),
                    request.getComplexId(),
                    request.getBillYear(),
                    request.getBillMonth(),
                    zeroIfNull(item.getTotalMinutes()),
                    zeroIfNull(item.getTotalHours()),
                    zeroIfNull(item.getFreeMinutes()),
                    zeroIfNull(item.getExtraMinutes()),
                    zeroIfNull(item.getVisitorFee())
            );
            visitorUsageSnapshotRepository.save(snapshot);

            HouseholdBill bill = getOrCreateBill(household, request.getBillYear(), request.getBillMonth());
            BigDecimal visitorFee = zeroIfNull(item.getVisitorFee());
            upsertBillItem(bill, HouseholdBillItemType.VISITOR_FEE, "방문차량비용", visitorFee, "방문차량 월 이용시간 정산");
            bill.updateAmounts(
                    bill.getBaseFee(),
                    bill.getVehicleFee(),
                    bill.getFacilityFee(),
                    visitorFee,
                    bill.getBaseFee().add(bill.getVehicleFee()).add(bill.getFacilityFee()).add(visitorFee)
            );
            householdBillRepository.save(bill);
        });

        return VisitorFeeReflectRes.builder()
                .complexId(request.getComplexId())
                .billYear(request.getBillYear())
                .billMonth(request.getBillMonth())
                .items(requestItems.stream()
                        .map(item -> VisitorFeeReflectRes.Item.builder()
                                .householdId(item.getHouseholdId())
                                .totalMinutes(item.getTotalMinutes())
                                .totalHours(item.getTotalHours())
                                .freeMinutes(item.getFreeMinutes())
                                .extraMinutes(item.getExtraMinutes())
                                .visitorFee(item.getVisitorFee())
                                .build())
                        .toList())
                .affectedHouseholdCount(requestItems.size())
                .reflectedAt(LocalDateTime.now())
                .build();
    }

    // 월별 비용 확정 서비스이다.
    public BillConfirmRes confirmBill(Long complexId, Long billId) {
        HouseholdBill bill = getBillForComplex(complexId, billId);
        if (bill.getStatus() == HouseholdBillStatus.CONFIRMED) {
            throw new BusinessException(HouseholdErrorCode.BILL_ALREADY_CONFIRMED);
        }
        LocalDateTime confirmedAt = LocalDateTime.now();
        bill.confirm(confirmedAt);
        householdBillRepository.save(bill);

        return BillConfirmRes.builder()
                .billId(bill.getId())
                .status(bill.getStatus())
                .confirmedAt(confirmedAt)
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
        PageRequest pageable = PageRequest.of(pageNumber(request.getPage()), pageSize(request.getSize()));
        Page<HouseholdBill> page = householdBillRepository.findAdminBills(
                complexId,
                request.getBillYear(),
                request.getBillMonth(),
                request.getStatus(),
                blankToNull(request.getBuilding()),
                blankToNull(request.getUnit()),
                pageable
        );
        Map<Long, Household> householdMap = householdRepository.findAllById(
                        page.getContent().stream().map(HouseholdBill::getHouseholdId).collect(Collectors.toSet())
                )
                .stream()
                .collect(Collectors.toMap(Household::getId, Function.identity()));

        return AdminHouseholdBillListRes.builder()
                .content(page.getContent().stream()
                        .map(bill -> {
                            Household household = householdMap.get(bill.getHouseholdId());
                            return AdminHouseholdBillListRes.Item.builder()
                                    .billId(bill.getId())
                                    .householdId(bill.getHouseholdId())
                                    .building(household == null ? null : household.getBuilding())
                                    .unit(household == null ? null : household.getUnit())
                                    .billYear(bill.getBillYear())
                                    .billMonth(bill.getBillMonth())
                                    .totalFee(bill.getTotalFee())
                                    .status(bill.getStatus())
                                    .build();
                        })
                        .toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .hasNext(page.hasNext())
                .build();
    }

    // 관리자 관리비 상세 조회 서비스이다.
    public AdminHouseholdBillDetailRes getAdminBillDetail(Long complexId, Long billId) {
        HouseholdBill bill = getBillForComplex(complexId, billId);
        Household household = getHouseholdForComplex(complexId, bill.getHouseholdId());

        return AdminHouseholdBillDetailRes.builder()
                .billId(bill.getId())
                .householdId(bill.getHouseholdId())
                .building(household.getBuilding())
                .unit(household.getUnit())
                .billYear(bill.getBillYear())
                .billMonth(bill.getBillMonth())
                .baseFee(bill.getBaseFee())
                .vehicleFee(bill.getVehicleFee())
                .facilityFee(bill.getFacilityFee())
                .visitorFee(bill.getVisitorFee())
                .totalFee(bill.getTotalFee())
                .status(bill.getStatus())
                .items(householdBillItemRepository.findByBillId(bill.getId()).stream()
                        .map(item -> AdminHouseholdBillDetailRes.Item.builder()
                                .itemType(item.getItemType())
                                .itemName(item.getItemName())
                                .amount(item.getAmount())
                                .calcMemo(item.getCalcMemo())
                                .build())
                        .toList())
                .confirmedAt(bill.getConfirmedAt())
                .build();
    }

    // 세대 비용 조회 서비스이다.
    public MyBillListRes getMyBills(Long userId, Long complexId, MyBillListReq request) {
        HouseholdMember myMember = householdMemberRepository.findActiveByUserIdAndComplexId(userId, complexId)
                .orElseThrow(() -> new BusinessException(HouseholdErrorCode.HOUSEHOLD_MEMBER_NOT_FOUND));
        PageRequest pageable = PageRequest.of(pageNumber(request.getPage()), pageSize(request.getSize()));
        Page<HouseholdBill> page = householdBillRepository.findResidentBills(
                myMember.getHouseholdId(),
                complexId,
                HouseholdBillStatus.CONFIRMED,
                request.getBillYear(),
                request.getBillMonth(),
                pageable
        );

        return MyBillListRes.builder()
                .content(page.getContent().stream()
                        .map(bill -> MyBillListRes.Item.builder()
                                .billId(bill.getId())
                                .billYear(bill.getBillYear())
                                .billMonth(bill.getBillMonth())
                                .totalFee(bill.getTotalFee())
                                .status(bill.getStatus())
                                .confirmedAt(bill.getConfirmedAt())
                                .createdAt(bill.getCreatedAt())
                                .build())
                        .toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .hasNext(page.hasNext())
                .build();
    }

    private HouseholdBill getBillForComplex(Long complexId, Long billId) {
        return householdBillRepository.findByIdAndComplexId(billId, complexId)
                .orElseThrow(() -> new BusinessException(HouseholdErrorCode.BILL_NOT_FOUND));
    }

    private Household getHouseholdForComplex(Long complexId, Long householdId) {
        return householdRepository.findByIdAndComplexId(householdId, complexId)
                .orElseThrow(() -> new BusinessException(HouseholdErrorCode.HOUSEHOLD_NOT_FOUND));
    }

    private HouseholdBill getOrCreateBill(Household household, Integer billYear, Integer billMonth) {
        return householdBillRepository.findByHouseholdIdAndBillYearAndBillMonth(household.getId(), billYear, billMonth)
                .orElseGet(() -> householdBillRepository.save(HouseholdBill.builder()
                        .householdId(household.getId())
                        .complexId(household.getComplexId())
                        .billYear(billYear)
                        .billMonth(billMonth)
                        .status(HouseholdBillStatus.DRAFT)
                        .build()));
    }

    private void upsertBillItem(
            HouseholdBill bill,
            HouseholdBillItemType itemType,
            String itemName,
            BigDecimal amount,
            String calcMemo
    ) {
        HouseholdBillItem item = householdBillItemRepository.findByBillIdAndItemType(bill.getId(), itemType)
                .orElseGet(() -> HouseholdBillItem.builder()
                        .billId(bill.getId())
                        .build());
        item.apply(itemType, itemName, zeroIfNull(amount), calcMemo);
        householdBillItemRepository.save(item);
    }

    private Integer pageNumber(Integer page) {
        return page == null || page < 0 ? 0 : page;
    }

    private Integer pageSize(Integer size) {
        return size == null || size < 1 ? 20 : size;
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }

    private BigDecimal zeroIfNull(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private Integer zeroIfNull(Integer value) {
        return value == null ? 0 : value;
    }
}
