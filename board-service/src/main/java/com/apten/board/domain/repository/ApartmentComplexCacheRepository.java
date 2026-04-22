package com.apten.board.domain.repository;

import com.apten.board.domain.entity.ApartmentComplexCache;
import org.springframework.data.jpa.repository.JpaRepository;

// board-service의 apartment complex cache 저장소
public interface ApartmentComplexCacheRepository extends JpaRepository<ApartmentComplexCache, Long> {
}
