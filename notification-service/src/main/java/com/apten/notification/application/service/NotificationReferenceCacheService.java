package com.apten.notification.application.service;

import com.apten.common.kafka.payload.UserEventPayload;
import com.apten.notification.domain.entity.UserCache;
import com.apten.notification.domain.repository.UserCacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// notification-service에서 사용자 캐시를 upsert 하는 서비스이다.
@Service
@Transactional
@RequiredArgsConstructor
public class NotificationReferenceCacheService {

    // 사용자 캐시 저장소이다.
    private final UserCacheRepository userCacheRepository;

    // 사용자 이벤트 payload를 받아 user_cache를 upsert 한다.
    public void upsertUserCache(UserEventPayload payload) {
        // 기존 캐시가 있으면 갱신하고 없으면 새로 만든다.
        UserCache userCache = userCacheRepository.findById(payload.getUserId())
                .orElseGet(() -> UserCache.builder()
                        .id(payload.getUserId())
                        .build());

        // 사용자 공통 필드를 현재 캐시에 반영한다.
        userCache.apply(payload);

        // 사용자 캐시를 저장한다.
        userCacheRepository.save(userCache);
    }
}
