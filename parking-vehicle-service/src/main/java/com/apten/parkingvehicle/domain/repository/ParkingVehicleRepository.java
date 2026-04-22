package com.apten.parkingvehicle.domain.repository;

import com.apten.parkingvehicle.domain.entity.ParkingVehicle;
import org.springframework.data.jpa.repository.JpaRepository;

// parking-vehicle-service의 저장과 단순 조회를 맡는 JPA Repository
// 복잡 조회는 infrastructure/mapper의 MyBatis 인터페이스로 분리하는 기준을 따른다
public interface ParkingVehicleRepository extends JpaRepository<ParkingVehicle, Long> {
}
