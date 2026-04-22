package com.apten.notification.domain.repository;

import com.apten.notification.domain.entity.ApartmentComplexCache;
import org.springframework.data.jpa.repository.JpaRepository;

// notification-service의 apartment complex cache 저장소
public interface ApartmentComplexCacheRepository extends JpaRepository<ApartmentComplexCache, Long> {
}
