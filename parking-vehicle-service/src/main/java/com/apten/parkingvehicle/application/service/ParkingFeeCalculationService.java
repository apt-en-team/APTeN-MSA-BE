package com.apten.parkingvehicle.application.service;

import com.apten.common.exception.BusinessException;
import com.apten.common.exception.CommonErrorCode;
import com.apten.parkingvehicle.application.model.request.VehicleFeeCalculateReq;
import com.apten.parkingvehicle.application.model.request.VisitorFeeCalculateReq;
import com.apten.parkingvehicle.application.model.response.AdminVehicleFeeMonthlyListRes;
import com.apten.parkingvehicle.application.model.response.AdminVisitorUsageMonthlyListRes;
import com.apten.parkingvehicle.application.model.response.PageResponse;
import com.apten.parkingvehicle.application.model.response.VehicleFeeCalculateRes;
import com.apten.parkingvehicle.application.model.response.VehicleFeeMonthlyGetRes;
import com.apten.parkingvehicle.application.model.response.VisitorFeeCalculateRes;
import com.apten.parkingvehicle.application.model.response.VisitorUsageMonthlyGetRes;
import com.apten.parkingvehicle.application.support.RoleContextValidator;
import com.apten.parkingvehicle.domain.entity.HouseholdCache;
import com.apten.parkingvehicle.domain.entity.ParkingLog;
import com.apten.parkingvehicle.domain.entity.VehicleFeeMonthly;
import com.apten.parkingvehicle.domain.entity.VehiclePolicy;
import com.apten.parkingvehicle.domain.entity.VisitorPolicy;
import com.apten.parkingvehicle.domain.entity.VisitorUsageMonthly;
import com.apten.parkingvehicle.domain.enums.ParkingEntryType;
import com.apten.parkingvehicle.domain.enums.VehicleStatus;
import com.apten.parkingvehicle.domain.repository.HouseholdCacheRepository;
import com.apten.parkingvehicle.domain.repository.ParkingLogRepository;
import com.apten.parkingvehicle.domain.repository.VehicleFeeMonthlyRepository;
import com.apten.parkingvehicle.domain.repository.VehiclePolicyRepository;
import com.apten.parkingvehicle.domain.repository.VehicleRepository;
import com.apten.parkingvehicle.domain.repository.VisitorPolicyRepository;
import com.apten.parkingvehicle.domain.repository.VisitorUsageMonthlyRepository;
import com.apten.parkingvehicle.exception.ParkingVehicleErrorCode;
import com.apten.parkingvehicle.infrastructure.kafka.ParkingVehicleOutboxService;
import com.apten.parkingvehicle.infrastructure.kafka.payload.VehicleFeeCalculatedEventPayload;
import com.apten.parkingvehicle.infrastructure.kafka.payload.VisitorFeeCalculatedEventPayload;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 차량 비용과 방문차량 비용 월 산정과 조회를 담당하는 응용 서비스이다.
@Slf4j
@Service
@RequiredArgsConstructor
public class ParkingFeeCalculationService {

    // 방문차량 정책 저장소
    private final VisitorPolicyRepository visitorPolicyRepository;

    // 차량 정책 저장소
    private final VehiclePolicyRepository vehiclePolicyRepository;

    // 입출차 로그 저장소
    private final ParkingLogRepository parkingLogRepository;

    // 입주민 차량 저장소
    private final VehicleRepository vehicleRepository;

    // 방문차량 월별 이용시간/비용 저장소
    private final VisitorUsageMonthlyRepository visitorUsageMonthlyRepository;

    // 차량 월별 비용 저장소
    private final VehicleFeeMonthlyRepository vehicleFeeMonthlyRepository;

    // 세대 캐시 저장소
    private final HouseholdCacheRepository householdCacheRepository;

    // outbox 적재 서비스
    private final ParkingVehicleOutboxService parkingVehicleOutboxService;

