package com.apten.common.kafka;

// 캐시 동기화에서 사용하는 공통 이벤트 타입 목록
public enum EventType {
    USER_CREATED, // user 생성 이벤트
    USER_UPDATED, // user 생성 이벤트
    USER_DEACTIVATED, // user 비활성화 이벤트

    APARTMENT_COMPLEX_CREATED, // 단지 생성 이벤트
    APARTMENT_COMPLEX_UPDATED, // 단지 수정 이벤트
    APARTMENT_COMPLEX_DEACTIVATED, // 단지 비활성화 이벤트
    COMPLEX_POLICY_UPDATED, // 단지 기본 정책 수정 이벤트
    VEHICLE_POLICY_UPDATED, // 차량 정책 수정 이벤트
    FACILITY_POLICY_UPDATED, // 시설 정책 수정 이벤트
    VISITOR_POLICY_UPDATED, // 방문차량 정책 수정 이벤트

    HOUSEHOLD_CREATED, // 세대 생성 이벤트
    HOUSEHOLD_UPDATED, // 세대 수정 이벤트
    HOUSEHOLD_DEACTIVATED, // 세대 비활성화 이벤트
    HOUSEHOLD_MEMBER_CREATED, // 세대원 생성 이벤트
    HOUSEHOLD_MEMBER_UPDATED, // 세대원 수정 이벤트
    HOUSEHOLD_MEMBER_REMOVED // 세대원 제거 이벤트
}
