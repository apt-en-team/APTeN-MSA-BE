package com.apten.parkingvehicle.application.model.response;

import lombok.Builder;
import lombok.Getter;

// 입출차 기록 화면 상단 통계 카드 4장에 표시할 요약 응답 DTO이다.
@Getter
@Builder
public class ParkingLogSummaryRes {

    // 오늘 입차 건수
    private long todayInCount;

    // 오늘 출차 건수
    private long todayOutCount;

    // 어제 대비 오늘 입차 차이 (양수면 증가, 음수면 감소)
    private long todayInDiffFromYesterday;

    // 어제 대비 오늘 출차 차이
    private long todayOutDiffFromYesterday;

    // 현재 단지 내 입차 중인 미등록 차량 수
    private long unregisteredCount;

    // 이번 달 전체 입출차 건수 (입차와 출차 합산)
    private long monthlyTotalCount;

    // 이번 달 일 평균 (오늘 일자 기준 반올림)
    private long monthlyDailyAverage;
}