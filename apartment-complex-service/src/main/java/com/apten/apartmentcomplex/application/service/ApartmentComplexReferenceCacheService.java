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

// apartment-complex-service에서 참조 캐시 테이블 upsert를 담당하는 서비스이다.
// Kafka listener는 수신만 맡고 실제 user_cache 반영은 이 계층이 처리한다.
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ApartmentComplexReferenceCacheService {

    // user_cache 저장과 갱신을 담당하는 repository이다.
    private final UserCacheRepository userCacheRepository;

    // 생성과 수정 이벤트는 동일한 upsert 규칙으로 user_cache를 반영한다.
    public void upsertUserCache(UserEventPayload payload) {
        // 같은 사용자 PK가 이미 있으면 갱신하고 없으면 새 row를 만든다.
        UserCache userCache = userCacheRepository.findById(payload.getUserId())
                .orElseGet(() -> UserCache.builder().id(payload.getUserId()).build());

        // payload 기준으로 관리자 검증에 필요한 최소 사용자 정보를 유지한다.
        userCache.apply(
                payload.getApartmentComplexId(),
                payload.getName(),
                resolveRole(payload.getRole()),
                resolveStatus(payload.getStatus()),
                isDeletedStatus(payload.getStatus())
        );
        userCacheRepository.save(userCache);
    }

    // 비활성화나 삭제성 이벤트는 status를 DELETED로 맞추고 isDeleted를 true로 반영한다.
    public void markUserCacheDeleted(UserEventPayload payload) {
        // 기존 row가 없더라도 삭제 상태 캐시를 만들어 이후 검증 결과를 일관되게 유지한다.
        UserCache userCache = userCacheRepository.findById(payload.getUserId())
                .orElseGet(() -> UserCache.builder().id(payload.getUserId()).build());

        // 삭제성 이벤트는 관리자 소속 지정 대상에서 제외되도록 DELETED 상태로 반영한다.
        userCache.apply(
                payload.getApartmentComplexId(),
                payload.getName(),
                resolveRole(payload.getRole()),
                UserCacheStatus.DELETED,
                true
        );
        userCacheRepository.save(userCache);
    }

    // eventType에 따라 upsert 또는 삭제 상태 반영을 분기한다.
    public void handleUserEvent(EventType eventType, UserEventPayload payload) {
        // 생성과 수정은 동일한 upsert 흐름으로 처리한다.
        if (eventType == EventType.USER_CREATED || eventType == EventType.USER_UPDATED) {
            upsertUserCache(payload);
            return;
        }

        // 비활성화 이벤트는 user_cache를 삭제 상태로 반영한다.
        if (eventType == EventType.USER_DEACTIVATED) {
            markUserCacheDeleted(payload);
            return;
        }

        // 현재 파일럿 범위 밖 이벤트가 들어오면 저장은 하지 않고 로그만 남긴다.
        log.warn("Skipped unsupported user event. eventType={}, userId={}", eventType, payload.getUserId());
    }

    // Kafka payload의 role 문자열을 apartment-complex-service 내부 enum으로 변환한다.
    private UserCacheRole resolveRole(String role) {
        return UserCacheRole.valueOf(role);
    }

    // Kafka payload의 status 문자열을 apartment-complex-service 내부 enum으로 변환한다.
    private UserCacheStatus resolveStatus(String status) {
        return UserCacheStatus.valueOf(status);
    }

    // status가 DELETED이면 isDeleted도 true로 맞춰 삭제성 상태를 명확하게 남긴다.
    private boolean isDeletedStatus(String status) {
        return UserCacheStatus.DELETED.name().equals(status);
    }
}
