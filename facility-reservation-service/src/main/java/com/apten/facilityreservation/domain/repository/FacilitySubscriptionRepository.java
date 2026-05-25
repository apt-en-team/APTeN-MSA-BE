package com.apten.facilityreservation.domain.repository;

import com.apten.facilityreservation.domain.entity.FacilitySubscription;
import com.apten.facilityreservation.domain.enums.FacilitySubscriptionStatus;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

// 시설 이용 구독 저장소이다.
public interface FacilitySubscriptionRepository extends JpaRepository<FacilitySubscription, Long> {

    // 세대-시설 기준 중복 구독 여부를 확인한다.
    boolean existsByHouseholdIdAndFacilityIdAndStatus(
            Long householdId,
            Long facilityId,
            FacilitySubscriptionStatus status
    );

    // 세대-시설 기준 활성 구독 단건을 조회한다.
    Optional<FacilitySubscription> findByHouseholdIdAndFacilityIdAndStatus(
            Long householdId,
            Long facilityId,
            FacilitySubscriptionStatus status
    );

    // 입주민의 단지 내 전체 구독 목록을 구독 시작일 내림차순으로 조회한다.
    List<FacilitySubscription> findByHouseholdIdAndComplexIdOrderBySubscribedAtDesc(
            Long householdId,
            Long complexId
    );

    // 세대-시설의 가장 최근 해지된 구독을 조회한다. (유예기간 판단용)
    Optional<FacilitySubscription> findTopByHouseholdIdAndFacilityIdAndStatusOrderByCancelledAtDesc(
            Long householdId,
            Long facilityId,
            FacilitySubscriptionStatus status
    );

    // 관리자 단지 내 전체 구독 목록을 구독 시작일 내림차순으로 조회한다.
    List<FacilitySubscription> findByComplexIdOrderBySubscribedAtDesc(Long complexId);

    // 관리자 단지·시설 필터 구독 목록을 조회한다.
    List<FacilitySubscription> findByComplexIdAndFacilityIdOrderBySubscribedAtDesc(
            Long complexId,
            Long facilityId
    );

    // 관리자 단지·상태 필터 구독 목록을 조회한다.
    List<FacilitySubscription> findByComplexIdAndStatusOrderBySubscribedAtDesc(
            Long complexId,
            FacilitySubscriptionStatus status
    );

    // 월 청구 대상 구독을 조회한다.
    // billingCutoffDay 기준으로 필터링은 서비스 레이어에서 처리한다.
    @Query("""
        SELECT s FROM FacilitySubscription s
        WHERE s.complexId = :complexId
          AND s.status = 'ACTIVE'
          AND s.subscribedAt <= :monthEnd
          AND (s.cancelledAt IS NULL OR s.cancelledAt >= :monthStart)
        """)
    List<FacilitySubscription> findBillableForMonth(
            @Param("complexId") Long complexId,
            @Param("monthStart") LocalDate monthStart,
            @Param("monthEnd") LocalDate monthEnd
    );
}
