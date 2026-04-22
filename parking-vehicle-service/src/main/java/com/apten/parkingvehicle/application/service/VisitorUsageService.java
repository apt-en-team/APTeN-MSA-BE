package com.apten.parkingvehicle.application.service;

import com.apten.parkingvehicle.application.model.request.VisitorUsageMonthlyAggregateReq;
import com.apten.parkingvehicle.application.model.response.VisitorUsageMonthlyAggregateRes;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

// 방문차량 월별 이용시간 집계와 발행 위치를 담당하는 응용 서비스
@Service
public class VisitorUsageService {

    public VisitorUsageMonthlyAggregateRes aggregateVisitorUsageMonthly(VisitorUsageMonthlyAggregateReq request) {
        // TODO: 방문차량 월별 이용시간 집계 구현
        // TODO: 방문차량 월별 이용시간 집계 및 이벤트 발행
        return VisitorUsageMonthlyAggregateRes.builder()
                .targetYear(request.getTargetYear())
                .targetMonth(request.getTargetMonth())
                .aggregatedHouseholdCount(0)
                .published(false)
                .executedAt(LocalDateTime.now())
                .build();
    }
}
