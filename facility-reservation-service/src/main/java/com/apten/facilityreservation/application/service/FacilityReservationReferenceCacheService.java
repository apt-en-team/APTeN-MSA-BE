package com.apten.facilityreservation.application.service;

import com.apten.common.kafka.payload.ApartmentComplexEventPayload;
import com.apten.common.kafka.payload.HouseholdEventPayload;
import com.apten.common.kafka.payload.HouseholdMemberEventPayload;
import com.apten.common.kafka.payload.UserEventPayload;
import com.apten.facilityreservation.domain.entity.ApartmentComplexCache;
import com.apten.facilityreservation.domain.entity.HouseholdCache;
import com.apten.facilityreservation.domain.entity.HouseholdMemberCache;
import com.apten.facilityreservation.domain.entity.UserCache;
import com.apten.facilityreservation.domain.repository.ApartmentComplexCacheRepository;
import com.apten.facilityreservation.domain.repository.HouseholdCacheRepository;
import com.apten.facilityreservation.domain.repository.HouseholdMemberCacheRepository;
import com.apten.facilityreservation.domain.repository.UserCacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// facility-reservation-service에서 참조 캐시 테이블 upsert를 담당하는 서비스
@Service
@Transactional
@RequiredArgsConstructor
public class FacilityReservationReferenceCacheService {

    // user cache 저장소
    private final UserCacheRepository userCacheRepository;

    // apartment complex cache 저장소
    private final ApartmentComplexCacheRepository apartmentComplexCacheRepository;

    // household cache 저장소
    private final HouseholdCacheRepository householdCacheRepository;

    // household member cache 저장소
    private final HouseholdMemberCacheRepository householdMemberCacheRepository;

    // 동일 PK가 있으면 갱신하고 없으면 생성한다
    public void upsertUserCache(UserEventPayload payload) {
        UserCache userCache = userCacheRepository.findById(payload.getUserId())
                .orElseGet(() -> UserCache.builder().userId(payload.getUserId()).build());
        userCache.apply(payload);
        userCacheRepository.save(userCache);
    }

    // 동일 PK가 있으면 갱신하고 없으면 생성한다
    public void upsertApartmentComplexCache(ApartmentComplexEventPayload payload) {
        ApartmentComplexCache apartmentComplexCache = apartmentComplexCacheRepository.findById(payload.getApartmentComplexId())
                .orElseGet(() -> ApartmentComplexCache.builder().apartmentComplexId(payload.getApartmentComplexId()).build());
        apartmentComplexCache.apply(payload);
        apartmentComplexCacheRepository.save(apartmentComplexCache);
    }

    // 동일 PK가 있으면 갱신하고 없으면 생성한다
    public void upsertHouseholdCache(HouseholdEventPayload payload) {
        HouseholdCache householdCache = householdCacheRepository.findById(payload.getHouseholdId())
                .orElseGet(() -> HouseholdCache.builder().householdId(payload.getHouseholdId()).build());
        householdCache.apply(payload);
        householdCacheRepository.save(householdCache);
    }

    // 제거 이벤트도 물리 삭제 대신 상태값을 포함한 upsert로 반영한다
    public void upsertHouseholdMemberCache(HouseholdMemberEventPayload payload) {
        HouseholdMemberCache householdMemberCache = householdMemberCacheRepository.findById(payload.getHouseholdMemberId())
                .orElseGet(() -> HouseholdMemberCache.builder().householdMemberId(payload.getHouseholdMemberId()).build());
        householdMemberCache.apply(payload);
        householdMemberCacheRepository.save(householdMemberCache);
    }
}
