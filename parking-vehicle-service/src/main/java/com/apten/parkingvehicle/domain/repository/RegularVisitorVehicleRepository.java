package com.apten.parkingvehicle.domain.repository;

import com.apten.parkingvehicle.domain.entity.RegularVisitorVehicle;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

// 고정 방문차량 원본 테이블 접근을 담당하는 저장소이다.
public interface RegularVisitorVehicleRepository extends JpaRepository<RegularVisitorVehicle, Long> {

    // 사용자 기준 활성 고정 방문차량 목록을 조회한다.
    List<RegularVisitorVehicle> findByUserIdAndIsDeletedFalse(Long userId);

    // 고정 방문차량 식별자와 미삭제 조건으로 조회한다.
    Optional<RegularVisitorVehicle> findByIdAndIsDeletedFalse(Long id);
}
