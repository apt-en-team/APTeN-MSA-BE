package com.apten.board.application.service;

import com.apten.board.domain.entity.ApartmentComplexCache;
import com.apten.board.domain.entity.HouseholdCache;
import com.apten.board.domain.entity.HouseholdMemberCache;
import com.apten.board.domain.entity.UserCache;
import com.apten.board.domain.repository.ApartmentComplexCacheRepository;
import com.apten.board.domain.repository.HouseholdCacheRepository;
import com.apten.board.domain.repository.HouseholdMemberCacheRepository;
import com.apten.board.domain.repository.UserCacheRepository;
import com.apten.common.exception.BusinessException;
import com.apten.common.kafka.payload.ApartmentComplexEventPayload;
import com.apten.common.kafka.payload.HouseholdEventPayload;
import com.apten.common.kafka.payload.HouseholdMemberEventPayload;
import com.apten.common.kafka.payload.UserEventPayload;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// board-service에서 참조 캐시 테이블 upsert를 담당하는 서비스
// listener는 이벤트 수신만 맡고 실제 저장은 이 계층에서 처리한다
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class BoardReferenceCacheService {

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
                .orElseGet(() -> UserCache.builder().id(payload.getUserId()).build());
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
        // 세대원 이벤트에는 complexId가 없으므로 세대 캐시에서 먼저 단지 ID를 찾는다.
        HouseholdCache householdCache = householdCacheRepository.findById(payload.getHouseholdId())
                .orElseThrow(() -> new BusinessException(com.apten.board.exception.BoardErrorCode.INVALID_PARAMETER));

        HouseholdMemberCache householdMemberCache = householdMemberCacheRepository.findById(payload.getHouseholdMemberId())
                .orElseGet(() -> HouseholdMemberCache.builder().id(payload.getHouseholdMemberId()).build());
        householdMemberCache.apply(payload, householdCache.getApartmentComplexId());
        householdMemberCacheRepository.save(householdMemberCache);
    }
}
