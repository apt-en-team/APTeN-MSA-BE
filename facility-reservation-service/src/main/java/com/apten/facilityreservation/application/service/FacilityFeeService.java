package com.apten.facilityreservation.application.service;

import com.apten.common.exception.BusinessException;
import com.apten.facilityreservation.application.model.request.FacilityFeeCalculateReq;
import com.apten.facilityreservation.application.model.request.FacilityFeePublishReq;
import com.apten.facilityreservation.application.model.response.FacilityFeeCalculateRes;
import com.apten.facilityreservation.application.model.response.FacilityFeePublishRes;
import com.apten.facilityreservation.domain.entity.FacilityPolicy;
import com.apten.facilityreservation.domain.entity.FacilitySubscription;
import com.apten.facilityreservation.domain.entity.FacilityUsageMonthly;
import com.apten.facilityreservation.domain.entity.GxProgram;
import com.apten.facilityreservation.domain.entity.GxReservation;
import com.apten.facilityreservation.domain.entity.Reservation;
import com.apten.facilityreservation.domain.enums.FacilityFeeType;
import com.apten.facilityreservation.domain.enums.GxReservationStatus;
import com.apten.facilityreservation.domain.enums.ReservationStatus;
import com.apten.facilityreservation.domain.repository.FacilityPolicyRepository;
import com.apten.facilityreservation.domain.repository.FacilitySubscriptionRepository;
import com.apten.facilityreservation.domain.repository.FacilityUsageMonthlyRepository;
import com.apten.facilityreservation.domain.repository.GxProgramRepository;
import com.apten.facilityreservation.domain.repository.GxReservationRepository;
import com.apten.facilityreservation.domain.repository.ReservationRepository;
import com.apten.facilityreservation.exception.FacilityReservationErrorCode;
import com.apten.facilityreservation.infrastructure.kafka.FacilityReservationOutboxService;
import com.apten.facilityreservation.infrastructure.kafka.payload.FacilityFeeCalculatedEventPayload;
import java.math.BigDecimal;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 시설 이용 비용 산정과 발행 API 시그니처를 담당하는 서비스이다.
@Service
@RequiredArgsConstructor
public class FacilityFeeService {

    private final ReservationRepository reservationRepository;
    private final FacilityPolicyRepository facilityPolicyRepository;
    private final FacilitySubscriptionRepository facilitySubscriptionRepository;
    private final GxReservationRepository gxReservationRepository;
    private final GxProgramRepository gxProgramRepository;
    private final FacilityUsageMonthlyRepository facilityUsageMonthlyRepository;
    // outbox는 property 활성화 시에만 bean이 존재한다.
    private final Optional<FacilityReservationOutboxService> outboxService;

