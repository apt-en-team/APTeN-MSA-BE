package com.apten.facilityreservation.application.service;

import com.apten.common.enums.FeatureCode;
import com.apten.common.exception.BusinessException;
import com.apten.facilityreservation.application.model.response.AdminFacilitySubscriptionListRes;
import com.apten.facilityreservation.application.model.response.AdminHouseholdSubscriptionDetailRes;
import com.apten.facilityreservation.application.model.response.AdminHouseholdSubscriptionSummaryRes;
import com.apten.facilityreservation.application.model.response.FacilitySubscriptionCancelRes;
import com.apten.facilityreservation.application.model.response.ResidentFacilitySubscriptionListRes;
import com.apten.facilityreservation.domain.entity.Facility;
import com.apten.facilityreservation.domain.entity.FacilityPolicy;
import com.apten.facilityreservation.domain.entity.FacilitySubscription;
import com.apten.facilityreservation.domain.entity.HouseholdCache;
import com.apten.facilityreservation.domain.entity.HouseholdMemberCache;
import com.apten.facilityreservation.domain.entity.UserCache;
import com.apten.facilityreservation.domain.enums.FacilitySubscriptionStatus;
import com.apten.facilityreservation.domain.enums.ReservationStatus;
import com.apten.facilityreservation.domain.repository.FacilityPolicyRepository;
import com.apten.facilityreservation.domain.repository.FacilityRepository;
import com.apten.facilityreservation.domain.repository.FacilitySubscriptionRepository;
import com.apten.facilityreservation.domain.repository.HouseholdCacheRepository;
import com.apten.facilityreservation.domain.repository.HouseholdMemberCacheRepository;
import com.apten.facilityreservation.domain.repository.ReservationRepository;
import com.apten.facilityreservation.domain.repository.UserCacheRepository;
import com.apten.facilityreservation.exception.FacilityReservationErrorCode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 시설 구독 관리
@Service
@RequiredArgsConstructor
public class FacilitySubscriptionService {

    private final FacilitySubscriptionRepository facilitySubscriptionRepository;
    private final HouseholdMemberCacheRepository householdMemberCacheRepository;
    private final HouseholdCacheRepository householdCacheRepository;
    private final UserCacheRepository userCacheRepository;
    private final FacilityRepository facilityRepository;
    private final FacilityPolicyRepository facilityPolicyRepository;
    private final ReservationRepository reservationRepository;
    private final FeatureAccessService featureAccessService;

    // 입주민 시설 구독 해지
    @Transactional
    public FacilitySubscriptionCancelRes cancelSubscription(Long userId, Long complexId, Long facilityId) {
        featureAccessService.validateEnabled(complexId, FeatureCode.FACILITY);

        HouseholdMemberCache memberCache = householdMemberCacheRepository.findByUserIdAndStatus(userId, "ACTIVE")
                .orElseThrow(() -> new BusinessException(FacilityReservationErrorCode.USER_NOT_FOUND));

        FacilitySubscription subscription = facilitySubscriptionRepository
                .findByUserIdAndFacilityIdAndStatus(userId, facilityId, FacilitySubscriptionStatus.ACTIVE)
                .orElseThrow(() -> new BusinessException(FacilityReservationErrorCode.SUBSCRIPTION_NOT_FOUND));

        // 구독 단지 소속 검증
        if (!subscription.getComplexId().equals(complexId)) {
            throw new BusinessException(FacilityReservationErrorCode.SUBSCRIPTION_NOT_FOUND);
        }

        // 확정 예약 잔여 여부 검증
        if (reservationRepository.existsByUserIdAndFacilityIdAndStatus(userId, facilityId, ReservationStatus.CONFIRMED)) {
            throw new BusinessException(FacilityReservationErrorCode.SUBSCRIPTION_HAS_CONFIRMED_RESERVATION);
        }

        // 월 이용 이력/가입 기간 검증
        LocalDate monthStart = LocalDate.now().withDayOfMonth(1);
        LocalDate monthEnd = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
        boolean hasCompletedThisMonth = reservationRepository.existsByUserIdAndFacilityIdAndReservationDateBetweenAndStatus(
                userId, facilityId, monthStart, monthEnd, ReservationStatus.COMPLETED);
        if (hasCompletedThisMonth && subscription.getSubscribedAt().plusDays(30).isAfter(LocalDate.now())) {
            throw new BusinessException(FacilityReservationErrorCode.SUBSCRIPTION_TOO_EARLY_TO_CANCEL);
        }

        subscription.cancel(LocalDate.now());

        return FacilitySubscriptionCancelRes.builder()
                .subscriptionId(subscription.getId())
                .facilityId(subscription.getFacilityId())
                .cancelledAt(subscription.getCancelledAt())
                .status(subscription.getStatus())
                .build();
    }

