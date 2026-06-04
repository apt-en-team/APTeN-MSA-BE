package com.apten.household.domain.repository;

import com.apten.household.domain.entity.UserCache;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

// 사용자 캐시 저장소이다.
public interface UserCacheRepository extends JpaRepository<UserCache, Long> {

    @Query("SELECT u FROM UserCache u WHERE u.name LIKE CONCAT('%', :name, '%')")
    List<UserCache> findByNameContaining(@Param("name") String name);
}
