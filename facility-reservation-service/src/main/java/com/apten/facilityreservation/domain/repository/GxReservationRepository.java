package com.apten.facilityreservation.domain.repository;

import com.apten.facilityreservation.domain.entity.GxReservation;
import com.apten.facilityreservation.domain.enums.GxReservationStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

// GX 예약 저장소이다.
public interface GxReservationRepository extends JpaRepository<GxReservation, Long> {

    // 같은 사용자 중복 신청 여부를 확인한다.
    boolean existsByProgramIdAndUserId(Long programId, Long userId);

    // 상태 기준 예약 수를 센다.
    long countByProgramIdAndStatus(Long programId, GxReservationStatus status);

    // 대기 순번 기준으로 예약 목록을 조회한다.
    List<GxReservation> findByProgramIdAndStatusOrderByWaitNoAsc(Long programId, GxReservationStatus status);
}
