package com.apten.facilityreservation.application.service;

import com.apten.common.kafka.payload.ApartmentComplexEventPayload;
import com.apten.common.kafka.payload.HouseholdEventPayload;
import com.apten.common.kafka.payload.HouseholdMemberEventPayload;
import com.apten.common.kafka.payload.UserEventPayload;
import com.apten.common.enums.FeatureCode;
import com.apten.facilityreservation.domain.entity.ComplexCache;
import com.apten.facilityreservation.domain.entity.ComplexFeatureCache;
import com.apten.facilityreservation.domain.entity.HouseholdCache;
import com.apten.facilityreservation.domain.entity.HouseholdMemberCache;
import com.apten.facilityreservation.domain.entity.UserCache;
import com.apten.facilityreservation.domain.repository.ComplexCacheRepository;
import com.apten.facilityreservation.domain.repository.ComplexFeatureCacheRepository;
import com.apten.facilityreservation.domain.repository.HouseholdCacheRepository;
import com.apten.facilityreservation.domain.repository.HouseholdMemberCacheRepository;
import com.apten.facilityreservation.domain.repository.UserCacheRepository;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 시설예약 참조 캐시 동기화
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class FacilityReservationReferenceCacheService {

    // 사용자 캐시 저장소
    private final UserCacheRepository userCacheRepository;

    // 단지 캐시 저장소
    private final ComplexCacheRepository complexCacheRepository;

    // 단지 기능 캐시 저장소
    private final ComplexFeatureCacheRepository complexFeatureCacheRepository;

    // 세대 캐시 저장소
    private final HouseholdCacheRepository householdCacheRepository;

    // 세대원 캐시 저장소
    private final HouseholdMemberCacheRepository householdMemberCacheRepository;

    // 사용자 캐시 저장/갱신
    public void upsertUserCache(UserEventPayload payload) {
        UserCache userCache = userCacheRepository.findById(payload.getUserId())
                .orElseGet(() -> UserCache.builder().id(payload.getUserId()).build());
        userCache.apply(payload);
        userCacheRepository.save(userCache);
    }

    // 단지 캐시 저장/갱신
    public void upsertApartmentComplexCache(ApartmentComplexEventPayload payload) {
        ComplexCache complexCache = complexCacheRepository.findById(payload.getApartmentComplexId())
                .orElseGet(() -> ComplexCache.builder().id(payload.getApartmentComplexId()).build());
        complexCache.apply(payload);
        complexCacheRepository.save(complexCache);
    }

    // 단지 기능 캐시 저장/갱신
    public void upsertComplexFeatureCache(ApartmentComplexEventPayload payload) {
        if (payload == null || payload.getApartmentComplexId() == null) {
            return;
        }

        if (payload.getFeatures() == null) {
            return;
        }

        logUnknownFeatureCodes(payload.getApartmentComplexId(), payload.getFeatures());

        // 누락 기능 기본 활성
        for (FeatureCode featureCode : FeatureCode.values()) {
            boolean enabled = resolveFeatureEnabled(payload.getFeatures(), featureCode);

            ComplexFeatureCache featureCache = complexFeatureCacheRepository
                    .findByComplexIdAndFeatureCode(payload.getApartmentComplexId(), featureCode)
                    .orElseGet(() -> ComplexFeatureCache.builder()
                            .complexId(payload.getApartmentComplexId())
                            .featureCode(featureCode)
                            .enabled(true)
                            .build());

            featureCache.updateEnabled(enabled);
            complexFeatureCacheRepository.save(featureCache);
        }
    }

    // 세대 캐시 저장/갱신
    public void upsertHouseholdCache(HouseholdEventPayload payload) {
        HouseholdCache householdCache = householdCacheRepository.findById(payload.getHouseholdId())
                .orElseGet(() -> HouseholdCache.builder().householdId(payload.getHouseholdId()).build());
        householdCache.apply(payload);
        householdCacheRepository.save(householdCache);
    }

    // 세대원 캐시 저장/갱신
    public void upsertHouseholdMemberCache(HouseholdMemberEventPayload payload) {
        HouseholdMemberCache householdMemberCache = householdMemberCacheRepository.findById(payload.getHouseholdMemberId())
                .orElseGet(() -> HouseholdMemberCache.builder().householdMemberId(payload.getHouseholdMemberId()).build());
        householdMemberCache.apply(payload);
        householdMemberCacheRepository.save(householdMemberCache);
    }

    // 누락 기능 기본 활성
    private boolean resolveFeatureEnabled(Map<String, Boolean> features, FeatureCode featureCode) {
        String key = featureCode.name();
        if (!features.containsKey(key)) {
            return true;
        }

        Boolean enabled = features.get(key);
        return enabled == null || enabled;
    }

    // 미지원 기능 코드 로그
    private void logUnknownFeatureCodes(Long complexId, Map<String, Boolean> features) {
        for (String featureCode : features.keySet()) {
            try {
                FeatureCode.valueOf(featureCode.toUpperCase());
            } catch (IllegalArgumentException exception) {
                log.warn("Skip unknown complex feature cache event. complexId={}, featureCode={}", complexId, featureCode);
            }
        }
    }
}
