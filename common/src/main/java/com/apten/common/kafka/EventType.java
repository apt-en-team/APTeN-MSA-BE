package com.apten.common.kafka;

// 캐시 동기화와 도메인 이벤트 전달에 사용하는 공통 이벤트 타입 목록이다.
public enum EventType {
    USER_CREATED, // user 생성 이벤트
    USER_UPDATED, // user 수정 이벤트
    USER_DELETED, // user 삭제 이벤트
    USER_DEACTIVATED, // user 비활성화 이벤트

    APARTMENT_COMPLEX_CREATED, // 단지 생성 이벤트
    APARTMENT_COMPLEX_UPDATED, // 단지 수정 이벤트
    APARTMENT_COMPLEX_DEACTIVATED, // 단지 비활성화 이벤트

    HOUSEHOLD_CREATED, // 세대 생성 이벤트
    HOUSEHOLD_UPDATED, // 세대 수정 이벤트
    HOUSEHOLD_DELETED, // 세대 삭제 이벤트
    HOUSEHOLD_DEACTIVATED, // 세대 비활성화 이벤트
    HOUSEHOLD_MEMBER_CREATED, // 세대원 생성 이벤트
    HOUSEHOLD_MEMBER_UPDATED, // 세대원 수정 이벤트
    HOUSEHOLD_MEMBER_DELETED, // 세대원 삭제 이벤트
    HOUSEHOLD_MEMBER_REMOVED, // 세대원 제거 이벤트

    VEHICLE_APPROVED, // 차량 승인 스냅샷 이벤트
    VEHICLE_UPDATED, // 차량 수정 스냅샷 이벤트
    VEHICLE_DELETED, // 차량 삭제 스냅샷 이벤트

    FACILITY_USAGE_COMPLETED, // 시설 이용 완료 스냅샷 이벤트
    VISITOR_USAGE_SUMMARIZED // 방문차량 월 집계 스냅샷 이벤트
}
