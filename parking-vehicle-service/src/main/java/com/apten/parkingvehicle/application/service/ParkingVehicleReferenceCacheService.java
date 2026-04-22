package com.apten.parkingvehicle.application.service;

import com.apten.common.kafka.payload.HouseholdEventPayload;
import com.apten.common.kafka.payload.UserEventPayload;
import com.apten.parkingvehicle.domain.entity.HouseholdCache;
import com.apten.parkingvehicle.domain.entity.UserCache;
import com.apten.parkingvehicle.domain.repository.HouseholdCacheRepository;
import com.apten.parkingvehicle.domain.repository.UserCacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// parking-vehicle-service에서 참조 캐시 테이블 upsert를 담당하는 서비스
@Service
@Transactional
@RequiredArgsConstructor
public class ParkingVehicleReferenceCacheService {

    // user cache 저장소
    private final UserCacheRepository userCacheRepository;

    // household cache 저장소
    private final HouseholdCacheRepository householdCacheRepository;

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
}
