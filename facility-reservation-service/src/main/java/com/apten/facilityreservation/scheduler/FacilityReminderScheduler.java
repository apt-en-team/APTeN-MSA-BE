package com.apten.facilityreservation.scheduler;

import com.apten.facilityreservation.application.service.FacilityNotificationService;
import com.apten.facilityreservation.domain.entity.Facility;
import com.apten.facilityreservation.domain.entity.Reservation;
import com.apten.facilityreservation.domain.enums.ReservationStatus;
import com.apten.facilityreservation.domain.repository.FacilityRepository;
import com.apten.facilityreservation.domain.repository.ReservationRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

// 예약 시작 1시간 전 입주민에게 리마인더 알림을 발송한다.
// 5분마다 실행하며 시작 55~65분 후 구간의 CONFIRMED 예약을 조회한다.
// 중복 발송 가능성이 있으나 리마인더 특성상 허용한다.
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
        prefix = "apten.scheduler.facility-reminder",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class FacilityReminderScheduler {

    private final ReservationRepository reservationRepository;
    private final FacilityRepository facilityRepository;
    private final FacilityNotificationService facilityNotificationService;

    @Scheduled(cron = "${apten.scheduler.facility-reminder.cron:0 */5 * * * *}")
    @SchedulerLock(name = "facility-reminder", lockAtMostFor = "4m", lockAtLeastFor = "30s")
    public void sendFacilityReminders() {
        LocalDate today = LocalDate.now();
        LocalTime from = LocalTime.now().plusMinutes(55);
        LocalTime to = LocalTime.now().plusMinutes(65);

        // 자정 전후 구간은 익일 예약 대상이 없으므로 생략한다
        if (from.isAfter(to)) {
            return;
        }

        List<Reservation> targets = reservationRepository
                .findByReservationDateAndStartTimeBetweenAndStatus(today, from, to, ReservationStatus.CONFIRMED);

        if (targets.isEmpty()) {
            return;
        }

        log.info("[FacilityReminder] 리마인더 대상 예약 수={}. from={}, to={}", targets.size(), from, to);

        // 시설명 batch 조회 (N+1 방지)
        List<Long> facilityIds = targets.stream().map(Reservation::getFacilityId).distinct().toList();
        Map<Long, String> facilityNameMap = facilityRepository.findAllById(facilityIds).stream()
                .collect(Collectors.toMap(Facility::getId, Facility::getName));

        for (Reservation reservation : targets) {
            String facilityName = facilityNameMap.getOrDefault(reservation.getFacilityId(), "시설");
            facilityNotificationService.notifyFacilityReminder(
                    reservation.getUserId(),
                    reservation.getComplexId(),
                    reservation.getId(),
                    facilityName
            );
        }
    }
}
