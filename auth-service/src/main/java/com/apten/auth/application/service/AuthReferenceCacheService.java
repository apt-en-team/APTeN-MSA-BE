package com.apten.auth.application.service;

import com.apten.auth.domain.entity.ComplexCache;
import com.apten.auth.domain.enums.ComplexCacheStatus;
import com.apten.auth.domain.repository.ComplexCacheRepository;
import com.apten.common.kafka.payload.ApartmentComplexEventPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// auth-service에서 단지 캐시를 upsert 하는 서비스이다.
@Service
@Transactional
@RequiredArgsConstructor
public class AuthReferenceCacheService {

    // 단지 캐시 저장소이다.
    private final ComplexCacheRepository complexCacheRepository;

    // 단지 이벤트 payload를 받아 complex_cache를 upsert 한다.
    public void upsertComplexCache(ApartmentComplexEventPayload payload) {
        //수정 단지 원본은 소프트 삭제하지만 Auth 캐시는 삭제 이벤트 수신 시 제거한다.
        if ("DELETED".equals(payload.getStatus())) {
            complexCacheRepository.deleteById(payload.getApartmentComplexId());
            return;
        }

        // 기존 캐시가 있으면 갱신하고 없으면 새로 만든다.
        ComplexCache complexCache = complexCacheRepository.findById(payload.getApartmentComplexId())
                .orElseGet(() -> ComplexCache.builder()
                        .id(payload.getApartmentComplexId())
                        .build());

        // 단지 기본 정보를 캐시에 반영한다.
        complexCache.apply(
                payload.getName(),
                payload.getAddress(),
                ComplexCacheStatus.valueOf(payload.getStatus())
        );

        // 단지 캐시를 저장한다.
        complexCacheRepository.save(complexCache);
    }
}
