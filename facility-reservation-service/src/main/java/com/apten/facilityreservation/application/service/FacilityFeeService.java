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
import com.apten.facilityreservation.domain.enums.FacilitySubscriptionStatus;
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
import com.apten.common.kafka.payload.FacilityFeeCalculatedEventPayload;
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

// 시설 이용 비용 산정/발행
@Service
@RequiredArgsConstructor
public class FacilityFeeService {

    private final ReservationRepository reservationRepository;
    private final FacilityPolicyRepository facilityPolicyRepository;
    private final FacilitySubscriptionRepository facilitySubscriptionRepository;
    private final GxReservationRepository gxReservationRepository;
    private final GxProgramRepository gxProgramRepository;
    private final FacilityUsageMonthlyRepository facilityUsageMonthlyRepository;
    // 선택형 Outbox Bean
    private final Optional<FacilityReservationOutboxService> outboxService;

    // 시설 이용 비용 산정
    @Transactional
    public FacilityFeeCalculateRes calculateFacilityFees(FacilityFeeCalculateReq req) {
        YearMonth yearMonth = resolveYearMonth(req);
        LocalDate fromDate = yearMonth.atDay(1);
        LocalDate toDate = yearMonth.atEndOfMonth();

        Map<Long, HouseholdFeeAggregate> aggregateMap = new LinkedHashMap<>();

        // 요금 유형별 월 청구 분기
        List<Reservation> completedReservations = reservationRepository.findByStatusAndReservationDateBetween(
                ReservationStatus.COMPLETED,
                fromDate,
                toDate
        );

        // 건별 요금 완료 예약 그룹화
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
                // 건별 요금 계산
                reservations.stream()
                        .collect(Collectors.groupingBy(Reservation::getHouseholdId))
                        .forEach((householdId, group) -> {
                            BigDecimal fee = nullSafe(policy.getBaseFee())
                                    .multiply(BigDecimal.valueOf(group.size()));
                            addFee(aggregateMap, complexId, householdId, fee);
                        });
            });
        });

        // 구독형 요금 대상 조회
        Set<Long> allComplexIds = completedReservations.stream()
                .map(Reservation::getComplexId)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        for (Long complexId : allComplexIds) {
            List<FacilitySubscription> billableSubscriptions = facilitySubscriptionRepository
                    .findBillableForMonth(complexId, fromDate, toDate, FacilitySubscriptionStatus.ACTIVE);
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

            // 세대+시설 단위로 그룹핑 → 실제 구독자 수 기준 요금 계산
            Map<Long, Map<Long, List<FacilitySubscription>>> byHouseholdAndFacility = billableSubscriptions.stream()
                    .collect(Collectors.groupingBy(FacilitySubscription::getHouseholdId,
                            Collectors.groupingBy(FacilitySubscription::getFacilityId)));

            byHouseholdAndFacility.forEach((householdId, byFacility) ->
                byFacility.forEach((facilityId, subs) -> {
                    FacilityPolicy policy = policyByFacilityId.get(facilityId);
                    if (policy == null) return;
                    FacilityFeeType feeType = policy.getFeeType() != null ? policy.getFeeType() : FacilityFeeType.FLAT;
                    if (feeType != FacilityFeeType.FLAT && feeType != FacilityFeeType.PER_PERSON) return;
                    // 기준일 적용: 한 명이라도 청구 대상이면 해당 구독만 포함
                    List<FacilitySubscription> billable = subs.stream()
                            .filter(s -> isBillableInMonth(s, yearMonth, policy))
                            .toList();
                    if (billable.isEmpty()) return;
                    BigDecimal fee = calcSubscriptionFee(policy, feeType, billable.size());
                    addFee(aggregateMap, complexId, householdId, fee);
                })
            );
        }

        // GX 월 비용 계산 (프로그램 시작일 기준)
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
                // 세대/프로그램 중복 청구 방지
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
                // 발행 완료 비용 덮어쓰기 방지
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

    // 시설 이용 비용 발행
    @Transactional
    public FacilityFeePublishRes publishFacilityFees(FacilityFeePublishReq req) {
        YearMonth yearMonth = resolveYearMonth(req.getUsageYear(), req.getUsageMonth());
        List<FacilityUsageMonthly> publishTargets = facilityUsageMonthlyRepository
                .findByUsageYearAndUsageMonthAndIsPublishedFalse(yearMonth.getYear(), yearMonth.getMonthValue());

        // 중복 발행 방지
        publishTargets.forEach(FacilityUsageMonthly::markPublished);

        // 단지별 Outbox 이벤트 적재
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
                // Outbox 활성 시 이벤트 적재
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

    // 구독형 월 청구 금액 계산 (subscriberCount = 해당 세대의 실제 구독자 수)
    private BigDecimal calcSubscriptionFee(FacilityPolicy policy, FacilityFeeType feeType, int subscriberCount) {
        BigDecimal baseFee = nullSafe(policy.getBaseFee());
        if (feeType == FacilityFeeType.PER_PERSON) {
            // 실제 구독자 수 × 인당 요금
            return baseFee.multiply(BigDecimal.valueOf(Math.max(1, subscriberCount)));
        }
        // FLAT: 기본료 + 기준 인원 초과 구독자 수 × 추가 요금
        if (policy.getIncludedPersonCount() != null && policy.getExtraPersonFee() != null) {
            long extraPersons = Math.max(0, subscriberCount - policy.getIncludedPersonCount());
            BigDecimal extraFee = nullSafe(policy.getExtraPersonFee()).multiply(BigDecimal.valueOf(extraPersons));
            return baseFee.add(extraFee);
        }
        return baseFee;
    }

    // 구독 당월 청구 대상 판별 (신청/해지 기준일)
    private boolean isBillableInMonth(FacilitySubscription subscription, YearMonth yearMonth, FacilityPolicy policy) {
        Integer subscribeCutoffDay = policy.getSubscribeCutoffDay();
        Integer cancelCutoffDay = policy.getCancelCutoffDay();

        // 신청 기준일 적용
        if (subscribeCutoffDay != null) {
            int maxSubscribeDay = Math.min(subscribeCutoffDay, yearMonth.lengthOfMonth());
            LocalDate subscribeMonth = YearMonth.from(subscription.getSubscribedAt()).atDay(1);
            if (subscribeMonth.equals(yearMonth.atDay(1))) {
                if (subscription.getSubscribedAt().getDayOfMonth() > maxSubscribeDay) {
                    return false;
                }
            }
        }

        // 해지 기준일 적용
        if (subscription.getCancelledAt() != null) {
            LocalDate cancelMonth = YearMonth.from(subscription.getCancelledAt()).atDay(1);
            // 이전 달 해지 제외
            if (cancelMonth.isBefore(yearMonth.atDay(1))) {
                return false;
            }
            // 당월 해지 기준일 적용
            if (cancelCutoffDay != null && cancelMonth.equals(yearMonth.atDay(1))) {
                int maxCancelDay = Math.min(cancelCutoffDay, yearMonth.lengthOfMonth());
                if (subscription.getCancelledAt().getDayOfMonth() <= maxCancelDay) {
                    return false;
                }
            }
        }

        return true;
    }

    // 건별 요금 금액 계산
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
