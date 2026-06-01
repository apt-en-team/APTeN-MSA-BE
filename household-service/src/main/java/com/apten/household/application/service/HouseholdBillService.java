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
import com.apten.household.application.model.response.BillComparisonRes;
import com.apten.household.application.model.response.BillConfirmRes;
import com.apten.household.application.model.response.BillUnconfirmRes;
import com.apten.household.application.model.response.FacilityFeeReflectRes;
import com.apten.household.application.model.response.MyBillListRes;
import com.apten.household.application.model.response.VehicleFeeReflectRes;
import com.apten.household.application.model.response.VisitorFeeReflectRes;
import com.apten.household.domain.entity.BuildingLineType;
import com.apten.household.domain.entity.ComplexPolicy;
import com.apten.household.domain.entity.Household;
import com.apten.household.domain.entity.HouseholdBill;
import com.apten.household.domain.entity.HouseholdType;
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
import com.apten.household.domain.repository.BuildingLineTypeRepository;
import com.apten.household.domain.repository.ComplexPolicyRepository;
import com.apten.household.domain.repository.FacilityUsageSnapshotRepository;
import com.apten.household.domain.repository.HouseholdBillItemRepository;
import com.apten.household.domain.repository.HouseholdBillRepository;
import com.apten.household.domain.repository.HouseholdMemberRepository;
import com.apten.household.domain.repository.HouseholdRepository;
import com.apten.household.domain.repository.HouseholdTypeRepository;
import com.apten.household.domain.repository.VisitorUsageSnapshotRepository;
import com.apten.household.domain.repository.VehicleSnapshotRepository;
import com.apten.household.exception.HouseholdErrorCode;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class HouseholdBillService {

    private final HouseholdBillRepository householdBillRepository;
    private final HouseholdBillItemRepository householdBillItemRepository;
    private final HouseholdRepository householdRepository;
    private final HouseholdMemberRepository householdMemberRepository;
    private final HouseholdTypeRepository householdTypeRepository;
    private final BuildingLineTypeRepository buildingLineTypeRepository;
    private final VisitorUsageSnapshotRepository visitorUsageSnapshotRepository;
    private final FacilityUsageSnapshotRepository facilityUsageSnapshotRepository;
    private final VehicleSnapshotRepository vehicleSnapshotRepository;
    private final ComplexPolicyRepository complexPolicyRepository;

    // 단지 기본 관리비 정책을 해당 월의 입주 세대 청구서에 반영한다.
    public BaseFeeReflectRes reflectBaseFee(Long complexId, BaseFeeReflectReq request) {
        if (request == null) {
            throw new BusinessException(HouseholdErrorCode.INVALID_BILL_AMOUNT);
        }
        validateReflectRequest(complexId, request.getBillYear(), request.getBillMonth());
        ComplexPolicy policy = getActivePolicy(complexId);
        BigDecimal baseFee = defaultAmount(policy.getBaseFee());

        List<Household> households = householdRepository.findByComplexIdAndStatus(complexId, HouseholdStatus.OCCUPIED);
        households.forEach(household -> {
            HouseholdBill bill = getOrCreateBill(complexId, household.getId(), request.getBillYear(), request.getBillMonth());
            validateBillEditable(bill);
            updateBillAmounts(bill, baseFee, bill.getVehicleFee(), bill.getFacilityFee(), bill.getVisitorFee());
            applyPolicySchedule(bill, policy);
            refreshLateFee(bill, LocalDate.now());
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

    // 차량 서비스에서 전달된 차량 스냅샷 기준으로 차량 비용 반영 대상을 준비한다.
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

    // 시설 이용 스냅샷을 월 단위로 합산해 세대별 관리비 항목에 반영한다.
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

    // 방문차량 서비스에서 전달된 세대별 월 이용 금액을 관리비에 반영한다.
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

    // 관리자 확정 처리로 주민에게 노출 가능한 청구 상태로 전환한다.
    public BillConfirmRes confirmBill(Long complexId, Long billId) {
        HouseholdBill bill = getBillForComplex(complexId, billId);
        if (bill.getStatus() == HouseholdBillStatus.CONFIRMED) {
            throw new BusinessException(HouseholdErrorCode.BILL_ALREADY_CONFIRMED);
        }

        ComplexPolicy policy = getActivePolicy(complexId);
        LocalDateTime confirmedAt = LocalDateTime.now();
        applyPolicySchedule(bill, policy);
        refreshLateFee(bill, LocalDate.now());
        bill.confirm(confirmedAt);
        householdBillRepository.save(bill);

        return BillConfirmRes.builder()
                .billId(bill.getId())
                .status(bill.getStatus())
                .confirmedAt(confirmedAt)
                .build();
    }

    // 확정된 청구를 다시 임시계산 상태로 되돌린다.
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

    // 관리자 관리비 목록을 조회 조건과 페이지 기준으로 조회한다.
    @Transactional(readOnly = true)
    public AdminHouseholdBillListRes getAdminBills(Long complexId, AdminHouseholdBillListReq request) {
        int pageNumber = request.getPage() != null ? request.getPage() : 0;
        int pageSize = request.getSize() != null ? request.getSize() : 20;

        log.info("[getAdminBills] building={}, unit={}", request.getBuilding(), request.getUnit());

        Page<HouseholdBill> page = householdBillRepository.findAdminBills(
                complexId,
                request.getBillYear(),
                request.getBillMonth(),
                request.getStatus(),
                blankToNull(request.getBuilding()),
                blankToNull(request.getUnit()),
                PageRequest.of(pageNumber, pageSize)
        );

        Map<Long, Household> householdById = findHouseholdsById(complexId, page.getContent());

        return AdminHouseholdBillListRes.builder()
                .content(page.getContent().stream()
                        .map(bill -> toAdminBillListItem(bill, householdById))
                        .toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .hasNext(page.hasNext())
                .build();
    }

    // 관리자 관리비 상세는 세대 정보와 청구 항목 목록을 함께 반환한다.
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
                .lateFee(effectiveLateFee(bill, LocalDate.now()))
                .payableAmount(effectivePayableAmount(bill, LocalDate.now()))
                .sendDate(bill.getSendDate())
                .dueDate(bill.getDueDate())
                .overdueStartDate(bill.getOverdueStartDate())
                .homeDisplayUntil(bill.getHomeDisplayUntil())
                .overdue(isOverdue(bill, LocalDate.now()))
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

    // 주민은 본인 세대의 확정된 관리비만 조회할 수 있다.
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
                LocalDate.now(),
                PageRequest.of(pageNumber, pageSize)
        );

        return MyBillListRes.builder()
                .content(page.getContent().stream()
                        .map(bill -> MyBillListRes.Item.builder()
                                .billId(bill.getId())
                                .billYear(bill.getBillYear())
                                .billMonth(bill.getBillMonth())
                                .totalFee(bill.getTotalFee())
                                .lateFee(effectiveLateFee(bill, LocalDate.now()))
                                .payableAmount(effectivePayableAmount(bill, LocalDate.now()))
                                .sendDate(bill.getSendDate())
                                .dueDate(bill.getDueDate())
                                .overdueStartDate(bill.getOverdueStartDate())
                                .homeDisplayUntil(bill.getHomeDisplayUntil())
                                .overdue(isOverdue(bill, LocalDate.now()))
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

    @Transactional(readOnly = true)
    public AdminHouseholdBillDetailRes getMyBillDetail(Long userId, Long complexId, Long billId) {
        HouseholdMember member = householdMemberRepository.findActiveByUserIdAndComplexId(userId, complexId)
                .orElseThrow(() -> new BusinessException(HouseholdErrorCode.HOUSEHOLD_MEMBER_NOT_FOUND));
        HouseholdBill bill = getBillForComplex(complexId, billId);
        if (!bill.getHouseholdId().equals(member.getHouseholdId())
                || bill.getStatus() != HouseholdBillStatus.CONFIRMED
                || isBeforeSendDate(bill, LocalDate.now())) {
            throw new BusinessException(HouseholdErrorCode.BILL_NOT_FOUND);
        }
        return getAdminBillDetail(complexId, billId);
    }

    // 동일 평형 세대와의 최근 6개월 관리비 비교 데이터를 반환한다.
    @Transactional(readOnly = true)
    public BillComparisonRes getMyBillComparison(Long userId, Long complexId, Long billId) {
        HouseholdMember member = householdMemberRepository.findActiveByUserIdAndComplexId(userId, complexId)
                .orElseThrow(() -> new BusinessException(HouseholdErrorCode.HOUSEHOLD_MEMBER_NOT_FOUND));
        HouseholdBill bill = getBillForComplex(complexId, billId);
        if (!bill.getHouseholdId().equals(member.getHouseholdId())) {
            throw new BusinessException(HouseholdErrorCode.BILL_NOT_FOUND);
        }

        Household household = getHouseholdForComplex(complexId, bill.getHouseholdId());

        // typeId가 없으면 building_line_type에서 unit 라인 번호로 보완한다.
        Long resolvedTypeId = household.getTypeId();
        if (resolvedTypeId == null) {
            try {
                int lineNumber = Integer.parseInt(household.getUnit()) % 10;
                resolvedTypeId = buildingLineTypeRepository.findActiveByUnitLine(
                                complexId, household.getBuilding(), lineNumber)
                        .map(blt -> blt.getTypeId())
                        .orElse(null);
            } catch (NumberFormatException ignored) {}
        }

        // 평형 정보 조회
        BigDecimal areaSize = null;
        if (resolvedTypeId != null) {
            areaSize = householdTypeRepository.findById(resolvedTypeId)
                    .map(HouseholdType::getExclusiveAreaM2)
                    .orElse(null);
        }

        // 최근 6개월 범위 계산
        List<YearMonth> months = buildSixMonths(bill.getBillYear(), bill.getBillMonth());
        int fromYm = toYearMonthInt(months.get(0));
        int toYm = toYearMonthInt(months.get(5));

        // 내 세대 청구 이력
        List<HouseholdBill> myBills = householdBillRepository.findByHouseholdIdAndYearMonthRange(
                bill.getHouseholdId(), fromYm, toYm);
        Map<Integer, BigDecimal> myBillMap = myBills.stream()
                .collect(Collectors.toMap(
                        b -> b.getBillYear() * 100 + b.getBillMonth(),
                        HouseholdBill::getTotalFee
                ));

        List<BigDecimal> myAmounts = months.stream()
                .map(ym -> myBillMap.getOrDefault(toYearMonthInt(ym), BigDecimal.ZERO))
                .toList();

        BigDecimal myAverage = calculateAverage(myAmounts);

        // 동일 평형 청구 이력
        List<BigDecimal> avgAmounts;
        BigDecimal sameTypeAverage = BigDecimal.ZERO;
        Integer rank = null;
        Integer totalHouseholds = null;

        // typeId가 있는 세대 + typeId=null이지만 같은 building_line_type인 세대 모두 포함
        List<Long> sameTypeHouseholdIds = resolveSameTypeHouseholdIds(
                complexId, household, resolvedTypeId);

        if (!sameTypeHouseholdIds.isEmpty()) {
            List<HouseholdBill> sameBills = householdBillRepository.findByHouseholdIdsAndYearMonthRange(
                    sameTypeHouseholdIds, HouseholdBillStatus.CONFIRMED, fromYm, toYm);

            avgAmounts = months.stream()
                    .map(ym -> {
                        int key = toYearMonthInt(ym);
                        List<BigDecimal> fees = sameBills.stream()
                                .filter(b -> b.getBillYear() * 100 + b.getBillMonth() == key)
                                .map(HouseholdBill::getTotalFee)
                                .toList();
                        return fees.isEmpty() ? BigDecimal.ZERO : calculateAverage(fees);
                    })
                    .toList();

            sameTypeAverage = calculateAverage(avgAmounts);

            // 현재 청구월 기준 순위 계산
            int currentYm = bill.getBillYear() * 100 + bill.getBillMonth();
            List<BigDecimal> currentMonthFees = sameBills.stream()
                    .filter(b -> b.getBillYear() * 100 + b.getBillMonth() == currentYm)
                    .map(HouseholdBill::getTotalFee)
                    .sorted()
                    .toList();

            if (!currentMonthFees.isEmpty()) {
                totalHouseholds = currentMonthFees.size();
                BigDecimal myFee = myBillMap.getOrDefault(currentYm, BigDecimal.ZERO);
                long lowerCount = currentMonthFees.stream()
                        .filter(fee -> fee.compareTo(myFee) < 0)
                        .count();
                rank = (int) lowerCount + 1;
            }
        } else {
            avgAmounts = months.stream().map(ym -> BigDecimal.ZERO).toList();
            sameTypeAverage = BigDecimal.ZERO;
        }

        return BillComparisonRes.builder()
                .areaSize(areaSize)
                .myAmounts(myAmounts)
                .myAverage(myAverage)
                .avgAmounts(avgAmounts)
                .sameTypeAverage(sameTypeAverage)
                .rank(rank)
                .totalHouseholds(totalHouseholds)
                .build();
    }

    // typeId가 있는 세대와 null이지만 같은 building_line_type 범위인 세대 IDs를 반환한다.
    private List<Long> resolveSameTypeHouseholdIds(Long complexId, Household household, Long typeId) {
        if (typeId == null) return List.of();

        int lineNumber;
        try {
            lineNumber = Integer.parseInt(household.getUnit()) % 10;
        } catch (NumberFormatException e) {
            return householdRepository.findByComplexIdAndBuilding(complexId, household.getBuilding())
                    .stream()
                    .filter(h -> typeId.equals(h.getTypeId()))
                    .map(Household::getId)
                    .toList();
        }

        final int finalLineNumber = lineNumber;
        BuildingLineType myLineType = buildingLineTypeRepository
                .findActiveByUnitLine(complexId, household.getBuilding(), lineNumber)
                .orElse(null);

        return householdRepository.findByComplexIdAndBuilding(complexId, household.getBuilding())
                .stream()
                .filter(h -> {
                    if (typeId.equals(h.getTypeId())) return true;
                    if (h.getTypeId() == null && myLineType != null) {
                        try {
                            int ln = Integer.parseInt(h.getUnit()) % 10;
                            return ln >= myLineType.getLineStart() && ln <= myLineType.getLineEnd();
                        } catch (NumberFormatException e) {
                            return false;
                        }
                    }
                    return false;
                })
                .map(Household::getId)
                .toList();
    }

    private List<YearMonth> buildSixMonths(int year, int month) {
        YearMonth current = YearMonth.of(year, month);
        List<YearMonth> months = new ArrayList<>();
        for (int i = 5; i >= 0; i--) {
            months.add(current.minusMonths(i));
        }
        return months;
    }

    private int toYearMonthInt(YearMonth ym) {
        return ym.getYear() * 100 + ym.getMonthValue();
    }

    private BigDecimal calculateAverage(List<BigDecimal> amounts) {
        List<BigDecimal> nonZero = amounts.stream()
                .filter(a -> a.compareTo(BigDecimal.ZERO) > 0)
                .toList();
        if (nonZero.isEmpty()) return BigDecimal.ZERO;
        return nonZero.stream().reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(nonZero.size()), 0, RoundingMode.HALF_UP);
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

    private ComplexPolicy getActivePolicy(Long complexId) {
        return complexPolicyRepository.findByComplexId(complexId)
                .filter(complexPolicy -> Boolean.TRUE.equals(complexPolicy.getIsActive()))
                .orElseThrow(() -> new BusinessException(HouseholdErrorCode.BILL_POLICY_NOT_FOUND));
    }

    private Map<Long, Household> findHouseholdsById(Long complexId, List<HouseholdBill> bills) {
        List<Long> householdIds = bills.stream()
                .map(HouseholdBill::getHouseholdId)
                .distinct()
                .toList();
        if (householdIds.isEmpty()) {
            return Map.of();
        }
        return householdRepository.findByComplexIdAndIdIn(complexId, householdIds).stream()
                .collect(Collectors.toMap(Household::getId, Function.identity()));
    }

    private AdminHouseholdBillListRes.Item toAdminBillListItem(HouseholdBill bill, Map<Long, Household> householdById) {
        Household household = householdById.get(bill.getHouseholdId());
        if (household == null) {
            throw new BusinessException(HouseholdErrorCode.HOUSEHOLD_NOT_FOUND);
        }
        return AdminHouseholdBillListRes.Item.builder()
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
                .lateFee(effectiveLateFee(bill, LocalDate.now()))
                .payableAmount(effectivePayableAmount(bill, LocalDate.now()))
                .sendDate(bill.getSendDate())
                .dueDate(bill.getDueDate())
                .overdueStartDate(bill.getOverdueStartDate())
                .homeDisplayUntil(bill.getHomeDisplayUntil())
                .overdue(isOverdue(bill, LocalDate.now()))
                .status(bill.getStatus())
                .build();
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }

    // 방문차량 한 세대분 사용량을 스냅샷과 청구 항목에 함께 반영한다.
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

    // 세대·청구년월 조합의 청구서가 없으면 임시계산 상태로 새로 생성한다.
    private HouseholdBill getOrCreateBill(Long complexId, Long householdId, Integer billYear, Integer billMonth) {
        return householdBillRepository.findByHouseholdIdAndBillYearAndBillMonth(householdId, billYear, billMonth)
                .orElseGet(() -> householdBillRepository.save(HouseholdBill.builder()
                        .complexId(complexId)
                        .householdId(householdId)
                        .billYear(billYear)
                        .billMonth(billMonth)
                        .build()));
    }

    // 확정된 청구서는 비용 재반영으로 수정할 수 없다.
    private void validateBillEditable(HouseholdBill bill) {
        if (bill.getStatus() == HouseholdBillStatus.CONFIRMED) {
            throw new BusinessException(HouseholdErrorCode.BILL_ALREADY_CONFIRMED);
        }
    }

    // 항목별 금액을 보정한 뒤 총액을 다시 계산한다.
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

    // 같은 청구서의 같은 항목은 새로 만들지 않고 금액과 계산 메모를 갱신한다.
    private void upsertBillItem(Long billId, HouseholdBillItemType itemType, String itemName, BigDecimal amount, String calcMemo) {
        HouseholdBillItem item = householdBillItemRepository.findByBillIdAndItemType(billId, itemType)
                .orElseGet(() -> HouseholdBillItem.builder()
                        .billId(billId)
                        .build());
        item.apply(itemType, itemName, amount, calcMemo);
        householdBillItemRepository.save(item);
    }

    private void applyPolicySchedule(HouseholdBill bill, ComplexPolicy policy) {
        BillingSchedule schedule = buildBillingSchedule(bill.getBillYear(), bill.getBillMonth(), policy);
        bill.applySchedule(
                schedule.sendDate(),
                schedule.dueDate(),
                schedule.overdueStartDate(),
                schedule.homeDisplayUntil(),
                defaultAmount(policy.getLateFeeRate())
        );
    }

    private BillingSchedule buildBillingSchedule(Integer billYear, Integer billMonth, ComplexPolicy policy) {
        LocalDate sendDate = resolveDate(billYear, billMonth, policy.getSendDay());
        LocalDate dueDate = moveWeekendToNextMonday(resolveDate(billYear, billMonth, policy.getDueDay()));
        LocalDate overdueStartDate = dueDate.plusDays(1);
        LocalDate homeDisplayUntil = resolveNextMonthDate(billYear, billMonth, policy.getHomeDisplayEndDay());
        return new BillingSchedule(sendDate, dueDate, overdueStartDate, homeDisplayUntil);
    }

    private LocalDate resolveDate(Integer year, Integer month, Integer day) {
        YearMonth yearMonth = YearMonth.of(year, month);
        return yearMonth.atDay(Math.min(day, yearMonth.lengthOfMonth()));
    }

    private LocalDate resolveNextMonthDate(Integer year, Integer month, Integer day) {
        YearMonth yearMonth = YearMonth.of(year, month).plusMonths(1);
        return yearMonth.atDay(Math.min(day, yearMonth.lengthOfMonth()));
    }

    private LocalDate moveWeekendToNextMonday(LocalDate date) {
        if (date.getDayOfWeek() == DayOfWeek.SATURDAY) {
            return date.plusDays(2);
        }
        if (date.getDayOfWeek() == DayOfWeek.SUNDAY) {
            return date.plusDays(1);
        }
        return date;
    }

    private void refreshLateFee(HouseholdBill bill, LocalDate today) {
        bill.updateLateFee(calculateLateFee(bill, today));
    }

    private BigDecimal calculateLateFee(HouseholdBill bill, LocalDate today) {
        if (bill.getOverdueStartDate() == null || today.isBefore(bill.getOverdueStartDate())) {
            return BigDecimal.ZERO;
        }
        return defaultAmount(bill.getTotalFee())
                .multiply(defaultAmount(bill.getLateFeeRate()))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    private Boolean isOverdue(HouseholdBill bill, LocalDate today) {
        return bill.getOverdueStartDate() != null && !today.isBefore(bill.getOverdueStartDate());
    }

    private Boolean isBeforeSendDate(HouseholdBill bill, LocalDate today) {
        return bill.getSendDate() != null && today.isBefore(bill.getSendDate());
    }

    private BigDecimal effectiveLateFee(HouseholdBill bill, LocalDate today) {
        if (isOverdue(bill, today)) {
            return calculateLateFee(bill, today);
        }
        return defaultAmount(bill.getLateFee());
    }

    private BigDecimal effectivePayableAmount(HouseholdBill bill, LocalDate today) {
        return defaultAmount(bill.getTotalFee()).add(effectiveLateFee(bill, today));
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

    private record BillingSchedule(
            LocalDate sendDate,
            LocalDate dueDate,
            LocalDate overdueStartDate,
            LocalDate homeDisplayUntil
    ) {
    }
}