    // 관리자 시설 구독 목록 조회
    @Transactional(readOnly = true)
    public List<AdminFacilitySubscriptionListRes> getAdminSubscriptionList(
            Long complexId, Long facilityId, FacilitySubscriptionStatus status) {
        featureAccessService.validateEnabled(complexId, FeatureCode.FACILITY);

        List<FacilitySubscription> subscriptions;
        if (facilityId != null && status != null) {
            subscriptions = facilitySubscriptionRepository
                    .findByComplexIdAndFacilityIdOrderBySubscribedAtDesc(complexId, facilityId)
                    .stream()
                    .filter(s -> s.getStatus() == status)
                    .toList();
        } else if (facilityId != null) {
            subscriptions = facilitySubscriptionRepository
                    .findByComplexIdAndFacilityIdOrderBySubscribedAtDesc(complexId, facilityId);
        } else if (status != null) {
            subscriptions = facilitySubscriptionRepository
                    .findByComplexIdAndStatusOrderBySubscribedAtDesc(complexId, status);
        } else {
            subscriptions = facilitySubscriptionRepository
                    .findByComplexIdOrderBySubscribedAtDesc(complexId);
        }

        List<Long> userIds = subscriptions.stream()
                .map(FacilitySubscription::getUserId)
                .filter(java.util.Objects::nonNull)
                .distinct().toList();
        Map<Long, UserCache> userMap = userCacheRepository.findAllById(userIds)
                .stream().collect(Collectors.toMap(UserCache::getId, u -> u));

        List<Long> householdIds = subscriptions.stream()
                .map(FacilitySubscription::getHouseholdId)
                .filter(java.util.Objects::nonNull)
                .distinct().toList();
        Map<Long, HouseholdCache> householdMap = householdCacheRepository.findAllById(householdIds)
                .stream().collect(Collectors.toMap(HouseholdCache::getHouseholdId, h -> h));

        return subscriptions.stream()
                .map(s -> {
                    UserCache user = s.getUserId() != null ? userMap.get(s.getUserId()) : null;
                    HouseholdCache household = s.getHouseholdId() != null ? householdMap.get(s.getHouseholdId()) : null;
                    return AdminFacilitySubscriptionListRes.builder()
                            .subscriptionId(s.getId())
                            .complexId(s.getComplexId())
                            .householdId(s.getHouseholdId())
                            .userId(s.getUserId())
                            .subscriberName(user != null ? user.getName() : null)
                            .buildingNo(household != null ? household.getBuildingNo() : null)
                            .unitNo(household != null ? household.getUnitNo() : null)
                            .facilityId(s.getFacilityId())
                            .subscribedAt(s.getSubscribedAt())
                            .cancelledAt(s.getCancelledAt())
                            .status(s.getStatus())
                            .build();
                })
                .toList();
    }

    // 입주민 시설 구독 목록 조회
    @Transactional(readOnly = true)
    public List<ResidentFacilitySubscriptionListRes> getMySubscriptions(Long userId, Long complexId) {
        featureAccessService.validateEnabled(complexId, FeatureCode.FACILITY);

        HouseholdMemberCache memberCache = householdMemberCacheRepository.findByUserIdAndStatus(userId, "ACTIVE")
                .orElseThrow(() -> new BusinessException(FacilityReservationErrorCode.USER_NOT_FOUND));

        List<FacilitySubscription> subscriptions = facilitySubscriptionRepository
                .findByUserIdAndComplexIdOrderBySubscribedAtDesc(userId, complexId);

        if (subscriptions.isEmpty()) {
            return List.of();
        }

        List<Long> facilityIds = subscriptions.stream().map(FacilitySubscription::getFacilityId).toList();

        // 시설/요금 정보 일괄 조회 (N+1 방지)
        Map<Long, Facility> facilityMap = facilityRepository.findAllById(facilityIds)
                .stream().collect(Collectors.toMap(Facility::getId, f -> f));
        Map<Long, FacilityPolicy> policyMap = facilityPolicyRepository
                .findByComplexIdAndFacilityIdInAndIsActiveTrue(complexId, facilityIds)
                .stream().collect(Collectors.toMap(FacilityPolicy::getFacilityId, p -> p));

        LocalDate monthStart = LocalDate.now().withDayOfMonth(1);
        LocalDate monthEnd = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());