    // 차량 비용을 월 기준으로 산정한다.
    // 단지 활성 차량 정책 row의 carCount 오름차순 단가표를 룩업해 세대별 승인 차량 수에 매칭되는 월 요금을 정액 산정한다.
    @Transactional
    public VehicleFeeCalculateRes calculateVehicleFees(VehicleFeeCalculateReq request) {
        // 요청 기본 필드 검증
        if (request == null
                || request.getComplexId() == null
                || request.getBillYear() == null
                || request.getBillMonth() == null) {
            throw new BusinessException(ParkingVehicleErrorCode.INVALID_PARAMETER);
        }

        Long complexId = request.getComplexId();
        int billYear = request.getBillYear();
        int billMonth = request.getBillMonth();

        // 단지 활성 차량 정책 row 전수 조회 + carCount 오름차순 정렬, 없으면 산정 불가
        List<VehiclePolicy> sortedPolicies = vehiclePolicyRepository.findByComplexIdAndIsActiveTrue(complexId).stream()
                .sorted(Comparator.comparingInt(VehiclePolicy::getCarCount))
                .toList();
        if (sortedPolicies.isEmpty()) {
            throw new BusinessException(ParkingVehicleErrorCode.VEHICLE_POLICY_NOT_FOUND);
        }

        // 단지 세대 목록 조회 — 세대마다 카운트 후 산정
        List<HouseholdCache> households = householdCacheRepository.findByComplexId(complexId);
        LocalDateTime publishedAt = LocalDateTime.now();

        // 세대별 row upsert + payload/응답 Item 빌드
        List<VehicleFeeCalculatedEventPayload.Item> payloadItems = new ArrayList<>();
        List<VehicleFeeCalculateRes.Item> responseItems = new ArrayList<>();
        for (HouseholdCache household : households) {
            Long householdId = household.getId();
            int approvedCount = (int) vehicleRepository
                    .countByHouseholdIdAndStatusAndIsDeletedFalse(householdId, VehicleStatus.APPROVED);

            // 0대 세대는 row 안 만듦 (조회 시 404 정책과 일관)
            if (approvedCount <= 0) {
                continue;
            }

            // 정책 매칭으로 월 요금 도출
            BigDecimal monthlyFee = matchVehicleFee(sortedPolicies, approvedCount, householdId);

            upsertVehicleFeeMonthly(
                    complexId, householdId, billYear, billMonth,
                    approvedCount, monthlyFee, publishedAt
            );

            payloadItems.add(VehicleFeeCalculatedEventPayload.Item.builder()
                    .householdId(householdId)
                    .vehicleFee(monthlyFee)
                    .build());
            responseItems.add(VehicleFeeCalculateRes.Item.builder()
                    .householdId(householdId)
                    .approvedVehicleCount(approvedCount)
                    .vehicleFee(monthlyFee)
                    .build());
        }

        // outbox 발행 — 같은 트랜잭션 안이라 직렬화/저장 실패 시 함께 롤백
        VehicleFeeCalculatedEventPayload payload = VehicleFeeCalculatedEventPayload.builder()
                .complexId(complexId)
                .billYear(billYear)
                .billMonth(billMonth)
                .items(payloadItems)
                .occurredAt(publishedAt)
                .build();
        parkingVehicleOutboxService.saveVehicleFeeCalculatedEvent(complexId, payload);

        return VehicleFeeCalculateRes.builder()
                .complexId(complexId)
                .billYear(billYear)
                .billMonth(billMonth)
                .items(responseItems)
                .published(true)
                .executedAt(publishedAt)
                .build();
    }

