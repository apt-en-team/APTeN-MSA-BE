package com.apten.facilityreservation.infrastructure.client.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// notification-service /internal/notifications 요청 DTO이다
// notification-service DB 구조를 직접 참조하지 않고 API 계약 필드만 들고 간다
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationCreateReq {

    // 알림을 받을 사용자 ID
    private Long receiverUserId;

    // 수신 사용자 소속 단지 ID
    private Long complexId;

    // NotificationType enum 이름, 예: FACILITY_RESERVED
    private String type;

    // NotificationTargetType enum 이름, 예: FACILITY_RESERVATION
    private String targetType;

    // 알림과 연결된 예약/프로그램 ID이며 Long TSID 기준이다
    private Long targetId;

    // 알림 제목
    private String title;

    // 알림 본문
    private String content;

    // 알림 클릭 시 이동할 프론트 경로
    private String linkPath;

    // 부가 정보를 문자열 JSON으로 전달하며 필수 데이터는 아니다
    private String payloadJson;
}
