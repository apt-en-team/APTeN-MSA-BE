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
import com.apten.facilityreservation.domain.repository.FacilityPolicyRepository;
import com.apten.facilityreservation.domain.repository.FacilityRepository;
import com.apten.facilityreservation.domain.repository.FacilitySubscriptionRepository;
import com.apten.facilityreservation.domain.repository.HouseholdCacheRepository;
import com.apten.facilityreservation.domain.repository.HouseholdMemberCacheRepository;
import com.apten.facilityreservation.domain.repository.UserCacheRepository;
import com.apten.facilityreservation.exception.FacilityReservationErrorCode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 시설 이용 구독 관련 API 시그니처를 담당하는 서비스이다.
@Service
@RequiredArgsConstructor
public class FacilitySubscriptionService {

    private final FacilitySubscriptionRepository facilitySubscriptionRepository;
    private final HouseholdMemberCacheRepository householdMemberCacheRepository;
    private final HouseholdCacheRepository householdCacheRepository;
    private final UserCacheRepository userCacheRepository;
    private final FacilityRepository facilityRepository;
    private final FacilityPolicyRepository facilityPolicyRepository;
    private final FeatureAccessService featureAccessService;

    // 입주민이 시설 구독을 해지한다.
    @Transactional
    public FacilitySubscriptionCancelRes cancelSubscription(Long userId, Long complexId, Long facilityId) {
        featureAccessService.validateEnabled(complexId, FeatureCode.FACILITY);

        HouseholdMemberCache memberCache = householdMemberCacheRepository.findByUserIdAndStatus(userId, "ACTIVE")
                .orElseThrow(() -> new BusinessException(FacilityReservationErrorCode.USER_NOT_FOUND));

        FacilitySubscription subscription = facilitySubscriptionRepository
                .findByHouseholdIdAndFacilityIdAndStatus(
                        memberCache.getHouseholdId(), facilityId, FacilitySubscriptionStatus.ACTIVE)
                .orElseThrow(() -> new BusinessException(FacilityReservationErrorCode.SUBSCRIPTION_NOT_FOUND));

        // 조회된 구독이 요청 단지 소속인지 검증한다.
        if (!subscription.getComplexId().equals(complexId)) {
            throw new BusinessException(FacilityReservationErrorCode.SUBSCRIPTION_NOT_FOUND);
        }

        // 신청일로부터 30일 미만이면 해지를 거부한다.
        if (subscription.getSubscribedAt().plusDays(30).isAfter(LocalDate.now())) {
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

    // 관리자가 단지 내 구독 목록을 조회한다. facilityId/status 필터 적용 가능하다.
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

        return subscriptions.stream()
                .map(s -> AdminFacilitySubscriptionListRes.builder()
                        .subscriptionId(s.getId())
                        .complexId(s.getComplexId())
                        .householdId(s.getHouseholdId())
                        .facilityId(s.getFacilityId())
                        .subscribedAt(s.getSubscribedAt())
                        .cancelledAt(s.getCancelledAt())
                        .status(s.getStatus())
                        .build())
                .toList();
    }

    // API-652 입주민 본인의 구독 목록을 조회한다.
    @Transactional(readOnly = true)
    public List<ResidentFacilitySubscriptionListRes> getMySubscriptions(Long userId, Long complexId) {
        featureAccessService.validateEnabled(complexId, FeatureCode.FACILITY);

        HouseholdMemberCache memberCache = householdMemberCacheRepository.findByUserIdAndStatus(userId, "ACTIVE")
                .orElseThrow(() -> new BusinessException(FacilityReservationErrorCode.USER_NOT_FOUND));

        List<FacilitySubscription> subscriptions = facilitySubscriptionRepository
                .findByHouseholdIdAndComplexIdOrderBySubscribedAtDesc(memberCache.getHouseholdId(), complexId);

        if (subscriptions.isEmpty()) {
            return List.of();
        }

        List<Long> facilityIds = subscriptions.stream().map(FacilitySubscription::getFacilityId).toList();

        // 시설 이름과 요금 방식을 배치 조회한다.
        Map<Long, Facility> facilityMap = facilityRepository.findAllById(facilityIds)
                .stream().collect(Collectors.toMap(Facility::getId, f -> f));
        Map<Long, FacilityPolicy> policyMap = facilityPolicyRepository
                .findByComplexIdAndFacilityIdInAndIsActiveTrue(complexId, facilityIds)
                .stream().collect(Collectors.toMap(FacilityPolicy::getFacilityId, p -> p));

        return subscriptions.stream()
                .map(s -> {
                    Facility facility = facilityMap.get(s.getFacilityId());
                    FacilityPolicy policy = policyMap.get(s.getFacilityId());
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
                            .build();
                })
                .toList();
    }

    // 관리자 세대별 구독 요약 목록을 조회한다.
    @Transactional(readOnly = true)
    public List<AdminHouseholdSubscriptionSummaryRes> getHouseholdSubscriptionList(Long complexId) {
        List<FacilitySubscription> all = facilitySubscriptionRepository.findByComplexIdOrderBySubscribedAtDesc(complexId);
        if (all.isEmpty()) {
            return List.of();
        }

        // householdId → 구독 목록으로 그룹핑한다.
        Map<Long, List<FacilitySubscription>> byHousehold = all.stream()
                .collect(Collectors.groupingBy(FacilitySubscription::getHouseholdId));

        // 세대 정보를 배치 조회한다.
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
                    return AdminHouseholdSubscriptionSummaryRes.builder()
                            .householdId(householdId)
                            .buildingNo(household != null ? household.getBuildingNo() : "-")
                            .unitNo(household != null ? household.getUnitNo() : "-")
                            .activeCount((int) activeCount)
                            .cancelledCount((int) cancelledCount)
                            .build();
                })
                .sorted(java.util.Comparator.comparing(AdminHouseholdSubscriptionSummaryRes::getBuildingNo)
                        .thenComparing(AdminHouseholdSubscriptionSummaryRes::getUnitNo))
                .toList();
    }

    // 관리자 세대별 구독 상세를 조회한다.
    @Transactional(readOnly = true)
    public AdminHouseholdSubscriptionDetailRes getHouseholdSubscriptionDetail(Long complexId, Long householdId) {
        HouseholdCache household = householdCacheRepository.findByHouseholdId(householdId)
                .orElseThrow(() -> new BusinessException(FacilityReservationErrorCode.HOUSEHOLD_NOT_FOUND));

        List<FacilitySubscription> subscriptions = facilitySubscriptionRepository
                .findByHouseholdIdAndComplexIdOrderBySubscribedAtDesc(householdId, complexId);

        // 세대원 이름 조회: householdMemberCache → userCache 순으로 배치 조회한다.
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

        // 시설명과 요금 정보를 배치 조회한다.
        List<Long> facilityIds = subscriptions.stream().map(FacilitySubscription::getFacilityId).toList();
        Map<Long, Facility> facilityMap = facilityRepository.findAllById(facilityIds)
                .stream().collect(Collectors.toMap(Facility::getId, f -> f));
        Map<Long, FacilityPolicy> policyMap = facilityIds.isEmpty() ? Map.of() :
                facilityPolicyRepository.findByComplexIdAndFacilityIdInAndIsActiveTrue(complexId, facilityIds)
                        .stream().collect(Collectors.toMap(FacilityPolicy::getFacilityId, p -> p));

        List<AdminHouseholdSubscriptionDetailRes.SubscriptionInfo> subInfos = subscriptions.stream()
                .map(s -> {
                    Facility facility = facilityMap.get(s.getFacilityId());
                    FacilityPolicy policy = policyMap.get(s.getFacilityId());
                    return AdminHouseholdSubscriptionDetailRes.SubscriptionInfo.builder()
                            .subscriptionId(s.getId())
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

    // 관리자가 세대의 구독을 강제 해지한다.
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
