package com.apten.facilityreservation.domain.repository;

import com.apten.facilityreservation.domain.entity.GxProgram;
import com.apten.facilityreservation.domain.enums.GxProgramStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

// GX 프로그램 저장소이다.
public interface GxProgramRepository extends JpaRepository<GxProgram, Long> {

    // 단지와 상태 기준 GX 프로그램 목록을 조회한다.
    List<GxProgram> findByComplexIdAndStatus(Long complexId, GxProgramStatus status);
}