    // 시설 이용 비용을 산정한다.
    @Transactional
    public FacilityFeeCalculateRes calculateFacilityFees(FacilityFeeCalculateReq req) {
        YearMonth yearMonth = resolveYearMonth(req);
        LocalDate fromDate = yearMonth.atDay(1);
        LocalDate toDate = yearMonth.atEndOfMonth();

        Map<Long, HouseholdFeeAggregate> aggregateMap = new LinkedHashMap<>();

        // FLAT/PER_PERSON: 구독 기반 월 청구 — 구독 레코드가 청구 기준이며 billingCutoffDay 규칙을 적용한다.
        // PER_USE: 완료 예약 건수 기반 청구 — 예약 건별로 baseFee를 곱한다.
        List<Reservation> completedReservations = reservationRepository.findByStatusAndReservationDateBetween(
                ReservationStatus.COMPLETED,
                fromDate,
                toDate
        );

        // PER_USE 전용: (complexId, facilityId) 기준 완료 예약 그룹
        Map<Long, Map<Long, List<Reservation>>> perUseByComplexAndFacility = completedReservations.stream()
                .filter(r -> r.getHouseholdId() != null)
                .collect(Collectors.groupingBy(Reservation::getComplexId,
                        Collectors.groupingBy(Reservation::getFacilityId)));

        perUseByComplexAndFacility.forEach((complexId, byFacility) -> {
            List<Long> facilityIds = new ArrayList<>(byFacility.keySet());
            Map<Long, FacilityPolicy> policyByFacilityId = facilityPolicyRepository
                    .findByComplexIdAndFacilityIdInAndIsActiveTrue(complexId, facilityIds)
                    .stream()
                    .collect(Collectors.toMap(FacilityPolicy::getFacilityId, p -> p));

            byFacility.forEach((facilityId, reservations) -> {
                FacilityPolicy policy = policyByFacilityId.get(facilityId);
                if (policy == null) {
                    return;
                }
                FacilityFeeType feeType = policy.getFeeType() != null ? policy.getFeeType() : FacilityFeeType.FLAT;
                if (feeType != FacilityFeeType.PER_USE) {
                    return;
                }
                // PER_USE: 세대별로 완료 예약 건수 × baseFee를 청구한다.
                reservations.stream()
                        .collect(Collectors.groupingBy(Reservation::getHouseholdId))
                        .forEach((householdId, group) -> {
                            BigDecimal fee = nullSafe(policy.getBaseFee())
                                    .multiply(BigDecimal.valueOf(group.size()));
                            addFee(aggregateMap, complexId, householdId, fee);
                        });
            });
        });

        // FLAT/PER_PERSON: 단지별 구독 레코드에서 당월 청구 대상을 조회한다.
        // 모든 단지 ID를 PER_USE 처리 결과 + 구독에서 수집한다.
        Set<Long> allComplexIds = completedReservations.stream()
                .map(Reservation::getComplexId)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        for (Long complexId : allComplexIds) {
            List<FacilitySubscription> billableSubscriptions = facilitySubscriptionRepository
                    .findBillableForMonth(complexId, fromDate, toDate);
            if (billableSubscriptions.isEmpty()) {
                continue;
            }

            List<Long> facilityIds = billableSubscriptions.stream()
                    .map(FacilitySubscription::getFacilityId)
                    .distinct()
                    .toList();
            Map<Long, FacilityPolicy> policyByFacilityId = facilityPolicyRepository
                    .findByComplexIdAndFacilityIdInAndIsActiveTrue(complexId, facilityIds)
                    .stream()
                    .collect(Collectors.toMap(FacilityPolicy::getFacilityId, p -> p));

            for (FacilitySubscription subscription : billableSubscriptions) {
                FacilityPolicy policy = policyByFacilityId.get(subscription.getFacilityId());
                if (policy == null) {
                    continue;
                }
                FacilityFeeType feeType = policy.getFeeType() != null ? policy.getFeeType() : FacilityFeeType.FLAT;
                if (feeType != FacilityFeeType.FLAT && feeType != FacilityFeeType.PER_PERSON) {
                    continue;
                }
                // billingCutoffDay 규칙: 기준일 이후 신청/해지는 당월 미반영
                if (!isBillableInMonth(subscription, yearMonth, policy.getBillingCutoffDay())) {
                    continue;
                }
                BigDecimal fee = calcSubscriptionFee(policy, feeType);
                addFee(aggregateMap, complexId, subscription.getHouseholdId(), fee);
            }
        }

        // GX는 프로그램 시작일 기준으로 당월 시작 프로그램의 확정 신청 1건을 월 1회 비용으로 본다.
        List<GxProgram> monthlyGxPrograms = gxProgramRepository.findByStartDateBetween(fromDate, toDate);
        if (!monthlyGxPrograms.isEmpty()) {
            List<Long> gxProgramIds = monthlyGxPrograms.stream().map(GxProgram::getId).toList();
            Map<Long, GxProgram> programById = monthlyGxPrograms.stream()
                    .collect(Collectors.toMap(GxProgram::getId, p -> p));

            List<GxReservation> confirmedGxReservations = gxReservationRepository
                    .findByStatusAndProgramIdIn(GxReservationStatus.CONFIRMED, gxProgramIds);

            Map<Long, List<GxReservation>> gxReservationsByComplex = confirmedGxReservations.stream()
                    .filter(r -> r.getHouseholdId() != null)
                    .collect(Collectors.groupingBy(GxReservation::getComplexId));

            gxReservationsByComplex.forEach((complexId, reservations) -> {
                // (세대ID, 프로그램ID) 기준으로 중복 없이 청구한다.
                Set<HouseholdProgramKey> uniqueProgramKeys = reservations.stream()
                        .map(r -> new HouseholdProgramKey(r.getHouseholdId(), r.getProgramId()))
                        .collect(Collectors.toCollection(LinkedHashSet::new));

                for (HouseholdProgramKey key : uniqueProgramKeys) {
                    GxProgram program = programById.get(key.programId());
                    if (program == null || !Objects.equals(program.getComplexId(), complexId)) {
                        continue;
                    }
                    addFee(aggregateMap, complexId, key.householdId(), nullSafe(program.getBaseFee()));
                }
            });
        }

        if (aggregateMap.isEmpty()) {
            return FacilityFeeCalculateRes.builder()
                    .usageYear(req.getUsageYear())
                    .usageMonth(req.getUsageMonth())
                    .processedCount(0)
                    .calculatedAt(LocalDateTime.now())
                    .build();
        }

        List<Long> householdIds = aggregateMap.keySet().stream().toList();
        Map<Long, FacilityUsageMonthly> existingByHouseholdId = facilityUsageMonthlyRepository
                .findByHouseholdIdInAndUsageYearAndUsageMonth(householdIds, yearMonth.getYear(), yearMonth.getMonthValue())
                .stream()
                .collect(Collectors.toMap(FacilityUsageMonthly::getHouseholdId, usage -> usage));

        int processedCount = 0;
        for (Map.Entry<Long, HouseholdFeeAggregate> entry : aggregateMap.entrySet()) {
            Long householdId = entry.getKey();
            HouseholdFeeAggregate aggregate = entry.getValue();
            FacilityUsageMonthly existing = existingByHouseholdId.get(householdId);

            if (existing != null) {
                // 발행된 월 비용은 Household Service 기준 데이터가 되었으므로 산정 배치가 덮어쓰지 않는다.
                if (Boolean.TRUE.equals(existing.getIsPublished())) {
                    continue;
                }
                existing.changeFacilityFee(aggregate.facilityFee());
                processedCount++;
                continue;
            }

            facilityUsageMonthlyRepository.save(FacilityUsageMonthly.builder()
                    .complexId(aggregate.complexId())
                    .householdId(householdId)
                    .usageYear(yearMonth.getYear())
                    .usageMonth(yearMonth.getMonthValue())
                    .facilityFee(aggregate.facilityFee())
                    .isPublished(false)
                    .build());
            processedCount++;
        }

        return FacilityFeeCalculateRes.builder()
                .usageYear(req.getUsageYear())
                .usageMonth(req.getUsageMonth())
                .processedCount(processedCount)
                .calculatedAt(LocalDateTime.now())
                .build();
    }

