package com.apten.common.kafka;

// 도메인별 Kafka topic 이름을 한 곳에서 관리한다.
// 정책 원본은 각 도메인 서비스 내부에서 직접 관리하므로 캐시 동기화 topic은 남기지 않는다.
public final class KafkaTopics {

    // user 캐시 동기화 topic
    public static final String USER = "user.v1";

    // apartment complex 캐시 동기화 topic
    public static final String APARTMENT_COMPLEX = "apartment-complex.v1";

    // household 캐시 동기화 topic
    public static final String HOUSEHOLD = "household.v1";

    // household member 캐시 동기화 topic
    public static final String HOUSEHOLD_MEMBER = "household-member.v1";

    private KafkaTopics() {
    }
}
