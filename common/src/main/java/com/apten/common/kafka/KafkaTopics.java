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

    // 차량 스냅샷 동기화 topic
    public static final String VEHICLE = "vehicle.v1";

    // 시설 이용 스냅샷 동기화 topic
    public static final String FACILITY_USAGE = "facility-usage.v1";

    // 방문차량 월 집계 스냅샷 동기화 topic
    public static final String VISITOR_USAGE = "visitor-usage.v1";

    // 시설 이용 비용 산정 이벤트 topic
    public static final String FACILITY_FEE_CALCULATED = "facility.fee.calculated";

    // 차량 상태 변경 이벤트 topic (등록 승인/거절 결과)
    public static final String VEHICLE_STATUS_CHANGED = "vehicle.status.changed";

    // board-service 도메인 이벤트 topic
    public static final String BOARD_NOTICE_CREATED = "notice.created";
    public static final String BOARD_POST_CREATED = "post.created";
    public static final String BOARD_COMMENT_CREATED = "comment.created";
    public static final String BOARD_VOTE_CREATED = "vote.created";
    public static final String BOARD_VOTE_CLOSED = "vote.closed";

    // 서비스 → notification-service 알림 생성 요청 topic (전 서비스 공용)
    public static final String NOTIFICATION_REQUEST = "notification-request.v1";

    private KafkaTopics() {
    }
}
