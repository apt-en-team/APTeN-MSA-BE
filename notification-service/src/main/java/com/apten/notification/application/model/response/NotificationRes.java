package com.apten.notification.application.model.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 알림 목록 단건 응답 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRes {
    // 읽음 처리와 상세 이동에 사용하는 DB 알림 ID
    private Long notificationId;
    // 알림 제목
    private String title;
    // 알림 본문
    private String content;
    // NotificationType enum 이름
    private String type;
    // NotificationType DB 저장 코드
    private String typeCode;
    // NotificationType 화면 표시값
    private String typeValue;
    // NotificationTargetType enum 이름
    private String targetType;
    // NotificationTargetType DB 저장 코드
    private String targetTypeCode;
    // NotificationTargetType 화면 표시값
    private String targetTypeValue;
    // 알림과 연결된 도메인 대상 ID
    private Long targetId;
    // 알림 클릭 시 이동할 프론트 경로
    private String linkPath;
    // 읽음 여부
    private Boolean isRead;
    // 읽음 처리 시각
    private LocalDateTime readAt;
    // 알림 생성 시각
    private LocalDateTime createdAt;
}
