package com.apten.facilityreservation.application.service;

import com.apten.facilityreservation.application.model.response.AdminReservationStatsRes;
import com.apten.facilityreservation.domain.enums.GxReservationStatus;
import com.apten.facilityreservation.domain.enums.ReservationStatus;
import com.apten.facilityreservation.domain.repository.GxReservationRepository;
import com.apten.facilityreservation.domain.repository.ReservationRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 관리자 예약 통계 서비스이다.
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminReservationStatsService {

    private final ReservationRepository reservationRepository;
    private final GxReservationRepository gxReservationRepository;

    public AdminReservationStatsRes getStats(Long complexId) {
        LocalDate today = LocalDate.now();
        YearMonth thisMonth = YearMonth.now();
        LocalDateTime monthStart = thisMonth.atDay(1).atStartOfDay();
        LocalDateTime monthEnd = thisMonth.atEndOfMonth().atTime(23, 59, 59);

        long todayTotal = reservationRepository.countByComplexIdAndReservationDate(complexId, today);

        long facilityConfirmed = reservationRepository.countByComplexIdAndStatus(complexId, ReservationStatus.CONFIRMED);
        long gxConfirmed = gxReservationRepository.countByComplexIdAndStatus(complexId, GxReservationStatus.CONFIRMED);
        long gxWaiting = gxReservationRepository.countByComplexIdAndStatus(complexId, GxReservationStatus.WAITING);
        long activeCount = facilityConfirmed + gxConfirmed + gxWaiting;

        long facilityCancelled = reservationRepository.countByComplexIdAndStatus(complexId, ReservationStatus.CANCELLED);
        long gxCancelled = gxReservationRepository.countByComplexIdAndStatus(complexId, GxReservationStatus.CANCELLED);
        long cancelledCount = facilityCancelled + gxCancelled;

        long facilityMonthly = reservationRepository.countByComplexIdAndCreatedAtBetween(complexId, monthStart, monthEnd);
        long gxMonthly = gxReservationRepository.countByComplexIdAndCreatedAtBetween(complexId, monthStart, monthEnd);
        long monthlyTotal = facilityMonthly + gxMonthly;

        return AdminReservationStatsRes.builder()
                .todayTotal(todayTotal)
                .activeCount(activeCount)
                .cancelledCount(cancelledCount)
                .monthlyTotal(monthlyTotal)
                .build();
    }
}
