package com.apten.household.application.service;

import com.apten.common.kafka.payload.ApartmentComplexEventPayload;
import com.apten.common.kafka.payload.FacilityUsageEventPayload;
import com.apten.common.kafka.payload.UserEventPayload;
import com.apten.common.kafka.payload.VehicleSnapshotEventPayload;
import com.apten.common.kafka.payload.VisitorUsageEventPayload;
import com.apten.household.domain.entity.ComplexCache;
import com.apten.household.domain.entity.UserCache;
import com.apten.household.domain.enums.ComplexCacheStatus;
import com.apten.household.domain.enums.UserCacheRole;
import com.apten.household.domain.enums.UserCacheStatus;
import com.apten.household.domain.repository.ComplexCacheRepository;
import com.apten.household.domain.repository.UserCacheRepository;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 외부 서비스 이벤트를 받아 household-service 캐시를 갱신하는 서비스이다.
@Service
@Transactional
@RequiredArgsConstructor
public class HouseholdReferenceCacheService {

    // 단지 캐시 저장소이다.
    private final ComplexCacheRepository complexCacheRepository;

    // 사용자 캐시 저장소이다.
    private final UserCacheRepository userCacheRepository;

    // 단지 이벤트 payload를 받아 complex_cache를 upsert 한다.
    public void upsertComplexCache(ApartmentComplexEventPayload payload) {
        // 기존 단지 캐시가 있으면 갱신하고 없으면 새로 만든다.
        ComplexCache complexCache = complexCacheRepository.findById(payload.getApartmentComplexId())
                .orElseGet(() -> ComplexCache.builder()
                        .id(payload.getApartmentComplexId())
                        .build());

        // 외부 단지 원본 값을 현재 캐시에 반영한다.
        complexCache.apply(
                payload.getName(),
                payload.getAddress(),
                ComplexCacheStatus.valueOf(payload.getStatus())
        );

        // 단지 캐시를 저장한다.
        complexCacheRepository.save(complexCache);
    }

    // 사용자 이벤트 payload를 받아 user_cache를 upsert 한다.
    public void upsertUserCache(UserEventPayload payload) {
        // 현재 공통 payload에는 phone과 birthDate가 없어 신규 생성 시 임시 기본값을 사용한다.
        UserCache userCache = userCacheRepository.findById(payload.getUserId())
                .orElseGet(() -> UserCache.builder()
                        .id(payload.getUserId())
                        .phone("")
                        .birthDate(LocalDate.of(1900, 1, 1))
                        .build());

        // 공통 payload에 있는 값만 우선 반영하고 누락 필드는 추후 계약 확장 시 채운다.
        userCache.apply(
                payload.getComplexId() != null ? payload.getComplexId() : payload.getApartmentComplexId(),
                payload.getName(),
                payload.getPhone() != null ? payload.getPhone() : userCache.getPhone(),
                payload.getBirthDate() != null ? payload.getBirthDate() : userCache.getBirthDate(),
                UserCacheRole.valueOf(payload.getRole()),
                UserCacheStatus.valueOf(payload.getStatus())
        );

        // 사용자 캐시를 저장한다.
        userCacheRepository.save(userCache);
    }

    // 차량 스냅샷 이벤트를 받아 vehicle_snapshot을 upsert 하는 준비 메서드이다.
    public void upsertVehicleSnapshot(VehicleSnapshotEventPayload payload) {
        // TODO vehicle_snapshot event 계약이 확정되면 household-service 스냅샷 저장 로직을 연결한다.
    }

    // 시설 이용 스냅샷 이벤트를 받아 facility_usage_snapshot을 upsert 하는 준비 메서드이다.
    public void upsertFacilityUsageSnapshot(FacilityUsageEventPayload payload) {
        // TODO facility_usage event 계약이 확정되면 household-service 스냅샷 저장 로직을 연결한다.
    }

    // 방문차량 월 집계 이벤트를 받아 visitor_usage_snapshot을 upsert 하는 준비 메서드이다.
    public void upsertVisitorUsageSnapshot(VisitorUsageEventPayload payload) {
        // TODO visitor_usage event 계약이 확정되면 household-service 스냅샷 저장 로직을 연결한다.
    }
}