    // 시설 이용 비용을 Household Service로 발행한다.
    @Transactional
    public FacilityFeePublishRes publishFacilityFees(FacilityFeePublishReq req) {
        YearMonth yearMonth = resolveYearMonth(req.getUsageYear(), req.getUsageMonth());
        List<FacilityUsageMonthly> publishTargets = facilityUsageMonthlyRepository
                .findByUsageYearAndUsageMonthAndIsPublishedFalse(yearMonth.getYear(), yearMonth.getMonthValue());

        // 이미 발행된 row를 다시 잡으면 Household Service 기준 데이터가 중복 처리될 수 있다.
        publishTargets.forEach(FacilityUsageMonthly::markPublished);

        // 단지별로 그룹화 후 outbox 이벤트를 저장한다.
        if (!publishTargets.isEmpty()) {
            Map<Long, List<FacilityUsageMonthly>> byComplex = publishTargets.stream()
                    .collect(Collectors.groupingBy(FacilityUsageMonthly::getComplexId));

            LocalDateTime occurredAt = LocalDateTime.now();
            byComplex.forEach((complexId, usages) -> {
                List<FacilityFeeCalculatedEventPayload.Item> items = usages.stream()
                        .map(u -> FacilityFeeCalculatedEventPayload.Item.builder()
                                .householdId(u.getHouseholdId())
                                .facilityFee(u.getFacilityFee())
                                .build())
                        .toList();
                FacilityFeeCalculatedEventPayload payload = FacilityFeeCalculatedEventPayload.builder()
                        .complexId(complexId)
                        .usageYear(yearMonth.getYear())
                        .usageMonth(yearMonth.getMonthValue())
                        .items(items)
                        .occurredAt(occurredAt)
                        .build();
                // outbox bean이 활성화된 경우에만 이벤트를 저장한다.
                outboxService.ifPresent(service -> service.saveFacilityFeeCalculatedEvent(payload));
            });
        }

        return FacilityFeePublishRes.builder()
                .usageYear(req.getUsageYear())
                .usageMonth(req.getUsageMonth())
                .publishedCount(publishTargets.size())
                .published(!publishTargets.isEmpty())
                .publishedAt(LocalDateTime.now())
                .build();
    }

    private YearMonth resolveYearMonth(FacilityFeeCalculateReq req) {
        if (req == null) {
            throw new BusinessException(FacilityReservationErrorCode.INVALID_PARAMETER);
        }

        return resolveYearMonth(req.getUsageYear(), req.getUsageMonth());
    }

    private YearMonth resolveYearMonth(Integer usageYear, Integer usageMonth) {
        if (usageYear == null || usageMonth == null) {
            throw new BusinessException(FacilityReservationErrorCode.INVALID_PARAMETER);
        }

        try {
            return YearMonth.of(usageYear, usageMonth);
        } catch (DateTimeException e) {
            throw new BusinessException(FacilityReservationErrorCode.INVALID_PARAMETER);
        }
    }

