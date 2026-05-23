package com.apten.facilityreservation.scheduler;

import com.apten.facilityreservation.application.model.request.FacilityFeeCalculateReq;
import com.apten.facilityreservation.application.model.request.FacilityFeePublishReq;
import com.apten.facilityreservation.application.service.FacilityFeeService;
import java.time.YearMonth;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

// 매월 2일 새벽 2시에 전월 시설 이용 비용을 자동 산정하고 Household Service로 발행한다.
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
        prefix = "apten.scheduler.facility-fee",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class FacilityFeeScheduler {

    private final FacilityFeeService facilityFeeService;

    // 매월 2일 새벽 2시 실행 — 전월 비용 산정 후 즉시 발행한다.
    @Scheduled(cron = "0 0 2 2 * ?")
    public void calculateAndPublishPreviousMonthFees() {
        YearMonth previousMonth = YearMonth.now().minusMonths(1);

        FacilityFeeCalculateReq calculateReq = FacilityFeeCalculateReq.builder()
                .usageYear(previousMonth.getYear())
                .usageMonth(previousMonth.getMonthValue())
                .build();
        facilityFeeService.calculateFacilityFees(calculateReq);

        FacilityFeePublishReq publishReq = FacilityFeePublishReq.builder()
                .usageYear(previousMonth.getYear())
                .usageMonth(previousMonth.getMonthValue())
                .build();
        facilityFeeService.publishFacilityFees(publishReq);
    }
}
