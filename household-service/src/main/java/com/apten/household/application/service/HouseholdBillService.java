package com.apten.household.application.service;

import com.apten.common.exception.BusinessException;
import com.apten.household.application.model.request.AdminHouseholdBillListReq;
import com.apten.household.application.model.request.BaseFeeReflectReq;
import com.apten.household.application.model.request.FacilityFeeReflectReq;
import com.apten.household.application.model.request.MyBillListReq;
import com.apten.household.application.model.request.VehicleFeeReflectReq;
import com.apten.household.application.model.request.VisitorFeeReflectReq;
import com.apten.household.application.model.response.AdminHouseholdBillDetailRes;
import com.apten.household.application.model.response.AdminHouseholdBillListRes;
import com.apten.household.application.model.response.BaseFeeReflectRes;
import com.apten.household.application.model.response.BillConfirmRes;
import com.apten.household.application.model.response.BillUnconfirmRes;
import com.apten.household.application.model.response.FacilityFeeReflectRes;
import com.apten.household.application.model.response.MyBillListRes;
import com.apten.household.application.model.response.VehicleFeeReflectRes;
import com.apten.household.application.model.response.VisitorFeeReflectRes;
import com.apten.household.domain.entity.ComplexPolicy;
import com.apten.household.domain.entity.Household;
import com.apten.household.domain.entity.HouseholdBill;
import com.apten.household.domain.entity.HouseholdBillItem;
import com.apten.household.domain.entity.HouseholdMember;
import com.apten.household.domain.entity.FacilityUsageSnapshot;
import com.apten.household.domain.entity.VisitorUsageSnapshot;
import com.apten.household.domain.entity.VehicleSnapshot;
import com.apten.household.domain.enums.FacilityUsageStatus;
import com.apten.household.domain.enums.HouseholdBillItemType;
import com.apten.household.domain.enums.HouseholdBillStatus;
import com.apten.household.domain.enums.HouseholdStatus;
import com.apten.household.domain.enums.VehicleSnapshotStatus;
import com.apten.household.domain.repository.ComplexPolicyRepository;
import com.apten.household.domain.repository.FacilityUsageSnapshotRepository;
import com.apten.household.domain.repository.HouseholdBillItemRepository;
import com.apten.household.domain.repository.HouseholdBillRepository;
import com.apten.household.domain.repository.HouseholdMemberRepository;
import com.apten.household.domain.repository.HouseholdRepository;
import com.apten.household.domain.repository.VisitorUsageSnapshotRepository;
import com.apten.household.domain.repository.VehicleSnapshotRepository;
import com.apten.household.exception.HouseholdErrorCode;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class HouseholdBillService {

    private final HouseholdBillRepository householdBillRepository;
    private final HouseholdBillItemRepository householdBillItemRepository;
    private final HouseholdRepository householdRepository;
    private final HouseholdMemberRepository householdMemberRepository;
    private final VisitorUsageSnapshotRepository visitorUsageSnapshotRepository;
    private final FacilityUsageSnapshotRepository facilityUsageSnapshotRepository;
    private final VehicleSnapshotRepository vehicleSnapshotRepository;
    private final ComplexPolicyRepository complexPolicyRepository;

    public BaseFeeReflectRes reflectBaseFee(Long complexId, BaseFeeReflectReq request) {
        if (request == null) {
            throw new BusinessException(HouseholdErrorCode.INVALID_BILL_AMOUNT);
        }
        validateReflectRequest(complexId, request.getBillYear(), request.getBillMonth());
        ComplexPolicy policy = complexPolicyRepository.findByComplexId(complexId)
                .filter(complexPolicy -> Boolean.TRUE.equals(complexPolicy.getIsActive()))
                .orElseThrow(() -> new BusinessException(HouseholdErrorCode.BILL_POLICY_NOT_FOUND));
        BigDecimal baseFee = defaultAmount(policy.getBaseFee());

        List<Household> households = householdRepository.findByComplexIdAndStatus(complexId, HouseholdStatus.OCCUPIED);
        households.forEach(household -> {
            HouseholdBill bill = getOrCreateBill(complexId, household.getId(), request.getBillYear(), request.getBillMonth());
            validateBillEditable(bill);
            updateBillAmounts(bill, baseFee, bill.getVehicleFee(), bill.getFacilityFee(), bill.getVisitorFee());
            householdBillRepository.save(bill);
            upsertBillItem(bill.getId(), HouseholdBillItemType.BASE_FEE, HouseholdBillItemType.BASE_FEE.getValue(), baseFee, "월별 기본 관리비 반영");
        });

        return BaseFeeReflectRes.builder()
                .complexId(complexId)
                .billYear(request.getBillYear())
                .billMonth(request.getBillMonth())
                .affectedHouseholdCount(households.size())
                .reflectedAt(LocalDateTime.now())
                .build();
    }

    public VehicleFeeReflectRes reflectVehicleFee(VehicleFeeReflectReq request) {
        validateReflectRequest(request.getComplexId(), request.getBillYear(), request.getBillMonth());
        List<VehicleSnapshot> snapshots = vehicleSnapshotRepository.findByComplexIdAndStatusAndIsDeletedFalse(
                request.getComplexId(),
                VehicleSnapshotStatus.APPROVED
        );

        snapshots.stream()
                .map(VehicleSnapshot::getHouseholdId)
                .distinct()
                .forEach(householdId -> {
                    HouseholdBill bill = getOrCreateBill(request.getComplexId(), householdId, request.getBillYear(), request.getBillMonth());
                    validateBillEditable(bill);
                });

        return VehicleFeeReflectRes.builder()
                .complexId(request.getComplexId())
                .billYear(request.getBillYear())
                .billMonth(request.getBillMonth())
                .affectedHouseholdCount((int) snapshots.stream()
                        .map(VehicleSnapshot::getHouseholdId)
                        .distinct()
                        .count())
                .reflectedAt(LocalDateTime.now())
                .build();
    }

    public FacilityFeeReflectRes reflectFacilityFee(FacilityFeeReflectReq request) {
        validateReflectRequest(request.getComplexId(), request.getBillYear(), request.getBillMonth());
        LocalDate fromDate = LocalDate.of(request.getBillYear(), request.getBillMonth(), 1);
        LocalDate toDate = fromDate.withDayOfMonth(fromDate.lengthOfMonth());
        Map<Long, BigDecimal> feeByHousehold = facilityUsageSnapshotRepository
                .findByComplexIdAndUsageDateBetweenAndStatus(
                        request.getComplexId(),
                        fromDate,
                        toDate,
                        FacilityUsageStatus.COMPLETED
                )
                .stream()
                .collect(Collectors.groupingBy(
                        FacilityUsageSnapshot::getHouseholdId,
                        Collectors.reducing(BigDecimal.ZERO, FacilityUsageSnapshot::getUsageFee, BigDecimal::add)
                ));

        feeByHousehold.forEach((householdId, facilityFee) -> {
            getHouseholdForComplex(request.getComplexId(), householdId);
            HouseholdBill bill = getOrCreateBill(request.getComplexId(), householdId, request.getBillYear(), request.getBillMonth());
            validateBillEditable(bill);
            BigDecimal amount = defaultAmount(facilityFee);
            updateBillAmounts(bill, bill.getBaseFee(), bill.getVehicleFee(), amount, bill.getVisitorFee());
            householdBillRepository.save(bill);
            upsertBillItem(bill.getId(), HouseholdBillItemType.FACILITY_FEE, HouseholdBillItemType.FACILITY_FEE.getValue(), amount, "시설 이용 월별 반영");
        });

        return FacilityFeeReflectRes.builder()
                .complexId(request.getComplexId())
                .billYear(request.getBillYear())
                .billMonth(request.getBillMonth())
                .affectedHouseholdCount(feeByHousehold.size())
                .reflectedAt(LocalDateTime.now())
                .build();
    }

    public VisitorFeeReflectRes reflectVisitorFee(VisitorFeeReflectReq request) {
        validateReflectRequest(request.getComplexId(), request.getBillYear(), request.getBillMonth());

        List<VisitorFeeReflectReq.Item> items = request.getItems() == null ? List.of() : request.getItems();
        List<VisitorFeeReflectRes.Item> reflectedItems = items.stream()
                .map(item -> reflectVisitorFeeItem(request, item))
                .toList();

        return VisitorFeeReflectRes.builder()
                .complexId(request.getComplexId())
                .billYear(request.getBillYear())
                .billMonth(request.getBillMonth())
                .items(reflectedItems)
                .affectedHouseholdCount(reflectedItems.size())
                .reflectedAt(LocalDateTime.now())
                .build();
    }

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

    public BillUnconfirmRes unconfirmBill(Long complexId, Long billId) {
        HouseholdBill bill = getBillForComplex(complexId, billId);
        if (bill.getStatus() != HouseholdBillStatus.CONFIRMED) {
            throw new BusinessException(HouseholdErrorCode.BILL_NOT_CONFIRMED);
        }

        bill.unconfirm();
        householdBillRepository.save(bill);

        return BillUnconfirmRes.builder()
                .billId(bill.getId())
                .status(bill.getStatus())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Transactional(readOnly = true)
    public AdminHouseholdBillListRes getAdminBills(Long complexId, AdminHouseholdBillListReq request) {
        int pageNumber = request.getPage() != null ? request.getPage() : 0;
        int pageSize = request.getSize() != null ? request.getSize() : 20;

        Page<HouseholdBill> page = householdBillRepository.findAdminBills(
                complexId,
                request.getBillYear(),
                request.getBillMonth(),
                request.getStatus(),
                blankToNull(request.getBuilding()),
                blankToNull(request.getUnit()),
                PageRequest.of(pageNumber, pageSize)
        );

        return AdminHouseholdBillListRes.builder()
                .content(page.getContent().stream()
                        .map(this::toAdminBillListItem)
                        .toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .hasNext(page.hasNext())
                .build();
    }

    @Transactional(readOnly = true)
    public AdminHouseholdBillDetailRes getAdminBillDetail(Long complexId, Long billId) {
        HouseholdBill bill = getBillForComplex(complexId, billId);
        Household household = getHouseholdForComplex(complexId, bill.getHouseholdId());
        List<HouseholdBillItem> items = householdBillItemRepository.findByBillId(bill.getId());

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
                .items(items.stream()
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

    @Transactional(readOnly = true)
    public MyBillListRes getMyBills(Long userId, Long complexId, MyBillListReq request) {
        HouseholdMember member = householdMemberRepository.findActiveByUserIdAndComplexId(userId, complexId)
                .orElseThrow(() -> new BusinessException(HouseholdErrorCode.HOUSEHOLD_MEMBER_NOT_FOUND));
        getHouseholdForComplex(complexId, member.getHouseholdId());

        int pageNumber = request.getPage() != null ? request.getPage() : 0;
        int pageSize = request.getSize() != null ? request.getSize() : 20;
        Page<HouseholdBill> page = householdBillRepository.findMyBills(
                member.getHouseholdId(),
                complexId,
                HouseholdBillStatus.CONFIRMED,
                request.getBillYear(),
                request.getBillMonth(),
                PageRequest.of(pageNumber, pageSize)
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
        HouseholdBill bill = householdBillRepository.findById(billId)
                .orElseThrow(() -> new BusinessException(HouseholdErrorCode.BILL_NOT_FOUND));
        if (!bill.getComplexId().equals(complexId)) {
            throw new BusinessException(HouseholdErrorCode.BILL_NOT_FOUND);
        }
        return bill;
    }

    private Household getHouseholdForComplex(Long complexId, Long householdId) {
        return householdRepository.findByIdAndComplexId(householdId, complexId)
                .orElseThrow(() -> new BusinessException(HouseholdErrorCode.HOUSEHOLD_NOT_FOUND));
    }

    private AdminHouseholdBillListRes.Item toAdminBillListItem(HouseholdBill bill) {
        Household household = getHouseholdForComplex(bill.getComplexId(), bill.getHouseholdId());
        return AdminHouseholdBillListRes.Item.builder()
                .billId(bill.getId())
                .householdId(bill.getHouseholdId())
                .building(household.getBuilding())
                .unit(household.getUnit())
                .billYear(bill.getBillYear())
                .billMonth(bill.getBillMonth())
                .totalFee(bill.getTotalFee())
                .status(bill.getStatus())
                .build();
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }

    private VisitorFeeReflectRes.Item reflectVisitorFeeItem(VisitorFeeReflectReq request, VisitorFeeReflectReq.Item item) {
        if (item == null || item.getHouseholdId() == null) {
            throw new BusinessException(HouseholdErrorCode.HOUSEHOLD_NOT_FOUND);
        }
        getHouseholdForComplex(request.getComplexId(), item.getHouseholdId());

        VisitorUsageSnapshot snapshot = visitorUsageSnapshotRepository
                .findByHouseholdIdAndUsageYearAndUsageMonth(item.getHouseholdId(), request.getBillYear(), request.getBillMonth())
                .orElseGet(() -> VisitorUsageSnapshot.builder()
                        .householdId(item.getHouseholdId())
                        .complexId(request.getComplexId())
                        .usageYear(request.getBillYear())
                        .usageMonth(request.getBillMonth())
                        .build());

        snapshot.apply(
                item.getHouseholdId(),
                request.getComplexId(),
                request.getBillYear(),
                request.getBillMonth(),
                defaultInt(item.getTotalMinutes()),
                defaultAmount(item.getTotalHours()),
                defaultInt(item.getFreeMinutes()),
                defaultInt(item.getExtraMinutes()),
                defaultAmount(item.getVisitorFee())
        );
        visitorUsageSnapshotRepository.save(snapshot);

        HouseholdBill bill = getOrCreateBill(request.getComplexId(), item.getHouseholdId(), request.getBillYear(), request.getBillMonth());
        validateBillEditable(bill);
        BigDecimal visitorFee = defaultAmount(item.getVisitorFee());
        updateBillAmounts(bill, bill.getBaseFee(), bill.getVehicleFee(), bill.getFacilityFee(), visitorFee);
        householdBillRepository.save(bill);
        upsertBillItem(bill.getId(), HouseholdBillItemType.VISITOR_FEE, HouseholdBillItemType.VISITOR_FEE.getValue(), visitorFee, "방문차량 월별 반영");

        return VisitorFeeReflectRes.Item.builder()
                .householdId(item.getHouseholdId())
                .totalMinutes(defaultInt(item.getTotalMinutes()))
                .totalHours(defaultAmount(item.getTotalHours()))
                .freeMinutes(defaultInt(item.getFreeMinutes()))
                .extraMinutes(defaultInt(item.getExtraMinutes()))
                .visitorFee(visitorFee)
                .build();
    }

    private HouseholdBill getOrCreateBill(Long complexId, Long householdId, Integer billYear, Integer billMonth) {
        return householdBillRepository.findByHouseholdIdAndBillYearAndBillMonth(householdId, billYear, billMonth)
                .orElseGet(() -> householdBillRepository.save(HouseholdBill.builder()
                        .complexId(complexId)
                        .householdId(householdId)
                        .billYear(billYear)
                        .billMonth(billMonth)
                        .build()));
    }

    private void validateBillEditable(HouseholdBill bill) {
        if (bill.getStatus() == HouseholdBillStatus.CONFIRMED) {
            throw new BusinessException(HouseholdErrorCode.BILL_ALREADY_CONFIRMED);
        }
    }

    private void updateBillAmounts(
            HouseholdBill bill,
            BigDecimal baseFee,
            BigDecimal vehicleFee,
            BigDecimal facilityFee,
            BigDecimal visitorFee
    ) {
        BigDecimal safeBaseFee = defaultAmount(baseFee);
        BigDecimal safeVehicleFee = defaultAmount(vehicleFee);
        BigDecimal safeFacilityFee = defaultAmount(facilityFee);
        BigDecimal safeVisitorFee = defaultAmount(visitorFee);
        bill.updateAmounts(
                safeBaseFee,
                safeVehicleFee,
                safeFacilityFee,
                safeVisitorFee,
                safeBaseFee.add(safeVehicleFee).add(safeFacilityFee).add(safeVisitorFee)
        );
    }

    private void upsertBillItem(Long billId, HouseholdBillItemType itemType, String itemName, BigDecimal amount, String calcMemo) {
        HouseholdBillItem item = householdBillItemRepository.findByBillIdAndItemType(billId, itemType)
                .orElseGet(() -> HouseholdBillItem.builder()
                        .billId(billId)
                        .build());
        item.apply(itemType, itemName, amount, calcMemo);
        householdBillItemRepository.save(item);
    }

    private void validateReflectRequest(Long complexId, Integer billYear, Integer billMonth) {
        if (complexId == null || billYear == null || billMonth == null || billMonth < 1 || billMonth > 12) {
            throw new BusinessException(HouseholdErrorCode.INVALID_BILL_AMOUNT);
        }
    }

    private Integer defaultInt(Integer value) {
        return value == null ? 0 : value;
    }

    private BigDecimal defaultAmount(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