    private void addFee(Map<Long, HouseholdFeeAggregate> aggregateMap, Long complexId, Long householdId, BigDecimal fee) {
        aggregateMap.compute(householdId, (key, existing) -> {
            if (existing == null) {
                return new HouseholdFeeAggregate(complexId, fee);
            }
            return new HouseholdFeeAggregate(existing.complexId(), existing.facilityFee().add(fee));
        });
    }

    // 구독 기반 FLAT/PER_PERSON 월 청구 금액을 계산한다.
    // PER_PERSON은 구독 1건당 1인으로 계산한다 (구독 레코드 자체가 1세대 1시설 단위).
    private BigDecimal calcSubscriptionFee(FacilityPolicy policy, FacilityFeeType feeType) {
        BigDecimal baseFee = nullSafe(policy.getBaseFee());
        if (feeType == FacilityFeeType.PER_PERSON) {
            return baseFee;
        }
        // FLAT: baseFee + 초과 인원 요금 옵션 (includedPersonCount=1로 가정)
        return baseFee;
    }

    // billingCutoffDay 규칙에 따라 구독이 당월 청구 대상인지 판별한다.
    // cutoffDay가 null이면 항상 청구한다.
    // 신청: subscribedAt의 일자가 cutoffDay 초과이면 당월 제외 (익월부터 청구)
    // 해지: cancelledAt의 일자가 cutoffDay 초과이면 당월 포함 (익월부터 미청구)
    private boolean isBillableInMonth(FacilitySubscription subscription, YearMonth yearMonth, Integer cutoffDay) {
        if (cutoffDay == null) {
            return true;
        }
        int maxDay = Math.min(cutoffDay, yearMonth.lengthOfMonth());

        // 신청일이 이 달인 경우: cutoffDay 초과 신청은 당월 제외
        LocalDate subscribeMonth = YearMonth.from(subscription.getSubscribedAt()).atDay(1);
        if (subscribeMonth.equals(yearMonth.atDay(1))) {
            if (subscription.getSubscribedAt().getDayOfMonth() > maxDay) {
                return false;
            }
        }

        // 해지일이 이 달 이전인 경우: cutoffDay 이전 해지는 당월 제외
        if (subscription.getCancelledAt() != null) {
            LocalDate cancelMonth = YearMonth.from(subscription.getCancelledAt()).atDay(1);
            if (cancelMonth.isBefore(yearMonth.atDay(1))) {
                return false;
            }
            // 해지일이 이 달인 경우: cutoffDay 이전 해지는 당월 제외
            if (cancelMonth.equals(yearMonth.atDay(1))) {
                if (subscription.getCancelledAt().getDayOfMonth() <= maxDay) {
                    return false;
                }
            }
        }

        return true;
    }

    // feeType에 따라 세대-시설 단위 청구 금액을 계산한다. (PER_USE 전용으로 유지)
    private BigDecimal calcFee(FacilityPolicy policy, List<Reservation> group) {
        BigDecimal baseFee = nullSafe(policy.getBaseFee());
        FacilityFeeType type = policy.getFeeType() != null ? policy.getFeeType() : FacilityFeeType.FLAT;

        if (type == FacilityFeeType.PER_USE) {
            return baseFee.multiply(BigDecimal.valueOf(group.size()));
        }
        if (type == FacilityFeeType.PER_PERSON) {
            long persons = group.stream().map(Reservation::getUserId).filter(Objects::nonNull).distinct().count();
            return baseFee.multiply(BigDecimal.valueOf(persons));
        }
        long distinctPersons = group.stream().map(Reservation::getUserId).filter(Objects::nonNull).distinct().count();
        int included = policy.getIncludedPersonCount() != null ? policy.getIncludedPersonCount() : Integer.MAX_VALUE;
        long extraPersons = Math.max(0, distinctPersons - included);
        BigDecimal extraFee = nullSafe(policy.getExtraPersonFee()).multiply(BigDecimal.valueOf(extraPersons));
        return baseFee.add(extraFee);
    }

    private BigDecimal nullSafe(BigDecimal fee) {
        return fee == null ? BigDecimal.ZERO : fee;
    }

    private record HouseholdFacilityKey(Long householdId, Long facilityId) {
    }

    private record HouseholdProgramKey(Long householdId, Long programId) {
    }

    private record HouseholdFeeAggregate(Long complexId, BigDecimal facilityFee) {
    }
}
