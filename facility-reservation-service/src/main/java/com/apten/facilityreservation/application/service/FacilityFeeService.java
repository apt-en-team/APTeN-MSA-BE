package com.apten.facilityreservation.application.service;

import com.apten.common.exception.BusinessException;
import com.apten.facilityreservation.application.model.request.FacilityFeeCalculateReq;
import com.apten.facilityreservation.application.model.request.FacilityFeePublishReq;
import com.apten.facilityreservation.application.model.response.FacilityFeeCalculateRes;
import com.apten.facilityreservation.application.model.response.FacilityFeePublishRes;
import com.apten.facilityreservation.domain.entity.FacilityPolicy;
import com.apten.facilityreservation.domain.entity.FacilityUsageMonthly;
import com.apten.facilityreservation.domain.entity.GxProgram;
import com.apten.facilityreservation.domain.entity.GxReservation;
import com.apten.facilityreservation.domain.entity.Reservation;
import com.apten.facilityreservation.domain.enums.FacilityFeeType;
import com.apten.facilityreservation.domain.enums.GxReservationStatus;
import com.apten.facilityreservation.domain.enums.ReservationStatus;
import com.apten.facilityreservation.domain.repository.FacilityPolicyRepository;
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
import java.time.LocalTime;
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
        LocalDateTime gxFromDateTime = fromDate.atStartOfDay();
        LocalDateTime gxToDateTime = toDate.atTime(LocalTime.MAX);

        Map<Long, HouseholdFeeAggregate> aggregateMap = new LinkedHashMap<>();

        // 일반 시설은 예약 건별 과금이 아니라 월 이용료 성격이라 세대-시설 기준으로 한 번만 반영한다.
        List<Reservation> completedReservations = reservationRepository.findByStatusAndReservationDateBetween(
                ReservationStatus.COMPLETED,
                fromDate,
                toDate
        );
        Map<Long, List<Reservation>> reservationsByComplex = completedReservations.stream()
                .filter(reservation -> reservation.getHouseholdId() != null)
                .collect(Collectors.groupingBy(Reservation::getComplexId));

        reservationsByComplex.forEach((complexId, reservations) -> {
            // (세대ID, 시설ID) 기준으로 예약 목록을 그룹화한다.
            Map<HouseholdFacilityKey, List<Reservation>> byKey = reservations.stream()
                    .collect(Collectors.groupingBy(r -> new HouseholdFacilityKey(r.getHouseholdId(), r.getFacilityId())));
            if (byKey.isEmpty()) {
                return;
            }

            List<Long> facilityIds = byKey.keySet().stream().map(HouseholdFacilityKey::facilityId).distinct().toList();
            Map<Long, FacilityPolicy> policyByFacilityId = facilityPolicyRepository
                    .findByComplexIdAndFacilityIdInAndIsActiveTrue(complexId, facilityIds)
                    .stream()
                    .collect(Collectors.toMap(FacilityPolicy::getFacilityId, policy -> policy));

            for (Map.Entry<HouseholdFacilityKey, List<Reservation>> entry : byKey.entrySet()) {
                HouseholdFacilityKey key = entry.getKey();
                List<Reservation> group = entry.getValue();
                FacilityPolicy policy = policyByFacilityId.get(key.facilityId());
                if (policy == null) {
                    throw new BusinessException(FacilityReservationErrorCode.INVALID_FACILITY_POLICY);
                }
                // feeType별 청구 금액을 산출한다.
                BigDecimal fee = calcFee(policy, group);
                addFee(aggregateMap, complexId, key.householdId(), fee);
            }
        });

        // GX는 승인된 프로그램 신청 1건을 월 1회 비용으로 본다.
        List<GxReservation> confirmedGxReservations = gxReservationRepository.findByStatusAndApprovedAtBetween(
                GxReservationStatus.CONFIRMED,
                gxFromDateTime,
                gxToDateTime
        );
        Map<Long, List<GxReservation>> gxReservationsByComplex = confirmedGxReservations.stream()
                .filter(reservation -> reservation.getHouseholdId() != null)
                .collect(Collectors.groupingBy(GxReservation::getComplexId));

        gxReservationsByComplex.forEach((complexId, reservations) -> {
            Set<HouseholdProgramKey> uniqueProgramKeys = reservations.stream()
                    .filter(reservation -> reservation.getApprovedAt() != null)
                    .map(reservation -> new HouseholdProgramKey(reservation.getHouseholdId(), reservation.getProgramId()))
                    .collect(Collectors.toCollection(LinkedHashSet::new));
            if (uniqueProgramKeys.isEmpty()) {
                return;
            }

            List<Long> programIds = uniqueProgramKeys.stream()
                    .map(HouseholdProgramKey::programId)
                    .distinct()
                    .toList();
            Map<Long, GxProgram> programById = gxProgramRepository.findByIdIn(programIds)
                    .stream()
                    .filter(program -> Objects.equals(program.getComplexId(), complexId))
                    .collect(Collectors.toMap(GxProgram::getId, program -> program));

            for (HouseholdProgramKey key : uniqueProgramKeys) {
                GxProgram program = programById.get(key.programId());
                if (program == null) {
                    throw new BusinessException(FacilityReservationErrorCode.GX_PROGRAM_NOT_FOUND);
                }
                addFee(aggregateMap, complexId, key.householdId(), nullSafe(program.getBaseFee()));
            }
        });

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

    // feeType에 따라 세대-시설 단위 청구 금액을 계산한다.
    private BigDecimal calcFee(FacilityPolicy policy, List<Reservation> group) {
        BigDecimal baseFee = nullSafe(policy.getBaseFee());
        FacilityFeeType type = policy.getFeeType() != null ? policy.getFeeType() : FacilityFeeType.FLAT;

        if (type == FacilityFeeType.PER_USE) {
            // 예약 완료 건수 × baseFee
            return baseFee.multiply(BigDecimal.valueOf(group.size()));
        }
        if (type == FacilityFeeType.PER_PERSON) {
            // 이용 인원 수 × baseFee
            long persons = group.stream().map(Reservation::getUserId).filter(Objects::nonNull).distinct().count();
            return baseFee.multiply(BigDecimal.valueOf(persons));
        }
        // FLAT: baseFee 기본 월 1회 + 초과 인원 요금 옵션
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
