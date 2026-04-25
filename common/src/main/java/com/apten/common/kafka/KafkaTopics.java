package com.apten.common.kafka;

// 도메인별 Kafka topic 이름을 한 곳에서 관리한다
public final class KafkaTopics {

    // user 캐시 동기화 topic
    public static final String USER = "user.v1";

    // apartment complex 캐시 동기화 topic
    public static final String APARTMENT_COMPLEX = "apartment-complex.v1";

    // 단지 기본 정책 캐시 동기화 topic
    public static final String COMPLEX_POLICY = "complex-policy.v1";

    // 차량 정책 캐시 동기화 topic
    public static final String VEHICLE_POLICY = "vehicle-policy.v1";

    // 시설 정책 캐시 동기화 topic
    public static final String FACILITY_POLICY = "facility-policy.v1";

    // 방문차량 정책 캐시 동기화 topic
    public static final String VISITOR_POLICY = "visitor-policy.v1";

    // household 캐시 동기화 topic
    public static final String HOUSEHOLD = "household.v1";

    // household member 캐시 동기화 topic
    public static final String HOUSEHOLD_MEMBER = "household-member.v1";

    private KafkaTopics() {
    }
}