    // 방문차량 비용을 월 기준으로 산정한다.
    // 활성 방문차량 정책의 한도와 시간당 단가로 단지의 청구월 방문차량/고정 방문차량 체류시간을 세대별로 합산해 초과분만 과금한다.
    @Transactional
    public VisitorFeeCalculateRes calculateVisitorFees(VisitorFeeCalculateReq request) {
        // 요청 기본 필드 검증
        if (request == null
                || request.getComplexId() == null
                || request.getBillYear() == null
                || request.getBillMonth() == null) {
            throw new BusinessException(ParkingVehicleErrorCode.INVALID_PARAMETER);
        }

        Long complexId = request.getComplexId();
        int billYear = request.getBillYear();
        int billMonth = request.getBillMonth();

        // 단지 활성 방문차량 정책 조회 — 비활성/미설정이면 산정 불가
        VisitorPolicy policy = visitorPolicyRepository.findByComplexIdAndIsActiveTrue(complexId)
                .orElseThrow(() -> new BusinessException(ParkingVehicleErrorCode.VISITOR_POLICY_NOT_FOUND));

        // 청구월 범위 산출 — [월 1일 0시, 익월 1일 0시)
        YearMonth yearMonth = YearMonth.of(billYear, billMonth);
        LocalDateTime fromDateTime = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime toDateTime = yearMonth.plusMonths(1).atDay(1).atStartOfDay();

        // 방문차량/고정 방문차량 입출차 로그 일괄 로드
        List<ParkingLog> logs = parkingLogRepository
                .findVisitorLogsForFeeCalculation(complexId, fromDateTime, toDateTime);

        // 그룹 키 단위 IN/OUT 짝짓기 → 세대별 체류 분 합산
        Map<Long, Integer> householdMinutes = aggregateHouseholdMinutes(logs);

        // 한도와 단가
        int limitMinutes = policy.getMonthlyLimitHours() * 60;
        BigDecimal hourFee = policy.getHourFee();
        LocalDateTime publishedAt = LocalDateTime.now();

        // 세대별 row upsert + payload/응답 Item 빌드
        List<VisitorFeeCalculatedEventPayload.Item> payloadItems = new ArrayList<>();
        List<VisitorFeeCalculateRes.Item> responseItems = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : householdMinutes.entrySet()) {
            Long householdId = entry.getKey();
            int totalMinutes = entry.getValue();
            int extraMinutes = Math.max(0, totalMinutes - limitMinutes);
            int freeMinutes = totalMinutes - extraMinutes;
            BigDecimal totalHours = BigDecimal.valueOf(totalMinutes)
                    .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
            BigDecimal visitorFee = BigDecimal.valueOf(extraMinutes)
                    .multiply(hourFee)
                    .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);

            upsertVisitorUsageMonthly(
                    complexId, householdId, billYear, billMonth,
                    totalMinutes, totalHours, freeMinutes, extraMinutes, visitorFee, publishedAt
            );

