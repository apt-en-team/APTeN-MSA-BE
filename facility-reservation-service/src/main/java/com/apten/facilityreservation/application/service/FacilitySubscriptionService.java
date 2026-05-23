package com.apten.facilityreservation.application.service;

import com.apten.common.enums.FeatureCode;
import com.apten.common.exception.BusinessException;
import com.apten.facilityreservation.application.model.response.AdminFacilitySubscriptionListRes;
import com.apten.facilityreservation.application.model.response.FacilitySubscriptionCancelRes;
import com.apten.facilityreservation.domain.entity.FacilitySubscription;
import com.apten.facilityreservation.domain.entity.HouseholdMemberCache;
import com.apten.facilityreservation.domain.enums.FacilitySubscriptionStatus;
import com.apten.facilityreservation.domain.repository.FacilitySubscriptionRepository;
import com.apten.facilityreservation.domain.repository.HouseholdMemberCacheRepository;
import com.apten.facilityreservation.exception.FacilityReservationErrorCode;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 시설 이용 구독 관련 API 시그니처를 담당하는 서비스이다.
@Service
@RequiredArgsConstructor
public class FacilitySubscriptionService {

    private final FacilitySubscriptionRepository facilitySubscriptionRepository;
    private final HouseholdMemberCacheRepository householdMemberCacheRepository;
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
}
