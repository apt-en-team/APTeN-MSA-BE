package com.apten.facilityreservation.domain.repository;

import com.apten.facilityreservation.domain.entity.GxReservation;
import com.apten.facilityreservation.domain.enums.GxReservationStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

// GX 예약 저장소이다.
public interface GxReservationRepository extends JpaRepository<GxReservation, Long> {

    // 같은 사용자 중복 신청 여부를 확인한다.
    boolean existsByProgramIdAndUserId(Long programId, Long userId);

    // 상태 기준 예약 수를 센다.
    long countByProgramIdAndStatus(Long programId, GxReservationStatus status);

    // 프로그램과 상태 기준 예약 목록을 조회한다.
    List<GxReservation> findByProgramIdAndStatus(Long programId, GxReservationStatus status);

    // 대기 순번 기준으로 예약 목록을 조회한다.
    List<GxReservation> findByProgramIdAndStatusOrderByWaitNoAsc(Long programId, GxReservationStatus status);

    // 사용자의 GX 예약을 조회한다.
    Optional<GxReservation> findByProgramIdAndUserId(Long programId, Long userId);

    // userId와 complexId 기준 소유권+단지 범위를 동시에 검증하는 단건 조회이다.
    Optional<GxReservation> findByIdAndUserIdAndComplexId(Long id, Long userId, Long complexId);

    // 관리자 단지 범위 단건 조회이다.
    Optional<GxReservation> findByIdAndComplexId(Long id, Long complexId);

    // 사용자의 단지 내 전체 GX 예약 목록을 조회한다.
    List<GxReservation> findByUserIdAndComplexId(Long userId, Long complexId);

    // GX는 승인 시점이 실제 과금 확정 시점에 가장 가깝기 때문에 approvedAt 기준으로 조회한다.
    List<GxReservation> findByStatusAndApprovedAtBetween(
            GxReservationStatus status,
            LocalDateTime fromDateTime,
            LocalDateTime toDateTime
    );

    // 복수 프로그램의 상태별 예약 수를 집계한다. (N+1 방지)
    @Query("SELECT r.programId, COUNT(r) FROM GxReservation r WHERE r.programId IN :programIds AND r.status = :status GROUP BY r.programId")
    List<Object[]> countByProgramIdInAndStatus(@Param("programIds") List<Long> programIds, @Param("status") GxReservationStatus status);
}
