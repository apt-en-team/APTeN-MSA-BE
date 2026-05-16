package com.apten.parkingvehicle.application.service;

import com.apten.common.enums.FeatureCode;
import com.apten.common.kafka.payload.ApartmentComplexEventPayload;
import com.apten.common.kafka.payload.HouseholdEventPayload;
import com.apten.common.kafka.payload.HouseholdMemberEventPayload;
import com.apten.common.kafka.payload.UserEventPayload;
import com.apten.parkingvehicle.domain.entity.*;
import com.apten.parkingvehicle.domain.repository.*;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// parking-vehicle-service에서 참조 캐시 테이블 upsert를 담당하는 서비스
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ParkingVehicleReferenceCacheService {

    // user cache 저장소
    private final UserCacheRepository userCacheRepository;

    // household cache 저장소
    private final HouseholdCacheRepository householdCacheRepository;

    // complex cache 저장소
    private final ComplexCacheRepository complexCacheRepository;

    // complex feature cache 저장소
    private final ComplexFeatureCacheRepository complexFeatureCacheRepository;

    // parking setting 저장소
    private final ParkingSettingRepository parkingSettingRepository;

    // 단지 생성 직후 기본 주차 구역을 함께 보장하기 위한 zone 저장소
    private final ParkingZoneRepository parkingZoneRepository;

    // 동일 PK가 있으면 갱신하고 없으면 생성한다
    public void upsertUserCache(UserEventPayload payload) {
        UserCache userCache = userCacheRepository.findById(payload.getUserId())
                .orElseGet(() -> UserCache.builder().id(payload.getUserId()).build());
        userCache.apply(payload);
        userCacheRepository.save(userCache);
    }

    // 동일 PK가 있으면 갱신하고 없으면 생성한다
    public void upsertHouseholdCache(HouseholdEventPayload payload) {
        HouseholdCache householdCache = householdCacheRepository.findById(payload.getHouseholdId())
                .orElseGet(() -> HouseholdCache.builder().id(payload.getHouseholdId()).build());
        householdCache.apply(payload);
        householdCacheRepository.save(householdCache);
    }

    // 단지 기본 정보 캐시를 최신 이벤트 기준으로 반영한다.
    public void upsertApartmentComplexCache(ApartmentComplexEventPayload payload) {
        if (payload == null || payload.getApartmentComplexId() == null) {
            return;
        }

        ComplexCache complexCache = complexCacheRepository.findById(payload.getApartmentComplexId())
                .orElseGet(() -> ComplexCache.builder().id(payload.getApartmentComplexId()).build());
        complexCache.updateFromPayload(payload);
        complexCacheRepository.save(complexCache);
    }

    // 단지 기능 설정 캐시를 최신 이벤트 기준으로 반영한다.
    public void upsertComplexFeatureCache(ApartmentComplexEventPayload payload) {
        if (payload == null || payload.getApartmentComplexId() == null) {
            return;
        }

        if (payload.getFeatures() == null) {
            return;
        }

        logUnknownFeatureCodes(payload.getApartmentComplexId(), payload.getFeatures());

        // 누락된 기능은 기존 서비스 영향 방지를 위해 true로 보정한다.
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

    // 단지의 주차 설정 row와 기본 zone을 함께 보장한다.
    // setting row가 없으면 NONE 기본값으로 생성하고, zone이 없으면 기본 zone 1개를 함께 생성한다.
    // parking_type은 관리자가 PATCH로 변경하는 값이므로 이벤트로 덮어쓰지 않는다.
    public void ensureParkingSetting(ApartmentComplexEventPayload payload) {
        if (payload == null || payload.getApartmentComplexId() == null) {
            return;
        }

        Long complexId = payload.getApartmentComplexId();

        // parking_setting row 보장 (이미 있으면 parking_type 보존)
        if (!parkingSettingRepository.existsByComplexId(complexId)) {
            ParkingSetting parkingSetting = ParkingSetting.builder()
                    .complexId(complexId)
                    .build();
            parkingSettingRepository.save(parkingSetting);
        }

        // 기본 zone 1개 보장 (이미 zone이 있으면 skip해 멱등성 유지)
        if (!parkingZoneRepository.existsByComplexId(complexId)) {
            ParkingZone defaultZone = ParkingZone.builder()
                    .complexId(complexId)
                    .areaName("기본 주차장")
                    .zoneName(null)
                    .totalSlots(0)
                    .isActive(true)
                    .build();
            parkingZoneRepository.save(defaultZone);
        }
    }

    // 세대원 이벤트를 받아 세대 캐시의 세대주 식별자를 동기화한다.
    public void syncHouseholdHeadUser(HouseholdMemberEventPayload payload) {
        //TODO 세대원 삭제와 역할 변경까지 고려한 세대주 동기화 정책 확정
        householdCacheRepository.findById(payload.getHouseholdId())
                .ifPresent(householdCache -> {
                    if ("HEAD".equals(payload.getMemberRole())) {
                        householdCache.updateHeadUserId(payload.getUserId());
                        householdCacheRepository.save(householdCache);
                    }
                });
    }

    // 이벤트에 누락된 기능은 true로 처리해 기존 기능 차단을 피한다.
    private boolean resolveFeatureEnabled(Map<String, Boolean> features, FeatureCode featureCode) {
        String key = featureCode.name();
        if (!features.containsKey(key)) {
            return true;
        }

        Boolean enabled = features.get(key);
        return enabled == null || enabled;
    }

    // 알 수 없는 기능 코드는 로그만 남기고 안전하게 건너뛴다.
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
