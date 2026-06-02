package com.apten.apartmentcomplex.application.service;

import com.apten.apartmentcomplex.domain.entity.UserCache;
import com.apten.apartmentcomplex.domain.enums.UserCacheRole;
import com.apten.apartmentcomplex.domain.enums.UserCacheStatus;
import com.apten.apartmentcomplex.domain.repository.UserCacheRepository;
import com.apten.common.kafka.EventType;
import com.apten.common.kafka.payload.UserEventPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 사용자 참조 캐시 동기화
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ApartmentComplexReferenceCacheService {

    private final UserCacheRepository userCacheRepository;

    // 사용자 캐시 저장/갱신
    public void upsertUserCache(UserEventPayload payload) {
        // 기존 사용자 캐시 재사용
        UserCache userCache = userCacheRepository.findById(payload.getUserId())
                .orElseGet(() -> UserCache.builder().id(payload.getUserId()).build());

        // 관리자 검증용 사용자 정보 반영
        userCache.apply(
                payload.getComplexId() != null ? payload.getComplexId() : payload.getApartmentComplexId(),
                payload.getName(),
                resolveRole(payload.getRole()),
                resolveStatus(payload.getStatus()),
                resolveIsDeleted(payload)
        );
        userCacheRepository.save(userCache);
    }

    // 사용자 캐시 삭제 상태 반영
    public void markUserCacheDeleted(UserEventPayload payload) {
        // 삭제 이벤트 누락 방지용 캐시 생성
        UserCache userCache = userCacheRepository.findById(payload.getUserId())
                .orElseGet(() -> UserCache.builder().id(payload.getUserId()).build());

        // 관리자 지정 제외 상태 반영
        userCache.apply(
                payload.getComplexId() != null ? payload.getComplexId() : payload.getApartmentComplexId(),
                payload.getName(),
                resolveRole(payload.getRole()),
                UserCacheStatus.DELETED,
                true
        );
        userCacheRepository.save(userCache);
    }

    // 사용자 이벤트 유형 분기
    public void handleUserEvent(EventType eventType, UserEventPayload payload) {
        // 생성/수정 이벤트 처리
        if (eventType == EventType.USER_CREATED || eventType == EventType.USER_UPDATED) {
            upsertUserCache(payload);
            return;
        }

        // 비활성화/삭제 이벤트 처리
        if (eventType == EventType.USER_DEACTIVATED || eventType == EventType.USER_DELETED) {
            markUserCacheDeleted(payload);
            return;
        }

        // 미지원 이벤트 로그
        log.warn("Skipped unsupported user event. eventType={}, userId={}", eventType, payload.getUserId());
    }

    // 사용자 권한 변환
    private UserCacheRole resolveRole(String role) {
        return UserCacheRole.valueOf(role);
    }

    // 사용자 상태 변환
    private UserCacheStatus resolveStatus(String status) {
        return UserCacheStatus.valueOf(status);
    }

    // 사용자 삭제 여부 판별
    private boolean resolveIsDeleted(UserEventPayload payload) {
        if (payload.getIsDeleted() != null) {
            return payload.getIsDeleted();
        }
        return UserCacheStatus.DELETED.name().equals(payload.getStatus());
    }
}