            payloadItems.add(VisitorFeeCalculatedEventPayload.Item.builder()
                    .householdId(householdId)
                    .totalMinutes(totalMinutes)
                    .totalHours(totalHours)
                    .freeMinutes(freeMinutes)
                    .extraMinutes(extraMinutes)
                    .visitorFee(visitorFee)
                    .build());
            responseItems.add(VisitorFeeCalculateRes.Item.builder()
                    .householdId(householdId)
                    .totalMinutes(totalMinutes)
                    .totalHours(totalHours)
                    .freeMinutes(freeMinutes)
                    .extraMinutes(extraMinutes)
                    .visitorFee(visitorFee)
                    .build());
        }

        // outbox 발행 — 같은 트랜잭션 안이라 직렬화/저장 실패 시 함께 롤백
        VisitorFeeCalculatedEventPayload payload = VisitorFeeCalculatedEventPayload.builder()
                .complexId(complexId)
                .billYear(billYear)
                .billMonth(billMonth)
                .items(payloadItems)
                .occurredAt(publishedAt)
                .build();
        parkingVehicleOutboxService.saveVisitorFeeCalculatedEvent(complexId, payload);

        return VisitorFeeCalculateRes.builder()
                .complexId(complexId)
                .billYear(billYear)
                .billMonth(billMonth)
                .items(responseItems)
                .published(true)
                .executedAt(publishedAt)
                .build();
    }

    // 입주민 본인 세대 차량 월 과금 단건 조회
    @Transactional(readOnly = true)
    public VehicleFeeMonthlyGetRes getResidentVehicleMonthlyFee(
            String yearMonthStr, Long userId, String userRole, Long complexId
    ) {
        // 입주민 헤더 컨텍스트 검증
        RoleContextValidator.validateResidentContext(userId, userRole, complexId);

        // yearMonth 파싱 (YYYY-MM)
        YearMonth yearMonth = parseYearMonth(yearMonthStr);

        // 세대주 기준 세대 역추적 — 세대원은 차단
        HouseholdCache household = householdCacheRepository.findByHeadUserId(userId)
                .orElseThrow(() -> new BusinessException(ParkingVehicleErrorCode.HOUSEHOLD_NOT_FOUND));

        // 세대-단지 정합성 검증
        if (!household.getComplexId().equals(complexId)) {
            throw new BusinessException(CommonErrorCode.FORBIDDEN);
        }

        // 산정 결과 조회 — 없으면 404 (산정 안 됨과 0원 산정은 구분)
        VehicleFeeMonthly entity = vehicleFeeMonthlyRepository
                .findByHouseholdIdAndBillYearAndBillMonth(
                        household.getId(), yearMonth.getYear(), yearMonth.getMonthValue()
                )
                .orElseThrow(() -> new BusinessException(ParkingVehicleErrorCode.VEHICLE_FEE_MONTHLY_NOT_FOUND));

        return VehicleFeeMonthlyGetRes.builder()
                .complexId(entity.getComplexId())
                .householdId(entity.getHouseholdId())
                .billYear(entity.getBillYear())
                .billMonth(entity.getBillMonth())
                .approvedVehicleCount(entity.getApprovedVehicleCount())
                .vehicleFee(entity.getVehicleFee())
                .publishedAt(entity.getPublishedAt())
                .build();
    }

    // 관리자 단지 차량 월 과금 페이지 조회
    @Transactional(readOnly = true)
    public PageResponse<AdminVehicleFeeMonthlyListRes> listAdminVehicleMonthlyFees(
            String yearMonthStr, int page, int size,
            String userRole, Long complexId, Long selectedComplexId
    ) {
        // 관리자 컨텍스트 해석 (MASTER는 selectedComplexId, 일반 관리자는 X-COMPLEX-ID)
        Long targetComplexId = RoleContextValidator
                .resolveAdminContextComplexId(userRole, complexId, selectedComplexId);

        // yearMonth 파싱과 페이징 파라미터 정규화
        YearMonth yearMonth = parseYearMonth(yearMonthStr);
        int normalizedPage = Math.max(page, 0);
        int normalizedSize = size > 0 ? size : 20;

        // 단지+연월 페이지 조회
        Page<VehicleFeeMonthly> resultPage = vehicleFeeMonthlyRepository
                .findByComplexIdAndBillYearAndBillMonth(
                        targetComplexId, yearMonth.getYear(), yearMonth.getMonthValue(),
                        PageRequest.of(normalizedPage, normalizedSize)
                );

        if (resultPage.isEmpty()) {
            return PageResponse.empty(normalizedPage, normalizedSize);
        }

        // 동/호 매핑용 세대 캐시 일괄 조회
        List<Long> householdIds = resultPage.getContent().stream()
                .map(VehicleFeeMonthly::getHouseholdId)
                .toList();
        Map<Long, HouseholdCache> householdMap = householdCacheRepository.findAllById(householdIds).stream()
                .collect(Collectors.toMap(HouseholdCache::getId, h -> h));

        // 응답 항목 빌드 — 세대 캐시에서 building/unit 채움
        List<AdminVehicleFeeMonthlyListRes> content = resultPage.getContent().stream()
                .map(entity -> {
                    HouseholdCache household = householdMap.get(entity.getHouseholdId());
                    return AdminVehicleFeeMonthlyListRes.builder()
                            .complexId(entity.getComplexId())
                            .householdId(entity.getHouseholdId())
                            .building(household != null ? household.getBuilding() : null)
                            .unit(household != null ? household.getUnit() : null)
                            .billYear(entity.getBillYear())
                            .billMonth(entity.getBillMonth())
                            .approvedVehicleCount(entity.getApprovedVehicleCount())
                            .vehicleFee(entity.getVehicleFee())
                            .publishedAt(entity.getPublishedAt())
                            .build();
                })
                .toList();

        return PageResponse.<AdminVehicleFeeMonthlyListRes>builder()
                .content(content)
                .page(resultPage.getNumber())
                .size(resultPage.getSize())
                .totalElements(resultPage.getTotalElements())
                .totalPages(resultPage.getTotalPages())
                .hasNext(resultPage.hasNext())
                .build();
    }

    // 입주민 본인 세대 방문차량 월 과금 단건 조회
    @Transactional(readOnly = true)
    public VisitorUsageMonthlyGetRes getResidentVisitorMonthlyFee(
            String yearMonthStr, Long userId, String userRole, Long complexId
    ) {
        // 입주민 헤더 컨텍스트 검증
        RoleContextValidator.validateResidentContext(userId, userRole, complexId);

        // yearMonth 파싱 (YYYY-MM)
        YearMonth yearMonth = parseYearMonth(yearMonthStr);

        // 세대주 기준 세대 역추적 — 세대원은 차단
        HouseholdCache household = householdCacheRepository.findByHeadUserId(userId)
                .orElseThrow(() -> new BusinessException(ParkingVehicleErrorCode.HOUSEHOLD_NOT_FOUND));

        // 세대-단지 정합성 검증 — 헤더 단지와 세대 단지가 다르면 권한 없음
        if (!household.getComplexId().equals(complexId)) {
            throw new BusinessException(CommonErrorCode.FORBIDDEN);
        }

        // 산정 결과 조회 — 없으면 404 (산정 안 됨과 0원 산정은 구분)
        VisitorUsageMonthly entity = visitorUsageMonthlyRepository
                .findByHouseholdIdAndUsageYearAndUsageMonth(
                        household.getId(), yearMonth.getYear(), yearMonth.getMonthValue()
                )
                .orElseThrow(() -> new BusinessException(ParkingVehicleErrorCode.VISITOR_USAGE_MONTHLY_NOT_FOUND));

        return VisitorUsageMonthlyGetRes.builder()
                .complexId(entity.getComplexId())
                .householdId(entity.getHouseholdId())
                .usageYear(entity.getUsageYear())
                .usageMonth(entity.getUsageMonth())
                .totalMinutes(entity.getTotalMinutes())
                .totalHours(entity.getTotalHours())
                .freeMinutes(entity.getFreeMinutes())
                .extraMinutes(entity.getExtraMinutes())
                .visitorFee(entity.getVisitorFee())
                .publishedAt(entity.getPublishedAt())
                .build();
    }

    // 관리자 단지 방문차량 월 과금 페이지 조회
    @Transactional(readOnly = true)
    public PageResponse<AdminVisitorUsageMonthlyListRes> listAdminVisitorMonthlyFees(
            String yearMonthStr, int page, int size,
            String userRole, Long complexId, Long selectedComplexId
    ) {
        // 관리자 컨텍스트 해석 (MASTER는 selectedComplexId, 일반 관리자는 X-COMPLEX-ID)
        Long targetComplexId = RoleContextValidator
                .resolveAdminContextComplexId(userRole, complexId, selectedComplexId);

        // yearMonth 파싱과 페이징 파라미터 정규화
        YearMonth yearMonth = parseYearMonth(yearMonthStr);
        int normalizedPage = Math.max(page, 0);
        int normalizedSize = size > 0 ? size : 20;

        // 단지+연월 페이지 조회
        Page<VisitorUsageMonthly> resultPage = visitorUsageMonthlyRepository
                .findByComplexIdAndUsageYearAndUsageMonth(
                        targetComplexId, yearMonth.getYear(), yearMonth.getMonthValue(),
                        PageRequest.of(normalizedPage, normalizedSize)
                );

        // 빈 페이지 빠른 응답
        if (resultPage.isEmpty()) {
            return PageResponse.empty(normalizedPage, normalizedSize);
        }

        // 동/호 매핑용 세대 캐시 일괄 조회
        List<Long> householdIds = resultPage.getContent().stream()
                .map(VisitorUsageMonthly::getHouseholdId)
                .toList();
        Map<Long, HouseholdCache> householdMap = householdCacheRepository.findAllById(householdIds).stream()
                .collect(Collectors.toMap(HouseholdCache::getId, h -> h));

        // 응답 항목 빌드 — 세대 캐시에서 building/unit 채움
        List<AdminVisitorUsageMonthlyListRes> content = resultPage.getContent().stream()
                .map(entity -> {
                    HouseholdCache household = householdMap.get(entity.getHouseholdId());
                    return AdminVisitorUsageMonthlyListRes.builder()
                            .complexId(entity.getComplexId())
                            .householdId(entity.getHouseholdId())
                            .building(household != null ? household.getBuilding() : null)
                            .unit(household != null ? household.getUnit() : null)
                            .usageYear(entity.getUsageYear())
                            .usageMonth(entity.getUsageMonth())
                            .totalMinutes(entity.getTotalMinutes())
                            .totalHours(entity.getTotalHours())
                            .freeMinutes(entity.getFreeMinutes())
                            .extraMinutes(entity.getExtraMinutes())
                            .visitorFee(entity.getVisitorFee())
                            .publishedAt(entity.getPublishedAt())
                            .build();
                })
                .toList();

        return PageResponse.<AdminVisitorUsageMonthlyListRes>builder()
                .content(content)
                .page(resultPage.getNumber())
                .size(resultPage.getSize())
                .totalElements(resultPage.getTotalElements())
                .totalPages(resultPage.getTotalPages())
                .hasNext(resultPage.hasNext())
                .build();
    }

    // 차량 정책 단가표(carCount 오름차순)에서 보유 대수 이하 중 carCount 최댓값 row의 monthlyFee를 반환한다.
    private BigDecimal matchVehicleFee(List<VehiclePolicy> sortedPolicies, int approvedCount, Long householdId) {
        BigDecimal matched = null;
        for (VehiclePolicy policy : sortedPolicies) {
            if (policy.getCarCount() <= approvedCount) {
                matched = policy.getMonthlyFee();
            } else {
                break;
            }
        }
        // 정책 최소 carCount보다 보유 대수가 작은 비정상 케이스 방어 — 0원 처리 + 로그
        if (matched == null) {
            log.warn("[vehicle-fee] no matching policy for approvedCount={}, householdId={}",
                    approvedCount, householdId);
            return BigDecimal.ZERO;
        }
        return matched;
    }

    // 세대 월 차량 비용 row를 upsert한다. 기존이면 apply, 없으면 신규 생성 후 save.
    private void upsertVehicleFeeMonthly(
            Long complexId, Long householdId, int billYear, int billMonth,
            int approvedVehicleCount, BigDecimal vehicleFee, LocalDateTime publishedAt
    ) {
        VehicleFeeMonthly entity = vehicleFeeMonthlyRepository
                .findByHouseholdIdAndBillYearAndBillMonth(householdId, billYear, billMonth)
                .orElse(null);
        if (entity != null) {
            entity.apply(approvedVehicleCount, vehicleFee, true, publishedAt);
            return;
        }
        VehicleFeeMonthly created = VehicleFeeMonthly.builder()
                .complexId(complexId)
                .householdId(householdId)
                .billYear(billYear)
                .billMonth(billMonth)
                .approvedVehicleCount(approvedVehicleCount)
                .vehicleFee(vehicleFee)
                .isPublished(true)
                .publishedAt(publishedAt)
                .build();
        vehicleFeeMonthlyRepository.save(created);
    }

    // 입출차 로그를 visitor_vehicle_id 또는 regular_visitor_vehicle_id 단위로 묶어 세대별 체류 분 합계를 산출한다.
    private Map<Long, Integer> aggregateHouseholdMinutes(List<ParkingLog> logs) {
        // 그룹 키별 분리 — 입력 정렬이 license_plate 기준이라 같은 키 인접 보장 안 됨, LinkedHashMap으로 모은다.
        Map<String, List<ParkingLog>> grouped = new LinkedHashMap<>();
        for (ParkingLog plog : logs) {
            String key = buildGroupKey(plog);
            if (key == null) {
                continue;
            }
            grouped.computeIfAbsent(key, k -> new ArrayList<>()).add(plog);
        }

        // 그룹별 IN/OUT 짝짓기로 세대별 분 누적
        Map<Long, Integer> householdMinutes = new HashMap<>();
        for (Map.Entry<String, List<ParkingLog>> entry : grouped.entrySet()) {
            pairAndAccumulate(entry.getKey(), entry.getValue(), householdMinutes);
        }
        return householdMinutes;
    }

    // 방문차량/고정 방문차량 FK 기준 그룹 키 생성. 둘 다 NULL이면 산정 대상 아님.
    private String buildGroupKey(ParkingLog plog) {
        if (plog.getVisitorVehicleId() != null) {
            return "V:" + plog.getVisitorVehicleId();
        }
        if (plog.getRegularVisitorVehicleId() != null) {
            return "R:" + plog.getRegularVisitorVehicleId();
        }
        return null;
    }

    // 한 그룹 안에서 loggedAt ASC로 IN→OUT 짝짓기 후 (out-in) 분을 세대별 누적 맵에 더한다.
    private void pairAndAccumulate(String groupKey, List<ParkingLog> groupLogs, Map<Long, Integer> householdMinutes) {
        // 안전 정렬 — 그룹 키가 다른 동일 번호판이 섞여 들어와도 시간순 보장
        groupLogs.sort((a, b) -> a.getLoggedAt().compareTo(b.getLoggedAt()));

        ParkingLog openIn = null;
        for (ParkingLog plog : groupLogs) {
            if (plog.getEntryType() == ParkingEntryType.IN) {
                if (openIn != null) {
                    // 연속 IN — 이전 IN은 출차 누락으로 보고 0분 처리하고 갱신
                    log.warn("[visitor-fee] consecutive IN without OUT, previous IN treated as 0 minutes. groupKey={}, prevLoggedAt={}",
                            groupKey, openIn.getLoggedAt());
                }
                openIn = plog;
                continue;
            }
            if (plog.getEntryType() == ParkingEntryType.OUT) {
                if (openIn == null) {
                    // 짝지을 IN 없는 OUT — 비정상으로 보고 스킵
                    log.warn("[visitor-fee] OUT without prior IN, skipped. groupKey={}, loggedAt={}",
                            groupKey, plog.getLoggedAt());
                    continue;
                }
                long minutes = Math.max(0, Duration.between(openIn.getLoggedAt(), plog.getLoggedAt()).toMinutes());
                Long householdId = openIn.getHouseholdId();
                if (householdId != null) {
                    householdMinutes.merge(householdId, (int) minutes, Integer::sum);
                } else {
                    // 방문차량 FK는 채워졌는데 householdId가 NULL인 데이터 정합성 이상 — 조용히 누락되지 않게 발견
                    log.warn("[visitor-fee] householdId null on visitor log, skipped. groupKey={}, loggedAt={}",
                            groupKey, openIn.getLoggedAt());
                }
                openIn = null;
            }
        }
        // 그룹 끝에 열린 IN 남으면 미출차 EXPIRED 정책에 따라 0분 처리
        if (openIn != null) {
            log.debug("[visitor-fee] IN-only log, treated as 0 minutes. groupKey={}, loggedAt={}",
                    groupKey, openIn.getLoggedAt());
        }
    }

    // 세대 월 방문차량 집계 row를 upsert한다. 기존이면 apply, 없으면 신규 생성 후 save.
    private void upsertVisitorUsageMonthly(
            Long complexId, Long householdId, int billYear, int billMonth,
            int totalMinutes, BigDecimal totalHours, int freeMinutes, int extraMinutes,
            BigDecimal visitorFee, LocalDateTime publishedAt
    ) {
        VisitorUsageMonthly entity = visitorUsageMonthlyRepository
                .findByHouseholdIdAndUsageYearAndUsageMonth(householdId, billYear, billMonth)
                .orElse(null);
        if (entity != null) {
            entity.apply(totalMinutes, totalHours, freeMinutes, extraMinutes, visitorFee, true, publishedAt);
            return;
        }
        VisitorUsageMonthly created = VisitorUsageMonthly.builder()
                .complexId(complexId)
                .householdId(householdId)
                .usageYear(billYear)
                .usageMonth(billMonth)
                .totalMinutes(totalMinutes)
                .totalHours(totalHours)
                .freeMinutes(freeMinutes)
                .extraMinutes(extraMinutes)
                .visitorFee(visitorFee)
                .isPublished(true)
                .publishedAt(publishedAt)
                .build();
        visitorUsageMonthlyRepository.save(created);
    }

    // yearMonth 문자열(YYYY-MM)을 YearMonth로 파싱. 형식 오류 시 INVALID_PARAMETER.
    private YearMonth parseYearMonth(String yearMonthStr) {
        if (yearMonthStr == null || yearMonthStr.isBlank()) {
            throw new BusinessException(ParkingVehicleErrorCode.INVALID_PARAMETER);
        }
        try {
            return YearMonth.parse(yearMonthStr);
        } catch (DateTimeParseException ex) {
            throw new BusinessException(ParkingVehicleErrorCode.INVALID_PARAMETER);
        }
    }
}
