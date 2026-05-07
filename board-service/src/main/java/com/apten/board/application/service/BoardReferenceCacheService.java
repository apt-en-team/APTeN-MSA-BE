package com.apten.board.application.service;

import com.apten.board.domain.entity.ComplexFeatureCache;
import com.apten.board.domain.entity.ApartmentComplexCache;
import com.apten.board.domain.entity.HouseholdCache;
import com.apten.board.domain.entity.HouseholdMemberCache;
import com.apten.board.domain.entity.UserCache;
import com.apten.board.domain.repository.ComplexFeatureCacheRepository;
import com.apten.board.domain.repository.ApartmentComplexCacheRepository;
import com.apten.board.domain.repository.HouseholdCacheRepository;
import com.apten.board.domain.repository.HouseholdMemberCacheRepository;
import com.apten.board.domain.repository.UserCacheRepository;
import com.apten.common.exception.BusinessException;
import com.apten.common.enums.FeatureCode;
import com.apten.common.kafka.payload.ApartmentComplexEventPayload;
import com.apten.common.kafka.payload.HouseholdEventPayload;
import com.apten.common.kafka.payload.HouseholdMemberEventPayload;
import com.apten.common.kafka.payload.UserEventPayload;
import java.util.Map;
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

    // complex feature cache 저장소
    private final ComplexFeatureCacheRepository complexFeatureCacheRepository;

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
