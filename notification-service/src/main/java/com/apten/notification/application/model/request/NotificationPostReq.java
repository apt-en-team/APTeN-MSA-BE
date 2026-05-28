package com.apten.notification.application.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 내부 알림 생성 요청 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationPostReq {
    // 알림을 받을 사용자 ID
    private Long receiverUserId;
    // 수신 사용자 소속 단지 ID
    private Long complexId;
    // NotificationType name/code/value 중 하나
    private String type;
    // NotificationTargetType name/code/value 중 하나
    private String targetType;
    // 알림과 연결된 도메인 대상 ID
    private Long targetId;
    // 알림 제목
    private String title;
    // 알림 본문
    private String content;
    // 클릭 시 이동할 프론트 경로
    private String linkPath;
    // 도메인별 추가 정보를 문자열 JSON으로 보관한다
    private String payloadJson;
}