        return subscriptions.stream()
                .map(s -> {
                    Facility facility = facilityMap.get(s.getFacilityId());
                    FacilityPolicy policy = policyMap.get(s.getFacilityId());
                    boolean hasCompleted = reservationRepository.existsByUserIdAndFacilityIdAndReservationDateBetweenAndStatus(
                            userId, s.getFacilityId(), monthStart, monthEnd, ReservationStatus.COMPLETED);
                    return ResidentFacilitySubscriptionListRes.builder()
                            .subscriptionId(s.getId())
                            .facilityId(s.getFacilityId())
                            .facilityName(facility != null ? facility.getName() : "")
                            .feeType(policy != null ? policy.getFeeType() : null)
                            .baseFee(policy != null ? policy.getBaseFee() : null)
                            .subscribeCutoffDay(policy != null ? policy.getSubscribeCutoffDay() : null)
                            .cancelCutoffDay(policy != null ? policy.getCancelCutoffDay() : null)
                            .subscribedAt(s.getSubscribedAt())
                            .cancelledAt(s.getCancelledAt())
                            .status(s.getStatus())
                            .hasCompletedThisMonth(hasCompleted)
                            .build();
                })
                .toList();
    }

    // 관리자 세대별 구독 요약 조회
    @Transactional(readOnly = true)
    public List<AdminHouseholdSubscriptionSummaryRes> getHouseholdSubscriptionList(Long complexId) {
        List<FacilitySubscription> all = facilitySubscriptionRepository.findByComplexIdOrderBySubscribedAtDesc(complexId);
        if (all.isEmpty()) {
            return List.of();
        }

        // 세대별 구독 그룹화
        Map<Long, List<FacilitySubscription>> byHousehold = all.stream()
                .collect(Collectors.groupingBy(FacilitySubscription::getHouseholdId));

        // 세대 정보 일괄 조회 (N+1 방지)
        Map<Long, HouseholdCache> householdMap = householdCacheRepository
                .findAllById(byHousehold.keySet())
                .stream()
                .collect(Collectors.toMap(HouseholdCache::getHouseholdId, h -> h));

        return byHousehold.entrySet().stream()
                .map(e -> {
                    Long householdId = e.getKey();
                    List<FacilitySubscription> subs = e.getValue();
                    HouseholdCache household = householdMap.get(householdId);
                    long activeCount = subs.stream().filter(s -> s.getStatus() == FacilitySubscriptionStatus.ACTIVE).count();
                    long cancelledCount = subs.size() - activeCount;
                    String buildingNo = (household != null && household.getBuildingNo() != null)
                            ? household.getBuildingNo() : "-";
                    String unitNo = (household != null && household.getUnitNo() != null)
                            ? household.getUnitNo() : "-";
                    return AdminHouseholdSubscriptionSummaryRes.builder()
                            .householdId(householdId)
                            .buildingNo(buildingNo)
                            .unitNo(unitNo)
                            .activeCount((int) activeCount)
                            .cancelledCount((int) cancelledCount)
                            .build();
                })
                .sorted(java.util.Comparator.comparing(AdminHouseholdSubscriptionSummaryRes::getBuildingNo,
                                java.util.Comparator.nullsLast(java.util.Comparator.naturalOrder()))
                        .thenComparing(AdminHouseholdSubscriptionSummaryRes::getUnitNo,
                                java.util.Comparator.nullsLast(java.util.Comparator.naturalOrder())))
                .toList();
    }

    // 관리자 세대별 구독 상세 조회
    @Transactional(readOnly = true)
    public AdminHouseholdSubscriptionDetailRes getHouseholdSubscriptionDetail(Long complexId, Long householdId) {
        HouseholdCache household = householdCacheRepository.findByHouseholdId(householdId)
                .orElseThrow(() -> new BusinessException(FacilityReservationErrorCode.HOUSEHOLD_NOT_FOUND));

        List<FacilitySubscription> subscriptions = facilitySubscriptionRepository
                .findByHouseholdIdAndComplexIdOrderBySubscribedAtDesc(householdId, complexId);

        // 세대원 정보 일괄 조회 (캐시 기준)
        List<HouseholdMemberCache> members = householdMemberCacheRepository.findByHouseholdId(householdId);
        List<Long> userIds = members.stream().map(HouseholdMemberCache::getUserId).toList();
        Map<Long, UserCache> userMap = userCacheRepository.findAllById(userIds)
                .stream().collect(Collectors.toMap(UserCache::getId, u -> u));

        List<AdminHouseholdSubscriptionDetailRes.MemberInfo> memberInfos = members.stream()
                .map(m -> {
                    UserCache user = userMap.get(m.getUserId());
                    return AdminHouseholdSubscriptionDetailRes.MemberInfo.builder()
                            .name(user != null ? user.getName() : "-")
                            .memberRole(m.getMemberRole())
                            .isPrimary(m.isPrimary())
                            .build();
                })
                .sorted(java.util.Comparator.comparing(AdminHouseholdSubscriptionDetailRes.MemberInfo::isPrimary).reversed())
                .toList();

        // 시설/요금 정보 일괄 조회 (N+1 방지)
        List<Long> facilityIds = subscriptions.stream().map(FacilitySubscription::getFacilityId).toList();
        Map<Long, Facility> facilityMap = facilityRepository.findAllById(facilityIds)
                .stream().collect(Collectors.toMap(Facility::getId, f -> f));
        Map<Long, FacilityPolicy> policyMap = facilityIds.isEmpty() ? Map.of() :
                facilityPolicyRepository.findByComplexIdAndFacilityIdInAndIsActiveTrue(complexId, facilityIds)
                        .stream().collect(Collectors.toMap(FacilityPolicy::getFacilityId, p -> p));

        // 구독자 이름 조회용 userId 수집 (세대원 userMap 이미 있음)
        List<AdminHouseholdSubscriptionDetailRes.SubscriptionInfo> subInfos = subscriptions.stream()
                .map(s -> {
                    Facility facility = facilityMap.get(s.getFacilityId());
                    FacilityPolicy policy = policyMap.get(s.getFacilityId());
                    UserCache subscriber = s.getUserId() != null ? userMap.get(s.getUserId()) : null;
                    return AdminHouseholdSubscriptionDetailRes.SubscriptionInfo.builder()
                            .subscriptionId(s.getId())
                            .userId(s.getUserId())
                            .subscriberName(subscriber != null ? subscriber.getName() : null)
                            .facilityId(s.getFacilityId())
                            .facilityName(facility != null ? facility.getName() : "-")
                            .feeType(policy != null ? policy.getFeeType() : null)
                            .baseFee(policy != null ? policy.getBaseFee() : null)
                            .status(s.getStatus())
                            .subscribedAt(s.getSubscribedAt())
                            .cancelledAt(s.getCancelledAt())
                            .build();
                })
                .toList();

        return AdminHouseholdSubscriptionDetailRes.builder()
                .householdId(householdId)
                .buildingNo(household.getBuildingNo())
                .unitNo(household.getUnitNo())
                .members(memberInfos)
                .subscriptions(subInfos)
                .build();
    }

    // 관리자 시설 구독 강제 해지
    @Transactional
    public void adminCancelSubscription(Long complexId, Long subscriptionId) {
        FacilitySubscription subscription = facilitySubscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new BusinessException(FacilityReservationErrorCode.SUBSCRIPTION_NOT_FOUND));

        if (!subscription.getComplexId().equals(complexId)) {
            throw new BusinessException(FacilityReservationErrorCode.FACILITY_NOT_FOUND);
        }

        if (subscription.getStatus() == FacilitySubscriptionStatus.CANCELLED) {
            throw new BusinessException(FacilityReservationErrorCode.SUBSCRIPTION_ALREADY_CANCELLED);
        }

        subscription.cancel(LocalDate.now());
    }
}
