package com.apten.facilityreservation.domain.repository;

import com.apten.facilityreservation.domain.entity.FacilitySubscription;
import com.apten.facilityreservation.domain.enums.FacilitySubscriptionStatus;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

// 시설 구독 저장/조회 Repository
public interface FacilitySubscriptionRepository extends JpaRepository<FacilitySubscription, Long> {

    // 개인 구독 중복 확인 (userId 기준)
    boolean existsByUserIdAndFacilityIdAndStatus(
            Long userId,
            Long facilityId,
            FacilitySubscriptionStatus status
    );

    // 개인 활성 구독 조회 (해지 시 사용)
    Optional<FacilitySubscription> findByUserIdAndFacilityIdAndStatus(
            Long userId,
            Long facilityId,
            FacilitySubscriptionStatus status
    );

    // 입주민 나의 구독 목록 조회 (userId 기준, 최근 구독순)
    List<FacilitySubscription> findByUserIdAndComplexIdOrderBySubscribedAtDesc(
            Long userId,
            Long complexId
    );

    // 최근 해지 구독 조회 (유예기간 판단, userId 기준)
    Optional<FacilitySubscription> findTopByUserIdAndFacilityIdAndStatusOrderByCancelledAtDesc(
            Long userId,
            Long facilityId,
            FacilitySubscriptionStatus status
    );

    // 관리자 - 세대 구독 전체 조회 (세대 상세/요금 집계용)
    List<FacilitySubscription> findByHouseholdIdAndComplexIdOrderBySubscribedAtDesc(
            Long householdId,
            Long complexId
    );

    // 관리자 시설 구독 목록 조회 (최근 구독순)
    List<FacilitySubscription> findByComplexIdOrderBySubscribedAtDesc(Long complexId);

    // 관리자 시설 구독 목록 조회 (시설 필터)
    List<FacilitySubscription> findByComplexIdAndFacilityIdOrderBySubscribedAtDesc(
            Long complexId,
            Long facilityId
    );

    // 관리자 시설 구독 목록 조회 (상태 필터)
    List<FacilitySubscription> findByComplexIdAndStatusOrderBySubscribedAtDesc(
            Long complexId,
            FacilitySubscriptionStatus status
    );

    // 월 청구 대상 구독 조회 — 문자열 'ACTIVE' 대신 enum 파라미터로 전달해야 CodeConverter가 '01'로 변환됨
    @Query("""
        SELECT s FROM FacilitySubscription s
        WHERE s.complexId = :complexId
          AND s.status = :status
          AND s.subscribedAt <= :monthEnd
          AND (s.cancelledAt IS NULL OR s.cancelledAt >= :monthStart)
        """)
    List<FacilitySubscription> findBillableForMonth(
            @Param("complexId") Long complexId,
            @Param("monthStart") LocalDate monthStart,
            @Param("monthEnd") LocalDate monthEnd,
            @Param("status") FacilitySubscriptionStatus status
    );
}
