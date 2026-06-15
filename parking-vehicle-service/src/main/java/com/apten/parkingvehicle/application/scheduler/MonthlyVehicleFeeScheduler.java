package com.apten.parkingvehicle.application.scheduler;

import com.apten.parkingvehicle.application.model.request.VehicleFeeCalculateReq;
import com.apten.parkingvehicle.application.service.ParkingFeeCalculationService;
import com.apten.parkingvehicle.domain.entity.ComplexCache;
import com.apten.parkingvehicle.domain.repository.ComplexCacheRepository;
import java.time.YearMonth;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

// 매월 2일 새벽 2시에 모든 단지 직전 월 입주민 차량 정액 비용을 산정하는 스케줄러이다.
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "apten.scheduler.monthly-vehicle-fee.enabled", havingValue = "true", matchIfMissing = true)
public class MonthlyVehicleFeeScheduler {

    // 단지 캐시 저장소
    private final ComplexCacheRepository complexCacheRepository;

    // 차량 비용 산정 응용 서비스
    private final ParkingFeeCalculationService parkingFeeCalculationService;

    // 직전 월 차량 비용 산정을 단지 단위 격리 실행
    @Scheduled(cron = "0 0 2 2 * *")
    @SchedulerLock(name = "monthly-vehicle-fee", lockAtMostFor = "30m", lockAtLeastFor = "1m")
    public void runMonthlyCalculation() {
        // 직전 월 계산
        YearMonth previousMonth = YearMonth.now().minusMonths(1);
        int billYear = previousMonth.getYear();
        int billMonth = previousMonth.getMonthValue();

        // 단지 목록 일괄 조회
        List<ComplexCache> complexes = complexCacheRepository.findAll();

        int successCount = 0;
        int failureCount = 0;

        // 단지별 산정 호출 — 한 단지 실패가 다른 단지 산정을 막지 않도록 격리
        for (ComplexCache complex : complexes) {
            try {
                parkingFeeCalculationService.calculateVehicleFees(
                        VehicleFeeCalculateReq.builder()
                                .complexId(complex.getId())
                                .billYear(billYear)
                                .billMonth(billMonth)
                                .build()
                );
                successCount++;
            } catch (Exception ex) {
                failureCount++;
                log.warn("[monthly-vehicle-fee] calculation failed for complexId={}, reason={}",
                        complex.getId(), ex.getMessage());
            }
        }

        log.info("[monthly-vehicle-fee] period={}-{}, success={}, failure={}",
                billYear, billMonth, successCount, failureCount);
    }
}
